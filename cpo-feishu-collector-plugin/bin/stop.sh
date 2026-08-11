#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="${PID_FILE:-$ROOT_DIR/logs/collector.pid}"

if [ -f "$PID_FILE" ]; then
  PID="$(cat "$PID_FILE")"
  if kill -0 "$PID" 2>/dev/null; then
    kill "$PID"
    echo "collector stopped: $PID"
  else
    echo "collector pid stale: $PID"
  fi
  rm -f "$PID_FILE"
  exit 0
fi

PKG_PID="$(pgrep -f "$ROOT_DIR/bin/collector.mjs" | head -n 1 || true)"
if [ -n "$PKG_PID" ]; then
  kill "$PKG_PID"
  echo "collector stopped by process match: $PKG_PID"
  exit 0
fi

echo "collector not running"
