#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CONFIG_FILE="${CONFIG_FILE:-$ROOT_DIR/config/application.yaml}"
PLUGIN_USER_ID="${PLUGIN_USER_ID:-${1:-E0028517}}"

resolve_binary() {
  if [ -n "${BINARY_PATH:-}" ] && [ -x "${BINARY_PATH}" ]; then
    echo "${BINARY_PATH}"
    return 0
  fi

  if [ -x "$ROOT_DIR/bin/cpo-feishu-collector-plugin" ]; then
    echo "$ROOT_DIR/bin/cpo-feishu-collector-plugin"
    return 0
  fi

  if [ -x "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin" ]; then
    echo "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin"
    return 0
  fi

  bash "$ROOT_DIR/bin/build.sh" >/dev/null
  echo "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin"
}

if [ ! -f "$CONFIG_FILE" ] && [ -f "$ROOT_DIR/config/application.example.yaml" ]; then
  cp "$ROOT_DIR/config/application.example.yaml" "$CONFIG_FILE"
fi

BINARY="$(resolve_binary)"

cd "$ROOT_DIR"
export PLUGIN_USER_ID
exec "$BINARY" --authorize
