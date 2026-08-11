import asyncio
import json
import os
import uuid
from datetime import datetime, timedelta
from pathlib import Path
from typing import Any, Dict, List, Optional

import tornado.escape
import tornado.httpclient
import tornado.ioloop
import tornado.web
import yaml


ROOT_DIR = Path(__file__).resolve().parent
CONFIG_FILE = Path(os.getenv("CONFIG_FILE", ROOT_DIR / "config" / "application.yaml"))


def substitute_env(value: Any) -> Any:
    if not isinstance(value, str):
        return value
    result = value
    while "${" in result and "}" in result:
        start = result.find("${")
        end = result.find("}", start)
        if end < 0:
            break
        body = result[start + 2:end]
        if ":" in body:
            name, fallback = body.split(":", 1)
        else:
            name, fallback = body, ""
        result = result[:start] + os.getenv(name, fallback) + result[end + 1 :]
    return result


def walk_env(data: Any) -> Any:
    if isinstance(data, dict):
        return {key: walk_env(value) for key, value in data.items()}
    if isinstance(data, list):
        return [walk_env(item) for item in data]
    return substitute_env(data)


def load_config() -> Dict[str, Any]:
    if CONFIG_FILE.exists():
        with CONFIG_FILE.open("r", encoding="utf-8") as fh:
            raw = yaml.safe_load(fh) or {}
    else:
        raw = {}
    raw = walk_env(raw)
    return {
        "app": raw.get("app", {}),
        "server": raw.get("server", {}),
        "nacos": raw.get("nacos", {}),
        "datasource": raw.get("datasource", {}),
        "storage": raw.get("storage", {}),
        "collector": raw.get("collector", {}),
        "astrbot": raw.get("astrbot", {}),
        "reporting": raw.get("reporting", {}),
        "elasticsearch": raw.get("elasticsearch", {}),
        "notification": raw.get("notification", {}),
    }


def ensure_dir(path: Path) -> None:
    path.mkdir(parents=True, exist_ok=True)


def resolve_path(raw_path: str, default: Path) -> Path:
    if not raw_path:
        return default
    path = Path(raw_path)
    if not path.is_absolute():
        path = (ROOT_DIR / path).resolve()
    return path


def collector_data_root(config: Dict[str, Any]) -> Path:
    raw_path = str(config.get("collector", {}).get("data-root", "")).strip()
    return resolve_path(raw_path, ROOT_DIR.parent / "cpo-feishu-collector-plugin" / "data")


def local_storage_root(config: Dict[str, Any]) -> Path:
    raw_path = str(config.get("storage", {}).get("local-data-root", "")).strip()
    return resolve_path(raw_path, ROOT_DIR / "data")


def event_storage_root(config: Dict[str, Any]) -> Path:
    raw_path = str(config.get("storage", {}).get("event-data-root", "")).strip()
    return resolve_path(raw_path, local_storage_root(config) / "event-store")


def raw_retention_days(config: Dict[str, Any]) -> int:
    return int(config.get("storage", {}).get("raw-retention-days", 15) or 15)


def summary_retention_days(config: Dict[str, Any]) -> int:
    return int(config.get("storage", {}).get("summary-retention-days", 180) or 180)


def default_user_id(config: Dict[str, Any]) -> str:
    return str(config.get("reporting", {}).get("default-user-id", "E0028517")).strip() or "E0028517"


def load_json_file(path: Path) -> Dict[str, Any]:
    with path.open("r", encoding="utf-8") as fh:
        data = json.load(fh)
    return data if isinstance(data, dict) else {}


def latest_collector_report_path(config: Dict[str, Any], user_id: str) -> Path:
    return collector_data_root(config) / user_id / "latest-report.json"


def load_latest_collector_report(config: Dict[str, Any], user_id: str) -> Dict[str, Any]:
    path = latest_collector_report_path(config, user_id)
    if not path.exists():
        raise FileNotFoundError(f"collector report not found: {path}")
    return load_json_file(path)


def summary_storage_paths(config: Dict[str, Any], user_id: str, generated_at: str) -> Dict[str, Path]:
    root = local_storage_root(config) / "ai-summary" / user_id / "daily"
    history_dir = root / "history"
    ensure_dir(history_dir)
    safe_name = generated_at.replace(":", "-").replace(" ", "-")
    return {
        "latest_json": root / "latest-summary.json",
        "latest_md": root / "latest-summary.md",
        "history_json": history_dir / f"{safe_name}.json",
        "history_md": history_dir / f"{safe_name}.md",
    }


def extract_plain_text_from_parts(parts: Any) -> str:
    texts = []
    if not isinstance(parts, list):
        return ""
    for part in parts:
        if not isinstance(part, dict):
            continue
        part_type = str(part.get("type", "")).strip()
        if part_type in {"plain", "text"}:
            text = part.get("text")
            if isinstance(text, str) and text:
                texts.append(text)
    return "".join(texts).strip()


def extract_astrbot_sse_text(body: str) -> str:
    text_chunks = []
    snapshot_text = ""
    error_message = ""

    for line in body.splitlines():
        if not line.startswith("data: "):
            continue
        payload_text = line[6:].strip()
        if not payload_text:
            continue
        try:
            payload = json.loads(payload_text)
        except json.JSONDecodeError:
            continue

        if not isinstance(payload, dict):
            continue

        payload_type = str(payload.get("type", "")).strip()
        if payload_type == "plain":
            data = payload.get("data")
            if isinstance(data, str) and data:
                text_chunks.append(data)
        elif payload_type == "run_snapshot":
            content = payload.get("data", {}).get("content", {})
            snapshot_text = extract_plain_text_from_parts(content.get("message"))
        elif payload_type == "error":
            data = payload.get("data")
            if isinstance(data, str) and data:
                error_message = data

    final_text = "".join(text_chunks).strip() or snapshot_text.strip()
    if final_text:
        return final_text
    if error_message:
        raise RuntimeError(error_message)
    raise RuntimeError("AstrBot returned an empty summary response")


