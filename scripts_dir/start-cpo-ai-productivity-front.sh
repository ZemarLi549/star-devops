#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SERVICE_DIR="$ROOT_DIR/cpo-ai-productivity-front"
LOG_DIR="$SERVICE_DIR/logs"
PID_FILE="$LOG_DIR/service.pid"
LOG_FILE="$LOG_DIR/service.out"
PORT="${FRONT_PORT:-3011}"
MODE="${FRONT_MODE:-dev}"
NODE_BIN_DIR="${NODE_BIN_DIR:-/home/kali/.hermes/node/bin}"
PROCESS_PATTERN="$SERVICE_DIR/node_modules/.bin/vite --host 0.0.0.0 --port $PORT"
VITE_BIN="$SERVICE_DIR/node_modules/.bin/vite"

mkdir -p "$LOG_DIR"

if [ ! -d "$SERVICE_DIR/node_modules" ]; then
  (cd "$SERVICE_DIR" && PATH="$NODE_BIN_DIR:$PATH" npm install)
fi

if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "cpo-ai-productivity-front already running: $(cat "$PID_FILE")"
  exit 0
fi

LEGACY_PID="$(pgrep -f "$PROCESS_PATTERN" | head -n 1 || true)"
if [ -n "$LEGACY_PID" ]; then
  echo "$LEGACY_PID" >"$PID_FILE"
  echo "cpo-ai-productivity-front already running without pid file: $LEGACY_PID"
  exit 0
fi

cd "$SERVICE_DIR"

case "$MODE" in
  dev)
    COMMAND=("$VITE_BIN" --host 0.0.0.0 --port "$PORT" --strictPort)
    ;;
  preview)
    if [ ! -f "$SERVICE_DIR/dist/index.html" ]; then
      PATH="$NODE_BIN_DIR:$PATH" npm run build
    fi
    COMMAND=("$VITE_BIN" preview --host 0.0.0.0 --port "$PORT" --strictPort)
    ;;
  *)
    echo "unknown FRONT_MODE: $MODE"
    echo "supported modes: dev | preview"
    exit 1
    ;;
esac

nohup env PATH="$NODE_BIN_DIR:$PATH" "${COMMAND[@]}" >>"$LOG_FILE" 2>&1 < /dev/null &
echo $! >"$PID_FILE"

echo "cpo-ai-productivity-front started: $(cat "$PID_FILE")"
echo "mode: $MODE"
echo "port: $PORT"
echo "log: $LOG_FILE"
