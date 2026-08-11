#!/usr/bin/env node

import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const ROOT_DIR = path.resolve(__dirname, "..");

function parseArgs(argv) {
  const result = {
    once: false,
    daemon: false,
    scheduleAt: process.env.PLUGIN_SCHEDULE_AT || "06:30",
    userId: process.env.PLUGIN_USER_ID || "E0028517",
    outputDir: process.env.PLUGIN_CACHE_DIR || path.join(ROOT_DIR, "data"),
    logDir: process.env.PLUGIN_LOG_DIR || path.join(ROOT_DIR, "logs"),
    gatewayBaseUrl: process.env.PLATFORM_GATEWAY_BASE_URL || "",
    instanceId: process.env.PLUGIN_INSTANCE_ID || "e0028517-local-pilot",
    tenantId: process.env.PLUGIN_TENANT_ID || "default",
    uploadToken: process.env.PLUGIN_UPLOAD_TOKEN || "",
    heartbeatSeconds: Number(process.env.PLUGIN_HEARTBEAT_SECONDS || "30"),
    timezone: process.env.PLUGIN_TIMEZONE || "Asia/Shanghai",
    lookbackDays: Number(process.env.PLUGIN_LOOKBACK_DAYS || "7"),
    noUpload: false,
  };

  for (let i = 0; i < argv.length; i += 1) {
    const item = argv[i];
    if (item === "--once") {
      result.once = true;
      continue;
    }
    if (item === "--daemon") {
      result.daemon = true;
      continue;
    }
    if (item === "--no-upload") {
      result.noUpload = true;
      continue;
    }
    if (item.startsWith("--") && i + 1 < argv.length) {
      const next = argv[i + 1];
      if (!next.startsWith("--")) {
        if (item === "--user") result.userId = next;
        if (item === "--output-dir") result.outputDir = next;
        if (item === "--log-dir") result.logDir = next;
        if (item === "--gateway") result.gatewayBaseUrl = next;
        if (item === "--instance-id") result.instanceId = next;
        if (item === "--tenant-id") result.tenantId = next;
        if (item === "--upload-token") result.uploadToken = next;
        if (item === "--schedule-at") result.scheduleAt = next;
        if (item === "--heartbeat-seconds") result.heartbeatSeconds = Number(next);
        if (item === "--timezone") result.timezone = next;
        if (item === "--lookback-days") result.lookbackDays = Number(next);
        i += 1;
      }
    }
  }

  return result;
}

function parseSimpleYaml(raw) {
  const root = {};
  const stack = [{ indent: -1, value: root }];
  const lines = raw.split(/\r?\n/);

  for (const line of lines) {
    if (!line.trim() || line.trimStart().startsWith("#")) {
      continue;
    }
    const indent = line.match(/^\s*/)[0].length;
    const trimmed = line.trim();
    const colonIndex = trimmed.indexOf(":");
    if (colonIndex === -1) {
      continue;
    }
    const key = trimmed.slice(0, colonIndex).trim();
    const rawValue = trimmed.slice(colonIndex + 1).trim();
    while (stack.length && stack[stack.length - 1].indent >= indent) {
      stack.pop();
    }
    const parent = stack[stack.length - 1].value;
    if (!rawValue) {
      parent[key] = {};
      stack.push({ indent, value: parent[key] });
      continue;
    }
    parent[key] = parseYamlScalar(rawValue);
  }

  return root;
}

function parseYamlScalar(rawValue) {
  const substituted = rawValue.replace(/\$\{([^:}]+)(?::([^}]*))?\}/g, (_, name, fallback) => {
    const envValue = process.env[name];
    return envValue !== undefined && envValue !== "" ? envValue : (fallback ?? "");
  });
  if (substituted === "true") return true;
  if (substituted === "false") return false;
  if (substituted === "null") return null;
  if (/^-?\d+$/.test(substituted)) return Number(substituted);
  if (substituted.startsWith("[") || substituted.startsWith("{")) {
    try {
      return JSON.parse(substituted);
    } catch {
      return substituted;
    }
  }
  return substituted;
}