def normalize_astrbot_chat_url(base_url: str) -> str:
    base = base_url.rstrip("/")
    if base.endswith("/api/v1"):
        return f"{base}/chat"
    return f"{base}/api/v1/chat"


def astrbot_status_payload(config: Dict[str, Any]) -> Dict[str, Any]:
    astrbot = config.get("astrbot", {})
    base_url = str(astrbot.get("base-url", "")).strip()
    api_key = str(astrbot.get("api-key", "")).strip()
    provider = str(astrbot.get("selected-provider", "deepseek")).strip() or "deepseek"
    model = str(astrbot.get("selected-model", "deepseek-v4-flash")).strip() or "deepseek-v4-flash"
    config_id = str(astrbot.get("config-id", "default")).strip() or "default"
    return {
        "configured": bool(base_url and api_key and api_key != "replace-me"),
        "baseUrl": base_url,
        "chatUrl": normalize_astrbot_chat_url(base_url) if base_url else "",
        "apiKeyConfigured": bool(api_key and api_key != "replace-me"),
        "provider": provider,
        "model": model,
        "configId": config_id,
        "summaryGenerationReady": bool(base_url and api_key and api_key != "replace-me"),
    }


def build_daily_summary_prompt(report: Dict[str, Any], focus: str) -> str:
    profile = report.get("profile", {})
    communication = report.get("communication", {})
    meeting = report.get("meeting", {})
    document = report.get("document", {})
    topics = report.get("topics", [])
    events = report.get("events", [])
    summaries = report.get("dailySummary", [])
    filter_rules = report.get("filterRules", {})
    summary_focus = focus.strip() if focus else "突出工作产出、协同推进、风险和下一步建议"

    compact_payload = {
        "profile": {
            "userId": profile.get("userId"),
            "displayName": profile.get("displayName"),
            "department": profile.get("department"),
            "title": profile.get("title"),
        },
        "range": report.get("range", {}),
        "communication": communication,
        "meeting": meeting,
        "document": document,
        "topics": topics,
        "events": events,
        "dailySummary": summaries,
        "filterRules": {
            "excludeBotInteractions": filter_rules.get("excludeBotInteractions"),
            "includeKeywords": filter_rules.get("includeKeywords"),
            "excludeKeywords": filter_rules.get("excludeKeywords"),
        },
    }

    return (
        "你是鑫图平台 AI 人效自动化系统中的日报总结助手。\n"
        "请基于下面的飞书协同统计数据，生成一份中文日报总结。\n"
        "要求：\n"
        "1. 只基于提供的数据总结，不要臆造不存在的事实。\n"
        "2. 必须统一忽略机器人、工作流、审批流和应用通知交互，它们已经被排除，不要再计入人效判断。\n"
        "3. 输出使用 Markdown。\n"
        "4. 输出结构固定为：\n"
        "   # 今日工作日报\n"
        "   ## 今日概览\n"
        "   ## 沟通协同\n"
        "   ## 会议与文档\n"
        "   ## 风险与待办\n"
        "   ## 明日建议\n"
        "5. 语言简洁、专业，适合直接展示到工作台页面。\n"
        f"6. 当前重点关注：{summary_focus}\n\n"
        "统计数据如下：\n"
        f"{json.dumps(compact_payload, ensure_ascii=False, indent=2)}\n"
    )


async def generate_daily_summary_via_astrbot(
    config: Dict[str, Any],
    report: Dict[str, Any],
    user_id: str,
    focus: str,
    override_model: Optional[str] = None,
) -> Dict[str, Any]:
    astrbot = config.get("astrbot", {})
    base_url = str(astrbot.get("base-url", "")).strip()
    api_key = str(astrbot.get("api-key", "")).strip()
    username = str(astrbot.get("username", "productivity-bot")).strip() or "productivity-bot"
    config_id = str(astrbot.get("config-id", "default")).strip() or "default"
    selected_provider = str(astrbot.get("selected-provider", "deepseek")).strip() or "deepseek"
    selected_model = (override_model or str(astrbot.get("selected-model", "deepseek-v4-flash")).strip() or "deepseek-v4-flash")
    timeout_seconds = int(astrbot.get("request-timeout-seconds", 120) or 120)
    session_prefix = str(config.get("reporting", {}).get("session-prefix", "productivity-daily")).strip() or "productivity-daily"

    if not base_url:
        raise RuntimeError("AstrBot base-url is not configured")
    if not api_key or api_key == "replace-me":
        raise RuntimeError("AstrBot api-key is not configured")

    prompt = build_daily_summary_prompt(report, focus)
    session_id = f"{session_prefix}-{user_id}-{datetime.now().strftime('%Y%m%d')}"
    request_body = {
        "username": username,
        "session_id": session_id,
        "config_id": config_id,
        "selected_provider": selected_provider,
        "selected_model": selected_model,
        "enable_streaming": True,
        "flags": {
            "enable_inline_genui": False,
            "enable_default_system_prompt": True,
            "enable_streaming": True,
        },
        "message": prompt,
    }

    request = tornado.httpclient.HTTPRequest(
        url=normalize_astrbot_chat_url(base_url),
        method="POST",
        headers={
            "Content-Type": "application/json; charset=utf-8",
            "X-API-Key": api_key,
        },
        body=json.dumps(request_body, ensure_ascii=False),
        request_timeout=timeout_seconds,
    )

    client = tornado.httpclient.AsyncHTTPClient()
    response = await client.fetch(request, raise_error=False)
    body_text = response.body.decode("utf-8", errors="ignore")
    if response.code >= 400:
        preview = body_text[:500].strip() or f"HTTP {response.code}"
        raise RuntimeError(f"AstrBot request failed: {preview}")

    summary_text = extract_astrbot_sse_text(body_text)
    return {
        "summaryText": summary_text,
        "sessionId": session_id,
        "provider": selected_provider,
        "model": selected_model,
        "source": "astrbot",
        "promptPreview": prompt[:500],
    }


