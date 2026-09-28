#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PLUGIN_USER_ID="${PLUGIN_USER_ID:-}"
NO_UPLOAD="${COLLECTOR_NO_UPLOAD:-true}"

while [ $# -gt 0 ]; do
  case "$1" in
    --user)
      PLUGIN_USER_ID="${2:-}"
      shift 2
      ;;
    --upload)
      NO_UPLOAD="false"
      shift
      ;;
    --no-upload)
      NO_UPLOAD="true"
      shift
      ;;
    *)
      echo "unknown argument: $1"
      echo "usage: bash bin/collect-once.sh [--user E0028517] [--upload|--no-upload]"
      exit 1
      ;;
  esac
done

PLUGIN_USER_ID="${PLUGIN_USER_ID:-E0028517}"
export PLUGIN_USER_ID

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

ARGS=(--once)
if [ "$NO_UPLOAD" = "true" ]; then
  ARGS+=(--no-upload)
fi

"$BINARY" "${ARGS[@]}"
