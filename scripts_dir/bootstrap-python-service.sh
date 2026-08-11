#!/usr/bin/env bash
set -euo pipefail

SERVICE_DIR="${1:?usage: bash scripts_dir/bootstrap-python-service.sh <service_dir>}"
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
TARGET_DIR="$ROOT_DIR/$SERVICE_DIR"
VENV_DIR="$TARGET_DIR/.venv"

if [ ! -d "$TARGET_DIR" ]; then
  echo "service dir not found: $TARGET_DIR"
  exit 1
fi

if [ ! -d "$VENV_DIR" ]; then
  python3 -m venv "$VENV_DIR"
fi

"$VENV_DIR/bin/pip" install --upgrade pip
"$VENV_DIR/bin/pip" install -r "$TARGET_DIR/requirements.txt"

echo "python service ready: $TARGET_DIR"