def save_generated_summary(
    config: Dict[str, Any],
    user_id: str,
    report: Dict[str, Any],
    summary_result: Dict[str, Any],
) -> Dict[str, str]:
    generated_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    payload = {
        "userId": user_id,
        "generatedAt": generated_at,
        "source": summary_result.get("source", "astrbot"),
        "provider": summary_result.get("provider"),
        "model": summary_result.get("model"),
        "sessionId": summary_result.get("sessionId"),
        "reportGeneratedAt": report.get("plugin", {}).get("generatedAt"),
        "summaryText": summary_result.get("summaryText", ""),
        "reportRange": report.get("range", {}),
    }

    markdown = "\n".join(
        [
            f"# {user_id} AI 日报总结",
            "",
            f"- 生成时间：{generated_at}",
            f"- 统计窗口：{report.get('range', {}).get('startAt', '')} ~ {report.get('range', {}).get('endAt', '')}",
            f"- 模型：{summary_result.get('provider', '')}/{summary_result.get('model', '')}",
            "",
            summary_result.get("summaryText", "").strip(),
            "",
        ]
    )

    paths = summary_storage_paths(config, user_id, generated_at)
    for path in paths.values():
        ensure_dir(path.parent)

    json_body = json.dumps(payload, ensure_ascii=False, indent=2) + "\n"
    for target in (paths["latest_json"], paths["history_json"]):
        target.write_text(json_body, encoding="utf-8")
    for target in (paths["latest_md"], paths["history_md"]):
        target.write_text(markdown, encoding="utf-8")

    return {key: str(value) for key, value in paths.items()}


def load_latest_generated_summary(config: Dict[str, Any], user_id: str) -> Dict[str, Any]:
    latest_json = local_storage_root(config) / "ai-summary" / user_id / "daily" / "latest-summary.json"
    if not latest_json.exists():
        raise FileNotFoundError(f"generated summary not found: {latest_json}")
    return load_json_file(latest_json)


def list_collected_user_ids(config: Dict[str, Any]) -> List[str]:
    user_ids = set()

    collector_root = collector_data_root(config)
    if collector_root.exists():
        for child in collector_root.iterdir():
            if child.is_dir() and (child / "latest-report.json").exists():
                user_ids.add(child.name)

    chat_event_root = event_storage_root(config) / "chat-events"
    if chat_event_root.exists():
        for child in chat_event_root.iterdir():
            if child.is_dir():
                user_ids.add(child.name)

    return sorted(user_ids)


def sanitize_filename(value: str, fallback: str) -> str:
    candidate = (value or "").strip()
    if not candidate:
        candidate = fallback
    return candidate.replace(":", "-").replace(" ", "-").replace("/", "-")


def event_payload_paths(config: Dict[str, Any], category: str, user_id: str, generated_at: str) -> Dict[str, Path]:
    safe_user_id = sanitize_filename(user_id, "unknown-user")
    safe_generated_at = sanitize_filename(generated_at, datetime.now().strftime("%Y-%m-%d-%H-%M-%S"))
    root = event_storage_root(config) / category / safe_user_id
    history_dir = root / "history"
    ensure_dir(history_dir)
    return {
        "latest": root / "latest.json",
        "history": history_dir / f"{safe_generated_at}.json",
    }


def detect_generated_at(payload: Dict[str, Any]) -> str:
    report = payload.get("report", {})
    plugin = report.get("plugin", {}) if isinstance(report, dict) else {}
    candidate = str(payload.get("generatedAt") or plugin.get("generatedAt") or "").strip()
    if candidate:
        return candidate
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")


def parse_datetime(value: str) -> datetime:
    return datetime.strptime(value, "%Y-%m-%d %H:%M:%S")


def elasticsearch_enabled(config: Dict[str, Any]) -> bool:
    current = config.get("elasticsearch", {}).get("enabled", False)
    if isinstance(current, bool):
        return current
    if isinstance(current, str):
        return current.strip().lower() == "true"
    return False


def elasticsearch_base_url(config: Dict[str, Any]) -> str:
    return str(config.get("elasticsearch", {}).get("base-url", "")).strip().rstrip("/")


def elasticsearch_request_timeout(config: Dict[str, Any]) -> int:
    return int(config.get("elasticsearch", {}).get("request-timeout-seconds", 15) or 15)


def elasticsearch_validate_cert(config: Dict[str, Any]) -> bool:
    current = config.get("elasticsearch", {}).get("verify-ssl", True)
    if isinstance(current, bool):
        return current
    if isinstance(current, str):
        return current.strip().lower() == "true"
    return True


def elasticsearch_auth_headers(config: Dict[str, Any]) -> Dict[str, str]:
    elasticsearch = config.get("elasticsearch", {})
    api_key = str(elasticsearch.get("api-key", "")).strip()
    username = str(elasticsearch.get("username", "")).strip()
    password = str(elasticsearch.get("password", "")).strip()
    headers: Dict[str, str] = {}
    if api_key:
        headers["Authorization"] = f"ApiKey {api_key}"
    elif username and password:
        import base64

        token = base64.b64encode(f"{username}:{password}".encode("utf-8")).decode("utf-8")
        headers["Authorization"] = f"Basic {token}"
    return headers


