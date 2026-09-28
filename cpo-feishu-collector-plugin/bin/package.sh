#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
RELEASES_DIR="${RELEASES_DIR:-$ROOT_DIR/releases}"
TMP_DIR="${TMP_DIR:-$ROOT_DIR/.tmp-package}"

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
  echo "go not found, please install Go 1.25+ or set GO_BIN before packaging"
  exit 1
}

GO_ROOT_DIR="$(cd "$(dirname "$GO_BIN")/.." && pwd)"
export GOROOT="${GOROOT:-$GO_ROOT_DIR}"

mkdir -p "$RELEASES_DIR"
rm -rf "$TMP_DIR"
mkdir -p "$TMP_DIR"

build_target() {
  local goos="$1"
  local goarch="$2"
  local ext="$3"
  local package_name="cpo-feishu-collector-plugin-${goos}-${goarch}"
  local stage_dir="$TMP_DIR/$package_name"
  local bin_name="cpo-feishu-collector-plugin${ext}"

  mkdir -p "$stage_dir/bin" "$stage_dir/config" "$stage_dir/docs"
  env -u GOROOT GOROOT="$GO_ROOT_DIR" CGO_ENABLED=0 GOOS="$goos" GOARCH="$goarch" \
    "$GO_BIN" build -o "$stage_dir/bin/$bin_name" ./cmd/collector

  cp "$ROOT_DIR/bin/install.sh" "$stage_dir/bin/"
  cp "$ROOT_DIR/bin/quick-deploy.sh" "$stage_dir/bin/"
  cp "$ROOT_DIR/bin/collect-once.sh" "$stage_dir/bin/"
  cp "$ROOT_DIR/bin/start.sh" "$stage_dir/bin/"
  cp "$ROOT_DIR/bin/stop.sh" "$stage_dir/bin/"
  cp "$ROOT_DIR/bin/show-report.sh" "$stage_dir/bin/"
  cp "$ROOT_DIR/bin/clean-history.sh" "$stage_dir/bin/"
  cp "$ROOT_DIR/config/application.example.yaml" "$stage_dir/config/"
  cp "$ROOT_DIR/README.md" "$stage_dir/"
  cp "$ROOT_DIR/docs/使用说明.md" "$stage_dir/docs/"
  cp "$ROOT_DIR/docs/安装部署与防火墙说明.md" "$stage_dir/docs/"
  cp "$ROOT_DIR/docs/脚本集合.md" "$stage_dir/docs/"

  if [ "$goos" = "windows" ]; then
    (
      cd "$TMP_DIR"
      zip -rq "$RELEASES_DIR/${package_name}.zip" "$package_name"
    )
  else
    tar -C "$TMP_DIR" -czf "$RELEASES_DIR/${package_name}.tar.gz" "$package_name"
  fi
}

build_target linux amd64 ""
build_target darwin arm64 ""
build_target windows amd64 ".exe"

echo "packages created in $RELEASES_DIR"