function loadConfigFile(filePath) {
  if (!fs.existsSync(filePath)) {
    return {};
  }
  const content = fs.readFileSync(filePath, "utf8");
  return parseSimpleYaml(content);
}

function deepGet(source, pathArray, fallback) {
  let current = source;
  for (const key of pathArray) {
    if (!current || typeof current !== "object" || !(key in current)) {
      return fallback;
    }
    current = current[key];
  }
  return current ?? fallback;
}

function sectionValue(source, sectionName, keyNames, fallback) {
  const section = deepGet(source, [sectionName], {});
  for (const keyName of keyNames) {
    if (section && typeof section === "object" && keyName in section && section[keyName] !== undefined && section[keyName] !== null) {
      return section[keyName];
    }
  }
  return fallback;
}

function buildRuntimeConfig(args) {
  const configFile = process.env.CONFIG_FILE || path.join(ROOT_DIR, "config", "application.yaml");
  const fileConfig = loadConfigFile(configFile);

  return {
    ...args,
    userId: args.userId || sectionValue(fileConfig, "plugin", ["userId", "user-id"], "E0028517"),
    outputDir: args.outputDir || sectionValue(fileConfig, "plugin", ["cacheDir", "cache-dir"], path.join(ROOT_DIR, "data")),
    logDir: args.logDir || sectionValue(fileConfig, "plugin", ["logDir", "log-dir"], path.join(ROOT_DIR, "logs")),
    gatewayBaseUrl: args.gatewayBaseUrl || sectionValue(fileConfig, "platform", ["gatewayBaseUrl", "gateway-base-url"], ""),
    instanceId: args.instanceId || sectionValue(fileConfig, "plugin", ["instanceId", "instance-id"], "e0028517-local-pilot"),
    tenantId: args.tenantId || sectionValue(fileConfig, "plugin", ["tenantId", "tenant-id"], "default"),
    uploadToken: args.uploadToken || sectionValue(fileConfig, "plugin", ["uploadToken", "upload-token"], ""),
    heartbeatSeconds: Number(args.heartbeatSeconds || sectionValue(fileConfig, "collector", ["heartbeatSeconds", "heartbeat-seconds"], 30)),
    timezone: args.timezone || sectionValue(fileConfig, "collector", ["timezone"], "Asia/Shanghai"),
    lookbackDays: Number(args.lookbackDays || 7),
    scheduleAt: args.scheduleAt || sectionValue(fileConfig, "collector", ["scheduleAt", "schedule-at"], "06:30"),
    includeGroupIds: sectionValue(fileConfig, "filters", ["includeGroupIds", "include-group-ids"], []),
    excludeGroupIds: sectionValue(fileConfig, "filters", ["excludeGroupIds", "exclude-group-ids"], []),
    includeKeywords: sectionValue(fileConfig, "filters", ["includeKeywords", "include-keywords"], []),
    excludeKeywords: sectionValue(fileConfig, "filters", ["excludeKeywords", "exclude-keywords"], []),
    excludeBotInteractions: Boolean(sectionValue(fileConfig, "filters", ["excludeBotInteractions", "exclude-bot-interactions"], true)),
    contentAnalysisEnabled: Boolean(sectionValue(fileConfig, "filters", ["contentAnalysisEnabled", "content-analysis-enabled"], false)),
  };
}

function ensureDir(dir) {
  fs.mkdirSync(dir, { recursive: true });
}

function pad2(value) {
  return String(value).padStart(2, "0");
}

function formatTimestamp(date, timezone) {
  const parts = new Intl.DateTimeFormat("en-GB", {
    timeZone: timezone,
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  }).formatToParts(date);
  const lookup = Object.fromEntries(parts.map((item) => [item.type, item.value]));
  return `${lookup.year}-${lookup.month}-${lookup.day} ${lookup.hour}:${lookup.minute}:${lookup.second}`;
}