def elasticsearch_index_name(prefix: str, event_time: datetime) -> str:
    return f"{prefix}-{event_time.strftime('%Y.%m.%d')}"


def normalize_sender_display(raw_chat: Dict[str, Any], participant_map: Dict[str, str]) -> str:
    sender_open_id = str(raw_chat.get("senderOpenId", "")).strip()
    sender_display = str(raw_chat.get("senderDisplay", "")).strip()
    if sender_open_id and sender_open_id in participant_map:
        return participant_map[sender_open_id]
    if sender_display and sender_display != sender_open_id:
        return sender_display
    return sender_open_id


def participant_display_map(report: Dict[str, Any]) -> Dict[str, str]:
    result: Dict[str, str] = {}
    for item in report.get("participants", []):
        if not isinstance(item, dict):
            continue
        open_id = str(item.get("feishuOpenId", "")).strip()
        display_name = str(item.get("displayName", "")).strip()
        if open_id and display_name:
            result[open_id] = display_name
    return result


def build_chat_event_documents(config: Dict[str, Any], payload: Dict[str, Any]) -> List[Dict[str, Any]]:
    report = payload.get("report", {}) if isinstance(payload.get("report"), dict) else {}
    plugin = report.get("plugin", {}) if isinstance(report, dict) else {}
    report_range = report.get("range", {}) if isinstance(report, dict) else {}
    participant_map = participant_display_map(report)
    generated_at = detect_generated_at(payload)
    retention_until = (parse_datetime(generated_at) + timedelta(days=raw_retention_days(config))).strftime("%Y-%m-%d %H:%M:%S")
    raw_chats = payload.get("rawChats", [])
    documents: List[Dict[str, Any]] = []
    if not isinstance(raw_chats, list):
        return documents

    for raw_chat in raw_chats:
        if not isinstance(raw_chat, dict):
            continue
        occurred_at = str(raw_chat.get("occurredAt", generated_at)).strip() or generated_at
        occurred_at_dt = parse_datetime(occurred_at)
        sender_open_id = str(raw_chat.get("senderOpenId", "")).strip()
        documents.append(
            {
                "@timestamp": occurred_at_dt.isoformat(),
                "retentionUntil": retention_until,
                "tenantId": payload.get("tenantId"),
                "platformUserId": payload.get("userId"),
                "pluginInstanceId": payload.get("pluginInstanceId"),
                "reportGeneratedAt": generated_at,
                "reportRange": report_range,
                "messageType": raw_chat.get("messageType"),
                "chatId": raw_chat.get("chatId"),
                "chatName": raw_chat.get("chatName"),
                "chatMode": raw_chat.get("chatMode"),
                "text": raw_chat.get("text"),
                "senderOpenId": sender_open_id,
                "senderDisplay": normalize_sender_display(raw_chat, participant_map),
                "senderType": raw_chat.get("senderType"),
                "mentionedOpenIds": raw_chat.get("mentionedOpenIds", []),
                "mentionedNames": raw_chat.get("mentionedNames", []),
                "occurredAt": occurred_at,
                "source": "feishu-collector-plugin",
                "identitySource": report.get("profile", {}).get("identitySource"),
                "reportParticipants": report.get("participants", []),
            }
        )
    return documents


def build_summary_document(config: Dict[str, Any], payload: Dict[str, Any]) -> Dict[str, Any]:
    report = payload.get("report", {}) if isinstance(payload.get("report"), dict) else {}
    generated_at = detect_generated_at(payload)
    retention_until = (parse_datetime(generated_at) + timedelta(days=summary_retention_days(config))).strftime("%Y-%m-%d %H:%M:%S")
    return {
        "@timestamp": parse_datetime(generated_at).isoformat(),
        "retentionUntil": retention_until,
        "tenantId": payload.get("tenantId"),
        "platformUserId": payload.get("userId"),
        "pluginInstanceId": payload.get("pluginInstanceId"),
        "generatedAt": generated_at,
        "range": report.get("range", {}),
        "profile": report.get("profile", {}),
        "communication": report.get("communication", {}),
        "meeting": report.get("meeting", {}),
        "document": report.get("document", {}),
        "topics": report.get("topics", []),
        "participants": report.get("participants", []),
        "dailySummary": report.get("dailySummary", []),
        "source": "feishu-collector-plugin",
    }


async def elasticsearch_bulk_index(config: Dict[str, Any], endpoint: str, lines: List[str]) -> Dict[str, Any]:
    if not lines:
        return {"enabled": elasticsearch_enabled(config), "indexed": 0, "errors": False}
    request = tornado.httpclient.HTTPRequest(
        url=f"{elasticsearch_base_url(config)}/{endpoint}",
        method="POST",
        headers={
            "Content-Type": "application/x-ndjson",
            **elasticsearch_auth_headers(config),
        },
        body="".join(lines),
        request_timeout=elasticsearch_request_timeout(config),
        validate_cert=elasticsearch_validate_cert(config),
    )
    response = await tornado.httpclient.AsyncHTTPClient().fetch(request, raise_error=False)
    body_text = response.body.decode("utf-8", errors="ignore")
    if response.code >= 400:
        return {"enabled": True, "indexed": 0, "errors": True, "error": body_text[:500] or f"HTTP {response.code}"}
    try:
        payload = json.loads(body_text) if body_text else {}
    except json.JSONDecodeError:
        payload = {}
    items = payload.get("items", []) if isinstance(payload, dict) else []
    return {
        "enabled": True,
        "indexed": len(items),
        "errors": bool(payload.get("errors", False)) if isinstance(payload, dict) else False,
        "responsePreview": body_text[:500],
    }


