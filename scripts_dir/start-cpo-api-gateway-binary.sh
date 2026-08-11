#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
KRAKEND_BIN="${KRAKEND_BIN:-$ROOT_DIR/cpo-api-gateway/.local/bin/krakend}"
CONFIG_FILE="$ROOT_DIR/cpo-api-gateway/krakend.json"
RUN_DIR="$ROOT_DIR/cpo-api-gateway/.local/run"
LOG_FILE="$ROOT_DIR/cpo-api-gateway/logs/binary-gateway.out"
PID_FILE="$RUN_DIR/cpo-api-gateway.pid"

mkdir -p "$RUN_DIR" "$(dirname "$LOG_FILE")"

if [[ ! -x "$KRAKEND_BIN" ]]; then
  echo "KrakenD binary not found at $KRAKEND_BIN" >&2
  echo "Run scripts_dir/install-krakend-binary.sh first." >&2
  exit 1
fi

if [[ -f "$PID_FILE" ]]; then
  PID="$(cat "$PID_FILE")"
  if kill -0 "$PID" 2>/dev/null; then
    echo "cpo-api-gateway already running with PID $PID"
    exit 0
  fi
fi

nohup "$KRAKEND_BIN" run -c "$CONFIG_FILE" >"$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

echo "cpo-api-gateway started with KrakenD binary"
echo "PID: $(cat "$PID_FILE")"
echo "Log: $LOG_FILE"
