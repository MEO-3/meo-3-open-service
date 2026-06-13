#!/usr/bin/env bash
set -euo pipefail

# Packages the MEO open service into a single self-contained, runnable tree:
#
#   meo-open-service/
#     bin/  lib/                     Java service (from ./gradlew installDist)
#     ble/meo-3-neo-ble-service      Rust BLE provisioning service
#     node-red/                      vendored Node-RED runtime (dev deps pruned)
#     config/                        meo.env + node-red/settings.js
#     scripts/run-*.sh               launchers
#
# The MQTT broker (Mosquitto) is a system dependency and is intentionally NOT
# bundled. Build inputs must exist first:
#   ./gradlew installDist
#   (cd node-red && npm install)
#   (cd rust/meo-3-neo-ble-service && make build-arm64)   # or set MEO_BLE_BIN

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

PACKAGE_NAME="${MEO_PACKAGE_NAME:-meo-open-service}"
DIST_DIR="$ROOT_DIR/dist"
STAGE_DIR="$DIST_DIR/$PACKAGE_NAME"

JAVA_DIST_DIR="$ROOT_DIR/build/install/meo-open-service"
NODE_RED_DIR="$ROOT_DIR/node-red"
CONFIG_DIR="$ROOT_DIR/config"

BLE_DIR="$ROOT_DIR/rust/meo-3-neo-ble-service"
BLE_BIN_NAME="meo-3-neo-ble-service"
BLE_TARGET="${MEO_BLE_TARGET:-aarch64-unknown-linux-gnu}"

# --- Locate the Rust BLE binary (prefer a release build for the gateway target).
resolve_ble_bin() {
    if [ -n "${MEO_BLE_BIN:-}" ]; then
        printf '%s\n' "$MEO_BLE_BIN"
        return
    fi
    local candidates=(
        "$BLE_DIR/target/$BLE_TARGET/release/$BLE_BIN_NAME"
        "$BLE_DIR/target/release/$BLE_BIN_NAME"
        "$BLE_DIR/target/$BLE_TARGET/debug/$BLE_BIN_NAME"
        "$BLE_DIR/target/debug/$BLE_BIN_NAME"
    )
    local c
    for c in "${candidates[@]}"; do
        if [ -x "$c" ]; then
            printf '%s\n' "$c"
            return
        fi
    done
    return 1
}

# --- Preflight checks (fail early with actionable messages).
if [ ! -d "$JAVA_DIST_DIR" ]; then
    echo "Java service distribution not found at $JAVA_DIST_DIR." >&2
    echo "Build it first with: ./gradlew installDist" >&2
    exit 1
fi

if [ ! -f "$NODE_RED_DIR/package.json" ]; then
    echo "Node-RED source not found at $NODE_RED_DIR." >&2
    exit 1
fi

if [ ! -d "$NODE_RED_DIR/node_modules" ]; then
    echo "Node-RED dependencies are missing. Run 'npm install' inside node-red first." >&2
    exit 1
fi

if [ ! -f "$CONFIG_DIR/meo.env" ] || [ ! -f "$CONFIG_DIR/node-red/settings.js" ]; then
    echo "Config files not found under $CONFIG_DIR (need meo.env and node-red/settings.js)." >&2
    exit 1
fi

BLE_BIN=""
if [ "${MEO_SKIP_BLE:-0}" = "1" ]; then
    echo "MEO_SKIP_BLE=1: packaging without the Rust BLE service (provisioning will be unavailable)." >&2
elif BLE_BIN="$(resolve_ble_bin)"; then
    :
else
    echo "Rust BLE binary not found for target '$BLE_TARGET'." >&2
    echo "Build it first with: (cd rust/meo-3-neo-ble-service && make build-arm64)" >&2
    echo "Or set MEO_BLE_BIN=/path/to/$BLE_BIN_NAME, or MEO_SKIP_BLE=1 to omit it." >&2
    exit 1
fi

# --- Stage.
echo "Staging package at $STAGE_DIR..."
rm -rf "$STAGE_DIR"
mkdir -p "$STAGE_DIR/ble"

# Java service (bin/ + lib/ land at the stage root).
cp -R "$JAVA_DIST_DIR/." "$STAGE_DIR/"

# Config and launchers.
cp -R "$CONFIG_DIR" "$STAGE_DIR/config"
mkdir -p "$STAGE_DIR/scripts"
cp "$SCRIPT_DIR"/run-*.sh "$STAGE_DIR/scripts/"

# Rust BLE service.
if [ -n "$BLE_BIN" ]; then
    install -m 0755 "$BLE_BIN" "$STAGE_DIR/ble/$BLE_BIN_NAME"
fi

# Node-RED runtime, with dev-only content trimmed to keep the package lean.
echo "Copying Node-RED runtime..."
cp -R "$NODE_RED_DIR" "$STAGE_DIR/node-red"
rm -rf "$STAGE_DIR/node-red/test" \
       "$STAGE_DIR/node-red/.git" \
       "$STAGE_DIR/node-red/.github"
find "$STAGE_DIR/node-red" -maxdepth 1 -type f \
    \( -name '*.md' -o -name '.mocharc.json' -o -name '.nycrc.json' \
       -o -name 'eslint.config.js' -o -name 'jsdoc.json' -o -name 'nodemon.json' \
       -o -name 'CITATION.cff' \) -delete

if [ "${MEO_PRUNE_NODE_RED:-1}" = "1" ] && command -v npm >/dev/null 2>&1; then
    echo "Pruning Node-RED dev dependencies..."
    (cd "$STAGE_DIR/node-red" && npm prune --omit=dev --no-audit --no-fund >/dev/null) \
        || echo "warning: 'npm prune' failed; shipping full node_modules." >&2
else
    echo "Skipping Node-RED prune (set MEO_PRUNE_NODE_RED=1 and ensure npm is installed to enable)." >&2
fi

# --- Archive.
echo "Creating package archive..."
(cd "$DIST_DIR" && tar -czf "$PACKAGE_NAME.tar.gz" "$PACKAGE_NAME")

echo "Package created: $DIST_DIR/$PACKAGE_NAME.tar.gz"
echo "Staged size: $(du -sh "$STAGE_DIR" | cut -f1)"
