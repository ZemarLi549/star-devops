#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="$ROOT_DIR/cpo-api-gateway/.local/run/cpo-api-gateway.pid"

if [[ ! -f "$PID_FILE" ]]; then
  echo "cpo-api-gateway binary pid file not found"
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
fi

rm -f "$PID_FILE"
echo "cpo-api-gateway binary stopped"
