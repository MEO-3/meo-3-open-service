#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

"$SCRIPT_DIR/run-meo-service.sh" &
MEO_SERVICE_PID="$!"

cleanup() {
    kill "$MEO_SERVICE_PID" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

"$SCRIPT_DIR/run-node-red.sh"
