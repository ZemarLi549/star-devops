#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SERVICE_DIR="$ROOT_DIR/cpo-ai-productivity-service"
LOG_DIR="$SERVICE_DIR/logs"
PID_FILE="$LOG_DIR/service.pid"
LOG_FILE="$LOG_DIR/service.out"

mkdir -p "$LOG_DIR"
bash "$ROOT_DIR/scripts_dir/bootstrap-python-service.sh" cpo-ai-productivity-service

if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" 2>/dev/null; then
  echo "cpo-ai-productivity-service already running: $(cat "$PID_FILE")"
  exit 0
fi

if [ ! -f "$SERVICE_DIR/config/application.yaml" ] && [ -f "$SERVICE_DIR/config/application.example.yaml" ]; then
  cp "$SERVICE_DIR/config/application.example.yaml" "$SERVICE_DIR/config/application.yaml"
fi

cd "$SERVICE_DIR"
setsid env CONFIG_FILE="$SERVICE_DIR/config/application.yaml" \
  "$SERVICE_DIR/.venv/bin/python" "$SERVICE_DIR/app.py" >>"$LOG_FILE" 2>&1 < /dev/null &
echo $! >"$PID_FILE"
echo "cpo-ai-productivity-service started: $(cat "$PID_FILE")"
