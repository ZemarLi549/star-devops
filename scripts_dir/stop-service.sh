#!/usr/bin/env bash
set -euo pipefail

SERVICE_DIR="${1:?usage: bash scripts_dir/stop-service.sh <service_dir>}"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="$ROOT_DIR/$SERVICE_DIR/logs/service.pid"

if [ ! -f "$PID_FILE" ]; then
  echo "pid file not found: $PID_FILE"
  exit 0
fi

PID="$(cat "$PID_FILE")"
if kill -0 "$PID" 2>/dev/null; then
  kill "$PID"
  echo "stopped: $SERVICE_DIR ($PID)"
else
  echo "process not running: $SERVICE_DIR ($PID)"
fi

rm -f "$PID_FILE"
