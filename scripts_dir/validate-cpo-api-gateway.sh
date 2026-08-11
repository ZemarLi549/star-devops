#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

docker run --rm \
  -v "$ROOT_DIR/cpo-api-gateway/krakend.json:/etc/krakend/krakend.json:ro" \
  krakend:2.13 \
  check -d -c /etc/krakend/krakend.json
