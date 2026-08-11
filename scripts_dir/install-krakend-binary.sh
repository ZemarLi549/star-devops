#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
INSTALL_DIR="${KRAKEND_INSTALL_DIR:-$ROOT_DIR/cpo-api-gateway/.local/bin}"
VERSION="${KRAKEND_VERSION:-2.13.8}"

case "$(uname -m)" in
  x86_64|amd64)
    ARCH_SUFFIX="amd64_generic-linux"
    ;;
  aarch64|arm64)
    ARCH_SUFFIX="arm64_generic-linux"
    ;;
  *)
    echo "Unsupported architecture: $(uname -m)" >&2
    exit 1
    ;;
esac

DOWNLOAD_URL="${KRAKEND_DOWNLOAD_URL:-https://repo.krakend.io/bin/krakend_${VERSION}_${ARCH_SUFFIX}.tar.gz}"

TMP_DIR="$(mktemp -d)"
cleanup() {
  rm -rf "$TMP_DIR"
}
trap cleanup EXIT

mkdir -p "$INSTALL_DIR"

echo "Downloading KrakenD ${VERSION} from ${DOWNLOAD_URL}"
curl -fsSL "$DOWNLOAD_URL" -o "$TMP_DIR/krakend.tar.gz"
tar -xzf "$TMP_DIR/krakend.tar.gz" -C "$TMP_DIR"

KRAKEND_BIN="$(find "$TMP_DIR" -maxdepth 3 -type f -name krakend | head -n 1)"
if [[ -z "${KRAKEND_BIN:-}" ]]; then
  echo "Unable to find krakend binary in archive" >&2
  exit 1
fi

install -m 0755 "$KRAKEND_BIN" "$INSTALL_DIR/krakend"

echo "KrakenD binary installed at $INSTALL_DIR/krakend"
