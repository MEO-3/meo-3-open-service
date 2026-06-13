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
mkdir -p "$MEO_DATA_DIR" "$ROOT_DIR/logs"

if [ -x "$ROOT_DIR/bin/meo-open-service" ]; then
    exec "$ROOT_DIR/bin/meo-open-service" "$@"
fi

if [ -x "$ROOT_DIR/build/install/meo-open-service/bin/meo-open-service" ]; then
    exec "$ROOT_DIR/build/install/meo-open-service/bin/meo-open-service" "$@"
fi

echo "MEO Java service launcher not found. Run ./gradlew installDist first." >&2
exit 1
