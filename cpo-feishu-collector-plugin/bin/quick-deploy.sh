#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PLUGIN_USER_ID="${PLUGIN_USER_ID:-${1:-E0028517}}"

bash "$ROOT_DIR/bin/install.sh" "$PLUGIN_USER_ID"
echo
echo "authorize first with your own Feishu app:"
echo "  bash $ROOT_DIR/bin/authorize.sh $PLUGIN_USER_ID"
echo
echo "after authorization, start the collector:"
echo "  PLUGIN_USER_ID=$PLUGIN_USER_ID bash $ROOT_DIR/bin/start.sh"

echo
echo "quick deploy finished"
echo "user: $PLUGIN_USER_ID"
echo "show report: bash $ROOT_DIR/bin/show-report.sh $PLUGIN_USER_ID"
