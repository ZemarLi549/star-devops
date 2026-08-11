#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="${PID_FILE:-$ROOT_DIR/logs/collector.pid}"
LOG_FILE="${LOG_FILE:-$ROOT_DIR/logs/collector.out}"

if [ -f "$PID_FILE" ]; then
  PID="$(cat "$PID_FILE")"
  if kill -0 "$PID" 2>/dev/null; then
    echo "collector running: $PID"
    echo "pid file: $PID_FILE"
    echo "log file: $LOG_FILE"
    exit 0
  fi
  echo "collector pid stale: $PID"
  exit 1
fi

echo "collector not running"
echo "pid file: $PID_FILE"
exit 1