async def index_chat_payload_to_elasticsearch(config: Dict[str, Any], payload: Dict[str, Any]) -> Dict[str, Any]:
    if not elasticsearch_enabled(config):
        return {"enabled": False, "indexed": 0}
    documents = build_chat_event_documents(config, payload)
    if not documents:
        return {"enabled": True, "indexed": 0, "errors": False}
    raw_prefix = str(config.get("elasticsearch", {}).get("raw-index-prefix", "cpo-productivity-chat-raw")).strip() or "cpo-productivity-chat-raw"
    lines: List[str] = []
    for doc in documents:
        event_time = datetime.fromisoformat(doc["@timestamp"])
        lines.append(json.dumps({"index": {"_index": elasticsearch_index_name(raw_prefix, event_time)}}, ensure_ascii=False) + "\n")
        lines.append(json.dumps(doc, ensure_ascii=False) + "\n")
    return await elasticsearch_bulk_index(config, "_bulk", lines)


async def index_summary_to_elasticsearch(config: Dict[str, Any], payload: Dict[str, Any]) -> Dict[str, Any]:
    if not elasticsearch_enabled(config):
        return {"enabled": False, "indexed": 0}
    doc = build_summary_document(config, payload)
    summary_prefix = str(config.get("elasticsearch", {}).get("summary-index-prefix", "cpo-productivity-summary")).strip() or "cpo-productivity-summary"
    event_time = datetime.fromisoformat(doc["@timestamp"])
    lines = [
        json.dumps({"index": {"_index": elasticsearch_index_name(summary_prefix, event_time)}}, ensure_ascii=False) + "\n",
        json.dumps(doc, ensure_ascii=False) + "\n",
    ]
    return await elasticsearch_bulk_index(config, "_bulk", lines)


def save_event_payload(config: Dict[str, Any], category: str, payload: Dict[str, Any]) -> Dict[str, str]:
    user_id = str(payload.get("userId") or payload.get("user_id") or "unknown-user").strip() or "unknown-user"
    generated_at = detect_generated_at(payload)
    paths = event_payload_paths(config, category, user_id, generated_at)
    body = json.dumps(payload, ensure_ascii=False, indent=2) + "\n"
    for target in paths.values():
        ensure_dir(target.parent)
        target.write_text(body, encoding="utf-8")
    return {key: str(value) for key, value in paths.items()}


def load_latest_event_payload(config: Dict[str, Any], category: str, user_id: str) -> Dict[str, Any]:
    latest_path = event_storage_root(config) / category / sanitize_filename(user_id, "unknown-user") / "latest.json"
    if not latest_path.exists():
        raise FileNotFoundError(f"{category} latest payload not found: {latest_path}")
    return load_json_file(latest_path)


def notification_storage_root(config: Dict[str, Any]) -> Path:
    raw_path = str(config.get("notification", {}).get("local-data-root", "")).strip()
    return resolve_path(raw_path, local_storage_root(config) / "notifications")


def notification_webhook_url(config: Dict[str, Any]) -> str:
    return str(config.get("notification", {}).get("webhook-url", "")).strip()


def notification_webhook_type(config: Dict[str, Any]) -> str:
    return str(config.get("notification", {}).get("webhook-type", "generic")).strip().lower() or "generic"


def notification_timeout_seconds(config: Dict[str, Any]) -> int:
    return int(config.get("notification", {}).get("timeout-seconds", 10) or 10)


def notification_history_paths(config: Dict[str, Any], user_id: str, created_at: str) -> Dict[str, Path]:
    safe_user_id = sanitize_filename(user_id, "unknown-user")
    safe_created_at = sanitize_filename(created_at, datetime.now().strftime("%Y-%m-%d-%H-%M-%S"))
    history_key = f"{safe_created_at}-{uuid.uuid4().hex[:8]}"
    root = notification_storage_root(config) / safe_user_id
    history_dir = root / "history"
    ensure_dir(history_dir)
    return {
        "latest": root / "latest.json",
        "history": history_dir / f"{history_key}.json",
    }


def normalize_notification_payload(payload: Dict[str, Any]) -> Dict[str, Any]:
    created_at = str(payload.get("createdAt") or payload.get("generatedAt") or "").strip()
    if not created_at:
        created_at = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    metadata = payload.get("metadata")
    return {
        "id": str(payload.get("id") or uuid.uuid4().hex),
        "category": str(payload.get("category") or "collector").strip() or "collector",
        "severity": str(payload.get("severity") or "warning").strip() or "warning",
        "status": str(payload.get("status") or "FAILED").strip() or "FAILED",
        "title": str(payload.get("title") or "未命名通知").strip() or "未命名通知",
        "detail": str(payload.get("detail") or payload.get("message") or "").strip(),
        "userId": str(payload.get("userId") or payload.get("platformUserId") or "unknown-user").strip() or "unknown-user",
        "tenantId": payload.get("tenantId"),
        "pluginInstanceId": payload.get("pluginInstanceId"),
        "source": str(payload.get("source") or "cpo-ai-productivity-service").strip() or "cpo-ai-productivity-service",
        "createdAt": created_at,
        "requiresAction": bool(payload.get("requiresAction", True)),
        "metadata": metadata if isinstance(metadata, dict) else {},
    }


def save_notification_payload(config: Dict[str, Any], payload: Dict[str, Any]) -> Dict[str, Any]:
    normalized = normalize_notification_payload(payload)
    paths = notification_history_paths(config, normalized["userId"], normalized["createdAt"])
    body = json.dumps(normalized, ensure_ascii=False, indent=2) + "\n"
    for target in paths.values():
        ensure_dir(target.parent)
        target.write_text(body, encoding="utf-8")
    return {
        "record": normalized,
        "savedPaths": {key: str(value) for key, value in paths.items()},
    }


