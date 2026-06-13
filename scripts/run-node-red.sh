#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="${MEO_ENV_FILE:-$ROOT_DIR/config/meo.env}"

if [ -f "$ENV_FILE" ]; then
    set -a
    # shellcheck disable=SC1090
    . "$ENV_FILE"
    set +a
fi

export MEO_DATA_DIR="${MEO_DATA_DIR:-$ROOT_DIR/data}"
export MEO_NODE_RED_USER_DIR="${MEO_NODE_RED_USER_DIR:-$MEO_DATA_DIR/node-red}"
export NODE_ENV="${NODE_ENV:-${MEO_NODE_RED_ENV:-development}}"
mkdir -p "$MEO_NODE_RED_USER_DIR" "$ROOT_DIR/logs"

NODE_RED_DIR="${MEO_NODE_RED_DIR:-$ROOT_DIR/node-red}"
SETTINGS_FILE="${MEO_NODE_RED_SETTINGS:-$ROOT_DIR/config/node-red/settings.js}"

if [ ! -f "$NODE_RED_DIR/packages/node_modules/node-red/red.js" ]; then
    echo "Node-RED runtime not found at $NODE_RED_DIR." >&2
    exit 1
fi

exec node "$NODE_RED_DIR/packages/node_modules/node-red/red.js" \
    --settings "$SETTINGS_FILE" \
    --userDir "$MEO_NODE_RED_USER_DIR" \
    "$@"
