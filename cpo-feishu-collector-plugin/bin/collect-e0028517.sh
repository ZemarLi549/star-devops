#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
export PLUGIN_USER_ID="${PLUGIN_USER_ID:-E0028517}"
exec bash "$ROOT_DIR/bin/collect-once.sh" --user "$PLUGIN_USER_ID" --no-upload