def list_notification_records(config: Dict[str, Any], user_id: str, limit: int = 20) -> List[Dict[str, Any]]:
    history_dir = notification_storage_root(config) / sanitize_filename(user_id, "unknown-user") / "history"
    if not history_dir.exists():
        return []
    records: List[Dict[str, Any]] = []
    files = sorted(history_dir.glob("*.json"), reverse=True)
    for item in files[: max(limit, 1)]:
        try:
            records.append(load_json_file(item))
        except Exception:
            continue
    return records


def build_notification_webhook_payload(config: Dict[str, Any], record: Dict[str, Any]) -> Dict[str, Any]:
    if notification_webhook_type(config) == "feishu":
        lines = [
            "鑫图平台采集通知",
            f"用户: {record.get('userId', '-')}",
            f"级别: {record.get('severity', '-')}",
            f"分类: {record.get('category', '-')}",
            f"标题: {record.get('title', '-')}",
        ]
        detail = str(record.get("detail", "")).strip()
        if detail:
            lines.append(f"详情: {detail}")
        lines.append(f"时间: {record.get('createdAt', '-')}")
        return {
            "msg_type": "text",
            "content": {
                "text": "\n".join(lines),
            },
        }
    return record


async def deliver_notification_webhook(config: Dict[str, Any], record: Dict[str, Any]) -> Dict[str, Any]:
    webhook_url = notification_webhook_url(config)
    if not webhook_url:
        return {"enabled": False, "delivered": False}
    body = json.dumps(build_notification_webhook_payload(config, record), ensure_ascii=False)
    request = tornado.httpclient.HTTPRequest(
        url=webhook_url,
        method="POST",
        headers={"Content-Type": "application/json; charset=utf-8"},
        body=body,
        request_timeout=notification_timeout_seconds(config),
    )
    response = await tornado.httpclient.AsyncHTTPClient().fetch(request, raise_error=False)
    response_text = response.body.decode("utf-8", errors="ignore")
    if response.code >= 400:
        return {
            "enabled": True,
            "delivered": False,
            "statusCode": response.code,
            "error": response_text[:500] or f"HTTP {response.code}",
        }
    return {
        "enabled": True,
        "delivered": True,
        "statusCode": response.code,
        "responsePreview": response_text[:500],
    }


async def persist_notification_event(config: Dict[str, Any], payload: Dict[str, Any]) -> Dict[str, Any]:
    saved = save_notification_payload(config, payload)
    delivery = await deliver_notification_webhook(config, saved["record"])
    return {
        "record": saved["record"],
        "savedPaths": saved["savedPaths"],
        "delivery": delivery,
    }


async def generate_and_store_daily_summary(
    config: Dict[str, Any],
    user_id: str,
    focus: str,
    override_model: Optional[str] = None,
) -> Dict[str, Any]:
    report = load_latest_collector_report(config, user_id)
    summary_result = await generate_daily_summary_via_astrbot(
        config,
        report,
        user_id,
        focus,
        override_model=override_model,
    )
    saved_paths = save_generated_summary(config, user_id, report, summary_result)
    return {
        "userId": user_id,
        "status": "SUCCESS",
        "source": summary_result.get("source"),
        "provider": summary_result.get("provider"),
        "model": summary_result.get("model"),
        "sessionId": summary_result.get("sessionId"),
        "reportGeneratedAt": report.get("plugin", {}).get("generatedAt"),
        "summaryText": summary_result.get("summaryText"),
        "savedPaths": saved_paths,
    }


class BaseHandler(tornado.web.RequestHandler):
    @property
    def app_config(self) -> Dict[str, Any]:
        return self.settings["app_config"]

    def write_json(self, status_code: int, message: str, data: Any) -> None:
        self.set_status(status_code)
        self.set_header("Content-Type", "application/json; charset=utf-8")
        self.finish(json.dumps({"code": status_code, "message": message, "data": data}, ensure_ascii=False))

    def read_json_body(self) -> Dict[str, Any]:
        if not self.request.body:
            return {}
        try:
            data = tornado.escape.json_decode(self.request.body)
        except ValueError:
            return {}
        return data if isinstance(data, dict) else {}


class HealthHandler(BaseHandler):
    async def get(self) -> None:
        self.write_json(200, "success", {"status": "UP"})


class ProductivityOverviewHandler(BaseHandler):
    async def get(self) -> None:
        user_id = self.get_query_argument("userId", default_user_id(self.app_config))
        time_range = self.get_query_argument("range", "7d")
        overview = {
            "userId": user_id,
            "range": time_range,
            "ticketCount": "待接入",
            "ticketDoneCount": "待接入",
            "ticketPendingCount": "待接入",
            "communicationCount": "待接入",
            "meetingCount": "待接入",
            "documentCount": "待接入",
            "resourceCount": "待接入",
            "serviceModules": [
                "工单运营",
                "飞书采集配置",
                "AI 日报与周报",
                "资源与监控纵览",
            ],
        }

        try:
            report = load_latest_collector_report(self.app_config, user_id)
            overview.update(
                {
                    "communicationCount": report.get("communication", {}).get("singleChatCount", 0)
                    + report.get("communication", {}).get("groupChatCount", 0),
                    "meetingCount": report.get("meeting", {}).get("meetingCount", 0),
                    "documentCount": report.get("document", {}).get("editCount", 0),
                    "mentionCount": report.get("communication", {}).get("mentionCount", 0),
                    "latestReportGeneratedAt": report.get("plugin", {}).get("generatedAt"),
                    "source": "collector-report",
                }
            )
        except FileNotFoundError:
            overview["source"] = "collector-report-missing"

        self.write_json(200, "success", overview)


