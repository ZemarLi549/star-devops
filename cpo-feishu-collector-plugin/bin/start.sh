#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CONFIG_FILE="${CONFIG_FILE:-$ROOT_DIR/config/application.yaml}"
PID_FILE="${PID_FILE:-$ROOT_DIR/logs/collector.pid}"
LOG_FILE="${LOG_FILE:-$ROOT_DIR/logs/collector.out}"
PLUGIN_USER_ID="${PLUGIN_USER_ID:-E0028517}"
COLLECTOR_NO_UPLOAD="${COLLECTOR_NO_UPLOAD:-false}"

resolve_binary() {
  if [ -n "${BINARY_PATH:-}" ] && [ -x "${BINARY_PATH}" ]; then
    echo "${BINARY_PATH}"
    return 0
  fi

  if [ -x "$ROOT_DIR/bin/cpo-feishu-collector-plugin" ]; then
    echo "$ROOT_DIR/bin/cpo-feishu-collector-plugin"
    return 0
  fi

  if [ -x "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin" ]; then
    echo "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin"
    return 0
  fi

  if [ -f "$ROOT_DIR/bin/build.sh" ]; then
    bash "$ROOT_DIR/bin/build.sh" >/dev/null
    if [ -x "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin" ]; then
      echo "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin"
      return 0
    fi
  fi

  return 1
}

mkdir -p "$ROOT_DIR/logs" "$ROOT_DIR/data"

if [ ! -f "$CONFIG_FILE" ] && [ -f "$ROOT_DIR/config/application.example.yaml" ]; then
  cp "$ROOT_DIR/config/application.example.yaml" "$CONFIG_FILE"
  echo "created default config: $CONFIG_FILE"
fi

if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "collector already running: $(cat "$PID_FILE")"
  exit 0
fi

BINARY="$(resolve_binary)" || {
  echo "collector binary not found. Please use a release package or run source build first."
  exit 1
}

APP_ID="${FEISHU_APP_ID:-}"
APP_SECRET="${FEISHU_APP_SECRET:-}"
if [ -z "$APP_ID" ] && grep -Eq 'app-id:\s*\$\{FEISHU_APP_ID:replace-me\}|app-id:\s*replace-me' "$CONFIG_FILE" 2>/dev/null; then
  echo "feishu app credentials are not configured in $CONFIG_FILE"
  echo "please configure your own FEISHU_APP_ID / FEISHU_APP_SECRET first"
  exit 1
fi
if [ -z "$APP_SECRET" ] && grep -Eq 'app-secret:\s*\$\{FEISHU_APP_SECRET:replace-me\}|app-secret:\s*replace-me' "$CONFIG_FILE" 2>/dev/null; then
  echo "feishu app credentials are not configured in $CONFIG_FILE"
  echo "please configure your own FEISHU_APP_ID / FEISHU_APP_SECRET first"
  exit 1
fi

TOKEN_FILE="${FEISHU_USER_TOKEN_FILE:-$ROOT_DIR/data/$PLUGIN_USER_ID/meta/feishu-user-token.json}"
if [ ! -f "$TOKEN_FILE" ]; then
  echo "feishu user token not found: $TOKEN_FILE"
  echo "run authorization first:"
  echo "  bash $ROOT_DIR/bin/authorize.sh $PLUGIN_USER_ID"
  echo
  echo "or only print the authorization URL:"
  echo "  PLUGIN_USER_ID=$PLUGIN_USER_ID $BINARY --print-auth-url"
  exit 1
fi

ARGS=(--daemon)
if [ "$COLLECTOR_NO_UPLOAD" = "true" ]; then
  ARGS+=(--no-upload)
fi

nohup "$BINARY" "${ARGS[@]}" >>"$LOG_FILE" 2>&1 &
echo $! >"$PID_FILE"

echo "collector started"
echo "root: $ROOT_DIR"
echo "config: $CONFIG_FILE"
echo "pid: $(cat "$PID_FILE")"
echo "log: $LOG_FILE"
echo "binary: $BINARY"
echo "user token: $TOKEN_FILE"
echo "upload disabled: $COLLECTOR_NO_UPLOAD"
