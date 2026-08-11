#!/usr/bin/env bash
set -euo pipefail

SERVICE_DIR="${1:?usage: bash scripts_dir/build-go-service.sh <service_dir>}"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
TARGET_DIR="$ROOT_DIR/$SERVICE_DIR"
DIST_DIR="${DIST_DIR:-$TARGET_DIR/dist/current}"

resolve_go_bin() {
  if [ -n "${GO_BIN:-}" ] && [ -x "${GO_BIN}" ]; then
    echo "${GO_BIN}"
    return 0
  fi

  if command -v go >/dev/null 2>&1; then
    command -v go
    return 0
  fi

  local g_bin="${G_BIN:-${HOME:-}/.g/bin/g}"
  local g_go_bin="${G_HOME_GO_BIN:-${HOME:-}/.g/go/bin/go}"

  if [ -x "$g_bin" ]; then
    "$g_bin" use >/dev/null 2>&1 || true
  fi

  if [ -x "$g_go_bin" ]; then
    echo "$g_go_bin"
    return 0
  fi

  return 1
}

GO_BIN="$(resolve_go_bin)" || {
  echo "go not found"
  exit 1
}

GO_ROOT_DIR="$(cd "$(dirname "$GO_BIN")/.." && pwd)"
mkdir -p "$DIST_DIR"

cd "$TARGET_DIR"
env -u GOROOT GOROOT="$GO_ROOT_DIR" CGO_ENABLED=0 \
  "$GO_BIN" build -o "$DIST_DIR/$(basename "$SERVICE_DIR")" ./cmd/gateway

echo "go service built: $DIST_DIR/$(basename "$SERVICE_DIR")"