class WorkOrderOverviewHandler(BaseHandler):
    async def get(self) -> None:
        user_id = self.get_query_argument("userId", default_user_id(self.app_config))
        time_range = self.get_query_argument("range", "7d")
        self.write_json(
            200,
            "success",
            {
                "userId": user_id,
                "range": time_range,
                "status": "PLANNED",
                "source": "PostgreSQL 工单库",
                "summary": "工单运营已统一归入 AI 人效自动化系统子项目，等待接入你后续提供的 SQL 查询口径。",
                "metrics": ["处理中", "已办", "协办", "SLA"],
            },
        )


class DailyReportSourceHandler(BaseHandler):
    async def get(self) -> None:
        user_id = self.get_query_argument("userId", default_user_id(self.app_config))
        try:
            report = load_latest_collector_report(self.app_config, user_id)
        except FileNotFoundError as exc:
            self.write_json(
                200,
                "success",
                {
                    "userId": user_id,
                    "exists": False,
                    "reportPath": str(latest_collector_report_path(self.app_config, user_id)),
                    "report": None,
                    "error": str(exc),
                },
            )
            return

        self.write_json(
            200,
            "success",
            {
                "userId": user_id,
                "exists": True,
                "reportPath": str(latest_collector_report_path(self.app_config, user_id)),
                "report": report,
            },
        )


class DailySummaryGenerateHandler(BaseHandler):
    async def post(self) -> None:
        payload = self.read_json_body()
        user_id = str(payload.get("userId") or default_user_id(self.app_config)).strip() or default_user_id(self.app_config)
        focus = str(payload.get("focus") or "").strip()
        override_model = str(payload.get("model") or "").strip() or None

        try:
            result = await generate_and_store_daily_summary(
                self.app_config,
                user_id,
                focus,
                override_model=override_model,
            )
        except Exception as exc:
            await persist_notification_event(
                self.app_config,
                {
                    "category": "daily_summary_failed",
                    "severity": "warning",
                    "status": "FAILED",
                    "title": "AI 日报生成失败",
                    "detail": str(exc),
                    "userId": user_id,
                    "source": "cpo-ai-productivity-service",
                    "metadata": {
                        "focus": focus,
                        "model": override_model,
                    },
                },
            )
            self.write_json(
                500,
                "failed to generate daily summary",
                {
                    "userId": user_id,
                    "error": str(exc),
                    "hint": "请确认 AstrBot 已启动、已创建具备 chat scope 的 API Key，并已在 AstrBot 中配置 deepseek-v4-flash provider。",
                },
            )
            return

        self.write_json(200, "success", result)


class DailySummaryLatestHandler(BaseHandler):
    async def get(self) -> None:
        user_id = self.get_query_argument("userId", default_user_id(self.app_config))
        try:
            summary = load_latest_generated_summary(self.app_config, user_id)
        except FileNotFoundError as exc:
            self.write_json(
                200,
                "success",
                {
                    "userId": user_id,
                    "exists": False,
                    "summary": None,
                    "error": str(exc),
                },
            )
            return
        self.write_json(
            200,
            "success",
            {
                "userId": user_id,
                "exists": True,
                "summary": summary,
            },
        )


class PluginHeartbeatHandler(BaseHandler):
    async def post(self) -> None:
        payload = self.read_json_body()
        saved_paths = save_event_payload(self.app_config, "heartbeat", payload)
        self.write_json(200, "success", {"category": "heartbeat", "savedPaths": saved_paths})


class PluginChatEventsUploadHandler(BaseHandler):
    async def post(self) -> None:
        payload = self.read_json_body()
        saved_paths = save_event_payload(self.app_config, "chat-events", payload)
        es_raw_result = await index_chat_payload_to_elasticsearch(self.app_config, payload)
        es_summary_result = await index_summary_to_elasticsearch(self.app_config, payload)
        self.write_json(
            200,
            "success",
            {
                "category": "chat-events",
                "savedPaths": saved_paths,
                "elasticsearch": {
                    "rawEvents": es_raw_result,
                    "summary": es_summary_result,
                },
            },
        )


class PluginMeetingStatsUploadHandler(BaseHandler):
    async def post(self) -> None:
        payload = self.read_json_body()
        saved_paths = save_event_payload(self.app_config, "meeting-stats", payload)
        self.write_json(200, "success", {"category": "meeting-stats", "savedPaths": saved_paths})


class PluginDocumentStatsUploadHandler(BaseHandler):
    async def post(self) -> None:
        payload = self.read_json_body()
        saved_paths = save_event_payload(self.app_config, "document-stats", payload)
        self.write_json(200, "success", {"category": "document-stats", "savedPaths": saved_paths})


class LatestChatEventPayloadHandler(BaseHandler):
    async def get(self) -> None:
        user_id = self.get_query_argument("userId", default_user_id(self.app_config))
        try:
            payload = load_latest_event_payload(self.app_config, "chat-events", user_id)
        except FileNotFoundError as exc:
            self.write_json(404, "latest chat-events payload not found", {"userId": user_id, "error": str(exc)})
            return
        self.write_json(200, "success", payload)


class AstrBotStatusHandler(BaseHandler):
    async def get(self) -> None:
        self.write_json(200, "success", astrbot_status_payload(self.app_config))