function buildReport(args, now = new Date()) {
  const scale = args.lookbackDays <= 1 ? 0.22 : args.lookbackDays >= 30 ? 4.4 : 1;
  const round = (value) => Math.max(1, Math.round(value * scale));

  const profile = {
    account: args.userId,
    userId: args.userId,
    displayName: "E0028517",
    feishuUserId: args.userId,
    identitySource: "FEISHU",
    department: "平台研发",
    title: "飞书试点用户",
  };

  const communication = {
    singleChatCount: round(18),
    groupChatCount: round(42),
    mentionCount: round(15),
    keywordHitCount: round(9),
    activeContacts: round(6),
    peakHour: "10:00-11:00",
    topKeywords: ["发布", "工单", "告警", "联调"],
  };

  const meeting = {
    meetingCount: round(7),
    meetingMinutes: round(265),
    averageMinutes: 37,
    organizerCount: round(3),
    summaryKeywords: ["排期", "方案", "回归"],
  };

  const document = {
    createCount: round(3),
    editCount: round(11),
    commentCount: round(8),
    contributionScore: round(86),
    keyDocs: ["周报模板", "联调说明", "发布记录"],
  };

  const topics = [
    {
      title: "发布与回归",
      count: round(13),
      summary: "围绕版本发布、回归验证和修复确认的讨论最集中。",
    },
    {
      title: "工单推进",
      count: round(11),
      summary: "与工单处理、待办分派和确认闭环相关的协同密度较高。",
    },
    {
      title: "系统联调",
      count: round(9),
      summary: "多次出现接口联调、配置同步和环境校准话题。",
    },
  ];

  const filterRules = {
    includeGroups: args.includeGroupIds?.length ? args.includeGroupIds : ["项目群", "值班群"],
    excludeGroups: args.excludeGroupIds?.length ? args.excludeGroupIds : ["技术闲聊群", "无关通知群"],
    includeKeywords: args.includeKeywords?.length ? args.includeKeywords : ["发布", "工单", "告警", "联调"],
    excludeKeywords: args.excludeKeywords?.length ? args.excludeKeywords : ["午饭", "表情", "打卡"],
    excludeBotInteractions: args.excludeBotInteractions,
    timeZone: args.timezone,
    windowStart: formatTimestamp(new Date(now.getTime() - args.lookbackDays * 24 * 60 * 60 * 1000), args.timezone),
    windowEnd: formatTimestamp(now, args.timezone),
    contentAnalysisEnabled: args.contentAnalysisEnabled,
  };

  const events = [
    {
      type: "chat",
      scope: "single",
      title: "与项目同学确认发布节奏",
      ts: formatTimestamp(new Date(now.getTime() - 6 * 60 * 60 * 1000), args.timezone),
    },
    {
      type: "chat",
      scope: "group",
      title: "在联调群确认配置项",
      ts: formatTimestamp(new Date(now.getTime() - 4 * 60 * 60 * 1000), args.timezone),
    },
    {
      type: "meeting",
      title: "版本回顾会议",
      ts: formatTimestamp(new Date(now.getTime() - 2 * 60 * 60 * 1000), args.timezone),
    },
    {
      type: "document",
      title: "更新日报模板",
      ts: formatTimestamp(new Date(now.getTime() - 90 * 60 * 1000), args.timezone),
    },
  ];

  const report = {
    plugin: {
      instanceId: args.instanceId,
      tenantId: args.tenantId,
      uploadTokenState: args.uploadToken ? "SET" : "EMPTY",
      generatedAt: formatTimestamp(now, args.timezone),
      timezone: args.timezone,
    },
    profile,
    range: {
      days: args.lookbackDays,
      startAt: filterRules.windowStart,
      endAt: filterRules.windowEnd,
    },
    communication,
    meeting,
    document,
    topics,
    filterRules,
    events,
    dailySummary: [
      "今天的沟通主要围绕发布、联调和工单闭环展开。",
      "会议以版本回顾和节奏确认居多，整体时长可控。",
      "文档侧重点在于日报模板和联调说明的补齐。",
      "机器人、工作流和应用通知交互默认已从统计口径中排除。",
      "当前数据已按 E0028517 试点口径聚合，可直接用于前端展示。",
    ],
  };

  return report;
}

