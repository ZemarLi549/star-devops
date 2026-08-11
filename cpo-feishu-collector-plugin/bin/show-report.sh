#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PLUGIN_USER_ID="${PLUGIN_USER_ID:-${1:-E0028517}}"
USER_DIR="$ROOT_DIR/data/$PLUGIN_USER_ID"

if [ ! -d "$USER_DIR" ]; then
  echo "report directory not found: $USER_DIR"
  exit 1
fi

echo "user dir: $USER_DIR"
echo
find "$USER_DIR" -maxdepth 4 -type f | sort
echo

REPORT_FILE="$USER_DIR/reports/daily/latest-report.md"
if [ -f "$REPORT_FILE" ]; then
  echo "---- $REPORT_FILE ----"
  cat "$REPORT_FILE"
  exit 0
fi

LEGACY_REPORT_FILE="$USER_DIR/latest-report.md"
if [ -f "$LEGACY_REPORT_FILE" ]; then
  echo "---- $LEGACY_REPORT_FILE ----"
  cat "$LEGACY_REPORT_FILE"
  exit 0
fi

echo "latest report not found"
exit 1
