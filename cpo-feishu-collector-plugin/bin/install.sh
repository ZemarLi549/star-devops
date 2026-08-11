#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CONFIG_FILE="${CONFIG_FILE:-$ROOT_DIR/config/application.yaml}"
PLUGIN_USER_ID="${PLUGIN_USER_ID:-${1:-E0028517}}"

mkdir -p \
  "$ROOT_DIR/logs" \
  "$ROOT_DIR/data/$PLUGIN_USER_ID/raw-chat" \
  "$ROOT_DIR/data/$PLUGIN_USER_ID/meta" \
  "$ROOT_DIR/data/$PLUGIN_USER_ID/reports/daily/history" \
  "$ROOT_DIR/data/$PLUGIN_USER_ID/reports/meeting/history" \
  "$ROOT_DIR/data/$PLUGIN_USER_ID/reports/document/history" \
  "$ROOT_DIR/data/$PLUGIN_USER_ID/archive" \
  "$ROOT_DIR/dist/current" \
  "$ROOT_DIR/releases"

if [ ! -f "$CONFIG_FILE" ] && [ -f "$ROOT_DIR/config/application.example.yaml" ]; then
  cp "$ROOT_DIR/config/application.example.yaml" "$CONFIG_FILE"
  echo "created default config: $CONFIG_FILE"
fi

if [ -x "$ROOT_DIR/bin/cpo-feishu-collector-plugin" ]; then
  echo "using bundled collector binary: $ROOT_DIR/bin/cpo-feishu-collector-plugin"
elif [ -f "$ROOT_DIR/bin/build.sh" ]; then
  bash "$ROOT_DIR/bin/build.sh"
else
  echo "collector binary and build script both not found"
  exit 1
fi

echo "install prepared"
echo "root: $ROOT_DIR"
echo "user: $PLUGIN_USER_ID"
echo "config: $CONFIG_FILE"
echo "data dir: $ROOT_DIR/data/$PLUGIN_USER_ID"
echo "binary dir: $ROOT_DIR/dist/current"