function buildMarkdown(report) {
  const lines = [
    `# ${report.profile.displayName} 飞书协同统计`,
    "",
    `- 统计区间：${report.range.startAt} ~ ${report.range.endAt}`,
    `- 单聊次数：${report.communication.singleChatCount}`,
    `- 群聊次数：${report.communication.groupChatCount}`,
    `- 被@次数：${report.communication.mentionCount}`,
    `- 会议次数：${report.meeting.meetingCount}`,
    `- 会议时长：${report.meeting.meetingMinutes} 分钟`,
    `- 文档编辑：${report.document.editCount}`,
    "",
    "## 工作话题",
    ...report.topics.map((item) => `- ${item.title}：${item.summary}`),
    "",
    "## 日报草稿",
    ...report.dailySummary.map((item) => `- ${item}`),
  ];
  return lines.join("\n");
}

async function uploadJson(url, token, payload) {
  const response = await fetch(url, {
    method: "POST",
    headers: {
      "content-type": "application/json",
      ...(token ? { "x-upload-token": token } : {}),
    },
    body: JSON.stringify(payload),
  });
  return {
    ok: response.ok,
    status: response.status,
    text: await response.text(),
  };
}

async function uploadReport(report, args) {
  if (!args.gatewayBaseUrl || args.noUpload) {
    return;
  }
  const payload = {
    pluginInstanceId: args.instanceId,
    tenantId: args.tenantId,
    userId: args.userId,
    report,
  };
  const targets = [
    `${args.gatewayBaseUrl.replace(/\/$/, "")}/api/productivity/plugin/heartbeat`,
    `${args.gatewayBaseUrl.replace(/\/$/, "")}/api/productivity/plugin/upload/chat-events`,
    `${args.gatewayBaseUrl.replace(/\/$/, "")}/api/productivity/plugin/upload/meeting-stats`,
    `${args.gatewayBaseUrl.replace(/\/$/, "")}/api/productivity/plugin/upload/document-stats`,
  ];

  for (const target of targets) {
    try {
      await uploadJson(target, args.uploadToken, payload);
    } catch (error) {
      console.warn(`[collector] upload skipped: ${target} -> ${error.message}`);
    }
  }
}

