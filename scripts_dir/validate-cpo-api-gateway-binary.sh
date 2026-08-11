#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
KRAKEND_BIN="${KRAKEND_BIN:-$ROOT_DIR/cpo-api-gateway/.local/bin/krakend}"

if [[ ! -x "$KRAKEND_BIN" ]]; then
  echo "KrakenD binary not found at $KRAKEND_BIN" >&2
  echo "Run scripts_dir/install-krakend-binary.sh first." >&2
  exit 1
fi

"$KRAKEND_BIN" check -d -c "$ROOT_DIR/cpo-api-gateway/krakend.json"
