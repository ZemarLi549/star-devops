#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
DATA_ROOT="${PLUGIN_CACHE_DIR:-$ROOT_DIR/data}"
TARGET_USER=""
RAW_DAYS="${CLEAN_RAW_KEEP_DAYS:-15}"
SUMMARY_DAYS="${CLEAN_SUMMARY_KEEP_DAYS:-180}"
DRY_RUN="false"
ALL_USERS="false"

while [ $# -gt 0 ]; do
  case "$1" in
    --user)
      TARGET_USER="${2:-}"
      shift 2
      ;;
    --days)
      RAW_DAYS="${2:-15}"
      SUMMARY_DAYS="${2:-15}"
      shift 2
      ;;
    --raw-days)
      RAW_DAYS="${2:-15}"
      shift 2
      ;;
    --summary-days)
      SUMMARY_DAYS="${2:-180}"
      shift 2
      ;;
    --dry-run)
      DRY_RUN="true"
      shift
      ;;
    --all)
      ALL_USERS="true"
      shift
      ;;
    *)
      echo "unknown argument: $1"
      echo "usage: bash bin/clean-history.sh [--user E0028517] [--days 15] [--raw-days 15] [--summary-days 180] [--dry-run] [--all]"
      exit 1
      ;;
  esac
done

if [ ! -d "$DATA_ROOT" ]; then
  echo "data root not found: $DATA_ROOT"
  exit 0
fi

if [ "$ALL_USERS" != "true" ] && [ -z "$TARGET_USER" ]; then
  TARGET_USER="${PLUGIN_USER_ID:-E0028517}"
fi

clean_dir() {
  local dir="$1"
  local keep_days="$2"
  if [ ! -d "$dir" ]; then
    return 0
  fi

  echo "scan: $dir (keep ${keep_days}d)"
  if [ "$DRY_RUN" = "true" ]; then
    find "$dir" -type f ! -name '.gitkeep' -mtime +"$keep_days" -print
    return 0
  fi

  find "$dir" -type f ! -name '.gitkeep' -mtime +"$keep_days" -print -delete
  find "$dir" -type d -empty -delete
}

clean_user() {
  local user_dir="$1"
  clean_dir "$user_dir/raw-chat" "$RAW_DAYS"
  clean_dir "$user_dir/reports/daily/history" "$SUMMARY_DAYS"
  clean_dir "$user_dir/reports/meeting/history" "$SUMMARY_DAYS"
  clean_dir "$user_dir/reports/document/history" "$SUMMARY_DAYS"
  clean_dir "$user_dir/archive" "$SUMMARY_DAYS"
}

if [ "$ALL_USERS" = "true" ]; then
  for user_dir in "$DATA_ROOT"/*; do
    [ -d "$user_dir" ] || continue
    echo "clean user: $(basename "$user_dir")"
    clean_user "$user_dir"
  done
else
  USER_DIR="$DATA_ROOT/$TARGET_USER"
  if [ ! -d "$USER_DIR" ]; then
    echo "user data dir not found: $USER_DIR"
    exit 0
  fi
  echo "clean user: $TARGET_USER"
  clean_user "$USER_DIR"
fi

echo "clean finished"
