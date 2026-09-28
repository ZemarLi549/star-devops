#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="$ROOT_DIR/cpo-ai-productivity-front/logs/service.pid"
PORT="${FRONT_PORT:-3011}"
PROCESS_PATTERN="$ROOT_DIR/cpo-ai-productivity-front/node_modules/.bin/vite --host 0.0.0.0 --port $PORT"

if [ ! -f "$PID_FILE" ]; then
  LEGACY_PIDS="$(pgrep -f "$PROCESS_PATTERN" || true)"
  if [ -z "$LEGACY_PIDS" ]; then
    echo "pid file not found: $PID_FILE"
    exit 0
  fi
  while read -r PID; do
    [ -n "$PID" ] || continue
    kill "$PID" 2>/dev/null || true
    echo "stopped stray process: cpo-ai-productivity-front ($PID)"
  done <<< "$LEGACY_PIDS"
  exit 0
fi

PID="$(cat "$PID_FILE")"
if kill -0 "$PID" 2>/dev/null; then
  kill "$PID"
  for _ in {1..20}; do
    if kill -0 "$PID" 2>/dev/null; then
      sleep 0.2
    else
      break
    fi
  done
  if kill -0 "$PID" 2>/dev/null; then
    kill -9 "$PID" 2>/dev/null || true
  fi
  echo "stopped: cpo-ai-productivity-front ($PID)"
else
  echo "process not running: cpo-ai-productivity-front ($PID)"
fi

rm -f "$PID_FILE"

LEGACY_PIDS="$(pgrep -f "$PROCESS_PATTERN" || true)"
if [ -n "$LEGACY_PIDS" ]; then
  while read -r LEGACY_PID; do
    [ -n "$LEGACY_PID" ] || continue
    if kill -0 "$LEGACY_PID" 2>/dev/null; then
      kill "$LEGACY_PID" 2>/dev/null || true
      sleep 0.2
      if kill -0 "$LEGACY_PID" 2>/dev/null; then
        kill -9 "$LEGACY_PID" 2>/dev/null || true
      fi
      echo "stopped stray process: cpo-ai-productivity-front ($LEGACY_PID)"
    fi
  done <<< "$LEGACY_PIDS"
fi
