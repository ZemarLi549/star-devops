#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/cpo-api-gateway/docker-compose.yml"

docker compose -f "$COMPOSE_FILE" down
echo "cpo-api-gateway stopped"