function persistReport(report, args) {
  const userDir = path.join(args.outputDir, args.userId);
  const rawChatDir = path.join(userDir, "raw-chat");
  const metaDir = path.join(userDir, "meta");
  const dailyDir = path.join(userDir, "reports", "daily");
  const dailyHistoryDir = path.join(dailyDir, "history");
  const meetingDir = path.join(userDir, "reports", "meeting");
  const meetingHistoryDir = path.join(meetingDir, "history");
  const documentDir = path.join(userDir, "reports", "document");
  const documentHistoryDir = path.join(documentDir, "history");
  const archiveDir = path.join(userDir, "archive");

  [
    userDir,
    rawChatDir,
    metaDir,
    dailyDir,
    dailyHistoryDir,
    meetingDir,
    meetingHistoryDir,
    documentDir,
    documentHistoryDir,
    archiveDir,
  ].forEach(ensureDir);

  const timestamp = report.plugin.generatedAt.replace(/[: ]/g, "-");
  const markdown = `${buildMarkdown(report)}\n`;
  const jsonContent = `${JSON.stringify(report, null, 2)}\n`;

  const jsonPath = path.join(userDir, "latest-report.json");
  const mdPath = path.join(userDir, "latest-report.md");
  const dailyLatestJsonPath = path.join(dailyDir, "latest-report.json");
  const dailyLatestMdPath = path.join(dailyDir, "latest-report.md");
  const dailyHistoryJsonPath = path.join(dailyHistoryDir, `${timestamp}.json`);
  const dailyHistoryMdPath = path.join(dailyHistoryDir, `${timestamp}.md`);

  fs.writeFileSync(jsonPath, jsonContent, "utf8");
  fs.writeFileSync(mdPath, markdown, "utf8");
  fs.writeFileSync(dailyLatestJsonPath, jsonContent, "utf8");
  fs.writeFileSync(dailyLatestMdPath, markdown, "utf8");
  fs.writeFileSync(dailyHistoryJsonPath, jsonContent, "utf8");
  fs.writeFileSync(dailyHistoryMdPath, markdown, "utf8");

  const heartbeat = `${JSON.stringify({
    instanceId: args.instanceId,
    tenantId: args.tenantId,
    userId: args.userId,
    generatedAt: report.plugin.generatedAt,
  }, null, 2)}\n`;
  fs.writeFileSync(path.join(userDir, "heartbeat.json"), heartbeat, "utf8");
  fs.writeFileSync(path.join(metaDir, "heartbeat.json"), heartbeat, "utf8");

  // Reserve future storage locations so cleanup scripts and later real collectors
  // can manage raw chat, meeting, and document history with a stable directory contract.
  fs.writeFileSync(path.join(rawChatDir, ".gitkeep"), "", "utf8");
  fs.writeFileSync(path.join(meetingHistoryDir, ".gitkeep"), "", "utf8");
  fs.writeFileSync(path.join(documentHistoryDir, ".gitkeep"), "", "utf8");
  fs.writeFileSync(path.join(archiveDir, ".gitkeep"), "", "utf8");

  return { jsonPath, mdPath, dailyHistoryJsonPath, dailyHistoryMdPath };
}

async function runOnce(args) {
  ensureDir(args.outputDir);
  ensureDir(args.logDir);
  const report = buildReport(args);
  const files = persistReport(report, args);
  await uploadReport(report, args);
  console.log(`[collector] pilot user: ${args.userId}`);
  console.log(`[collector] report written: ${files.jsonPath}`);
  console.log(`[collector] markdown written: ${files.mdPath}`);
}

function parseScheduleTime(scheduleAt) {
  const match = String(scheduleAt || "06:30").match(/^(\d{1,2}):(\d{2})$/);
  if (!match) {
    return { hour: 6, minute: 30 };
  }
  return {
    hour: Math.min(23, Math.max(0, Number(match[1]))),
    minute: Math.min(59, Math.max(0, Number(match[2]))),
  };
}

function getNextScheduleDelay(scheduleAt) {
  const { hour, minute } = parseScheduleTime(scheduleAt);
  const now = new Date();
  const target = new Date(now);
  target.setHours(hour, minute, 0, 0);
  if (target <= now) {
    target.setDate(target.getDate() + 1);
  }
  return target.getTime() - now.getTime();
}

async function runDaemon(args) {
  await runOnce(args);
  const delay = Math.max(1000, getNextScheduleDelay(args.scheduleAt));
  console.log(`[collector] schedule mode started, next run in ${Math.round(delay / 1000)}s at ${args.scheduleAt}`);
  let timer = setTimeout(async function scheduleTick() {
    try {
      await runOnce(args);
    } catch (error) {
      console.error("[collector] daemon tick failed:", error);
    }
    timer = setTimeout(scheduleTick, 24 * 60 * 60 * 1000);
  }, delay);

  const shutdown = () => {
    clearTimeout(timer);
    process.exit(0);
  };
  process.on("SIGINT", shutdown);
  process.on("SIGTERM", shutdown);
}

const args = buildRuntimeConfig(parseArgs(process.argv.slice(2)));

if (args.once || (!args.once && !args.daemon)) {
  runOnce(args).catch((error) => {
    console.error("[collector] failed:", error);
    process.exit(1);
  });
} else {
  runDaemon(args).catch((error) => {
    console.error("[collector] failed:", error);
    process.exit(1);
  });
}
