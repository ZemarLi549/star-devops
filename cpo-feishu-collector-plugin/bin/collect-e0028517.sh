#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
export PLUGIN_USER_ID="${PLUGIN_USER_ID:-E0028517}"

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

  if [ -f "$ROOT_DIR/bin/build.sh" ]; then
    bash "$ROOT_DIR/bin/build.sh" >/dev/null
    if [ -x "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin" ]; then
      echo "$ROOT_DIR/dist/current/cpo-feishu-collector-plugin"
      return 0
    fi
  fi

  return 1
}

cd "$ROOT_DIR"
BINARY="$(resolve_binary)" || {
  echo "collector binary not found. Please use a release package or run source build first."
  exit 1
}

"$BINARY" --once --no-upload
