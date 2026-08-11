#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
DIST_DIR="${DIST_DIR:-$ROOT_DIR/dist/current}"

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

  if [ -n "${GOROOT:-}" ] && [ -x "${GOROOT}/bin/go" ]; then
    echo "${GOROOT}/bin/go"
    return 0
  fi

  if [ -x /usr/local/go/bin/go ]; then
    echo /usr/local/go/bin/go
    return 0
  fi

  return 1
}

GO_BIN="$(resolve_go_bin)" || {
  echo "go not found, please install Go 1.25+ or set GO_BIN, or use a prebuilt release package"
  exit 1
}

GO_ROOT_DIR="$(cd "$(dirname "$GO_BIN")/.." && pwd)"
export GOROOT="${GOROOT:-$GO_ROOT_DIR}"

GOOS_VALUE="${GOOS_VALUE:-$(env -u GOROOT GOROOT="$GO_ROOT_DIR" "$GO_BIN" env GOOS)}"
GOARCH_VALUE="${GOARCH_VALUE:-$(env -u GOROOT GOROOT="$GO_ROOT_DIR" "$GO_BIN" env GOARCH)}"
BINARY_NAME="cpo-feishu-collector-plugin"
if [ "$GOOS_VALUE" = "windows" ]; then
  BINARY_NAME="${BINARY_NAME}.exe"
fi

mkdir -p "$DIST_DIR"
echo "building ${GOOS_VALUE}/${GOARCH_VALUE} -> $DIST_DIR/$BINARY_NAME"
env -u GOROOT GOROOT="$GO_ROOT_DIR" CGO_ENABLED=0 GOOS="$GOOS_VALUE" GOARCH="$GOARCH_VALUE" \
  "$GO_BIN" build -o "$DIST_DIR/$BINARY_NAME" ./cmd/collector
echo "build done: $DIST_DIR/$BINARY_NAME"