class DailySummaryBatchGenerateHandler(BaseHandler):
    async def post(self) -> None:
        payload = self.read_json_body()
        focus = str(payload.get("focus") or "").strip()
        override_model = str(payload.get("model") or "").strip() or None
        continue_on_error = bool(payload.get("continueOnError", True))
        notify_on_failure = bool(payload.get("notifyOnFailure", True))
        requested = payload.get("userIds")
        concurrency = max(1, int(payload.get("concurrency") or self.app_config.get("reporting", {}).get("batch-concurrency", 2) or 2))

        if isinstance(requested, list):
            user_ids = [str(item).strip() for item in requested if str(item).strip()]
        elif isinstance(requested, str) and requested.strip():
            user_ids = [item.strip() for item in requested.split(",") if item.strip()]
        else:
            user_ids = list_collected_user_ids(self.app_config)

        if not user_ids:
            self.write_json(404, "no collected users found", {"requestedUserIds": requested, "resolvedUserIds": []})
            return

        semaphore = asyncio.Semaphore(concurrency)
        results: List[Dict[str, Any]] = []

        async def run_for_user(target_user_id: str) -> None:
            async with semaphore:
                try:
                    result = await generate_and_store_daily_summary(
                        self.app_config,
                        target_user_id,
                        focus,
                        override_model=override_model,
                    )
                    results.append(result)
                except Exception as exc:
                    error_result = {
                        "userId": target_user_id,
                        "status": "FAILED",
                        "error": str(exc),
                    }
                    results.append(error_result)
                    if notify_on_failure:
                        await persist_notification_event(
                            self.app_config,
                            {
                                "category": "daily_summary_failed",
                                "severity": "warning",
                                "status": "FAILED",
                                "title": "AI 日报生成失败",
                                "detail": str(exc),
                                "userId": target_user_id,
                                "source": "cpo-ai-productivity-service",
                                "metadata": {
                                    "focus": focus,
                                    "model": override_model,
                                },
                            },
                        )
                    if not continue_on_error:
                        raise

        try:
            await asyncio.gather(*(run_for_user(item) for item in user_ids))
        except Exception as exc:
            self.write_json(
                500,
                "batch daily summary generation failed",
                {
                    "error": str(exc),
                    "requestedUserIds": requested,
                    "resolvedUserIds": user_ids,
                    "results": results,
                },
            )
            return

        success_count = len([item for item in results if item.get("status") == "SUCCESS"])
        failure_count = len(results) - success_count
        self.write_json(
            200,
            "success",
            {
                "requestedUserIds": requested,
                "resolvedUserIds": user_ids,
                "concurrency": concurrency,
                "successCount": success_count,
                "failureCount": failure_count,
                "results": results,
            },
        )


class NotificationUploadHandler(BaseHandler):
    async def post(self) -> None:
        payload = self.read_json_body()
        saved = await persist_notification_event(self.app_config, payload)
        self.write_json(200, "success", saved)


class NotificationLatestHandler(BaseHandler):
    async def get(self) -> None:
        user_id = self.get_query_argument("userId", default_user_id(self.app_config))
        limit = int(self.get_query_argument("limit", "20") or 20)
        records = list_notification_records(self.app_config, user_id, limit=limit)
        self.write_json(
            200,
            "success",
            {
                "userId": user_id,
                "count": len(records),
                "records": records,
            },
        )


def make_app(config: Dict[str, Any]) -> tornado.web.Application:
    return tornado.web.Application(
        [
            (r"/healthz", HealthHandler),
            (r"/api/productivity/overview/self", ProductivityOverviewHandler),
            (r"/api/productivity/workorder/self", WorkOrderOverviewHandler),
            (r"/api/productivity/report/daily/latest", DailyReportSourceHandler),
            (r"/api/productivity/astrbot/status", AstrBotStatusHandler),
            (r"/api/productivity/report/daily/summary/generate", DailySummaryGenerateHandler),
            (r"/api/productivity/report/daily/summary/generate/batch", DailySummaryBatchGenerateHandler),
            (r"/api/productivity/report/daily/summary/latest", DailySummaryLatestHandler),
            (r"/api/productivity/plugin/heartbeat", PluginHeartbeatHandler),
            (r"/api/productivity/plugin/upload/chat-events", PluginChatEventsUploadHandler),
            (r"/api/productivity/plugin/upload/meeting-stats", PluginMeetingStatsUploadHandler),
            (r"/api/productivity/plugin/upload/document-stats", PluginDocumentStatsUploadHandler),
            (r"/api/productivity/plugin/upload/chat-events/latest", LatestChatEventPayloadHandler),
            (r"/api/productivity/plugin/notification", NotificationUploadHandler),
            (r"/api/productivity/plugin/notification/latest", NotificationLatestHandler),
        ],
        app_config=config,
        debug=os.getenv("DEBUG", "false").lower() == "true",
    )


def main() -> None:
    config = load_config()
    port = int(os.getenv("SERVER_PORT", config.get("server", {}).get("port", 9060)))
    app = make_app(config)
    app.listen(port)
    print(
        json.dumps(
            {
                "service": config.get("app", {}).get("name", "cpo-ai-productivity-service"),
                "port": port,
                "nacosServer": config.get("nacos", {}).get("server-addr", "127.0.0.1:8848"),
                "nacosNamespace": config.get("nacos", {}).get("namespace", "st-observable"),
                "collectorDataRoot": str(collector_data_root(config)),
                "eventDataRoot": str(event_storage_root(config)),
                "elasticsearchEnabled": elasticsearch_enabled(config),
                "elasticsearchBaseUrl": elasticsearch_base_url(config),
                "astrbotBaseUrl": config.get("astrbot", {}).get("base-url", ""),
            },
            ensure_ascii=False,
        )
    )
    tornado.ioloop.IOLoop.current().start()


if __name__ == "__main__":
    main()
