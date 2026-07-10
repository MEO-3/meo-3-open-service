# MQTT Device Messaging

> This document is the authoritative contract for runtime MQTT messaging between the MEO open
> service (gateway) and provisioned MEO devices: device control (commands) and device state
> (replies and events). It may change as the platform evolves.

This contract applies **after** provisioning, once a device is on Wi-Fi and connected to the
gateway's MQTT broker. BLE provisioning is a separate contract — see
`firmware_development_guide.md`. It is also separate from `blemqtt` (`blemqtt/v1/...`), which is
the gateway-internal channel between the Java service and the Rust BLE service.

The device learns the broker address (the gateway's LAN IPv4) during BLE provisioning — see the
network config payload in `firmware_development_guide.md`.

Out of scope for this version (deliberately deferred, not forgotten):

- **Device online/offline presence** — no status topic, no Last Will.
- **Broker credentials** — the broker is an open local listener for now; per-device
  authentication is not part of this contract yet.

## Identities

- `deviceId` — the device's stable identity, its Wi-Fi MAC as recorded at provisioning time.
  In topics it is normalized to lowercase hex without separators (e.g. `AA:BB:CC:DD:EE:FF` →
  `aabbccddeeff`).
- `requestId` — a gateway-generated string correlating one command with its reply. Opaque to the
  device; it must be echoed back verbatim.

## Topics

One topic namespace per device under a versioned prefix:

| Topic | Direction | QoS | Retained |
| --- | --- | --- | --- |
| `meo/v1/device/{deviceId}/command` | gateway → device | 1 | no |
| `meo/v1/device/{deviceId}/reply` | device → gateway | 0 | no |
| `meo/v1/device/{deviceId}/event` | device → gateway | 0 | no |

The device subscribes to exactly one topic — its own `command` — and publishes to two fixed
topic strings. No dynamic topic construction is needed in firmware.

The gateway subscribes with wildcards:

```text
meo/v1/device/+/reply
meo/v1/device/+/event
```

There is no retained state topic. The gateway is the source of truth for device state: it keeps a
last-value cache per `(deviceId, capabilityId)` fed by replies and events, and refreshes on demand
by sending a READ command.

## Payloads

All payloads are UTF-8 JSON, flat, one message per publish. Field order is not significant.
Unknown fields must be ignored, not treated as errors.

Capability IDs (`cap`) come from the shared command catalog — `MeoCmd.java` on the gateway,
`Meo3_Cmd.h` in firmware — and are carried verbatim as decimal integers in JSON.

### Command (gateway → device)

| Field | Type | Required | Meaning |
| --- | --- | --- | --- |
| `requestId` | string | yes | Correlation ID, echoed in the reply |
| `cap` | int | yes | Capability to invoke, from the shared catalog |
| `value` | int | for writes | Single scalar value; meaning is defined by the capability |

There is no separate verb field — the capability ID itself encodes the action:

- `MEO_READ_*` capabilities read a value; the reply carries it.
- `MEO_WRITE_*` capabilities write `value` to an actuator.
- `MEO_CMD_*` capabilities are generic device commands (e.g. STOP). Every MEO firmware supports
  them implicitly; they are not declared during provisioning.

Examples:

```json
{ "requestId": "c-42", "cap": 65281, "value": 1 }
```

```json
{ "requestId": "c-43", "cap": 61441 }
```

```json
{ "requestId": "c-44", "cap": 6 }
```

(`65281` = `MEO_WRITE_LED`, `61441` = `MEO_READ_TEMP`, `6` = `MEO_CMD_STOP`.)

`value` is always a **single number**. Multi-parameter actuators pack their parameters into one
integer with the encoding documented in the capability catalog (e.g. `MEO_WRITE_LED_RGB` takes
`0xRRGGBB`). Per-capability parameter schemas are intentionally avoided.

### Reply (device → gateway)

The device must publish exactly one reply per received command, echoing the `requestId`.

| Field | Type | Required | Meaning |
| --- | --- | --- | --- |
| `requestId` | string | yes | Echoed from the command |
| `ok` | bool | yes | Whether the command was accepted and executed |
| `cap` | int | on success | Echoed target capability |
| `value` | number | for READ, and WRITE with a resulting state | Resulting or read value |
| `error` | int | on failure | Numeric error code (see `MeoCmdErrCode.java`: 1 = bad request, 2 = unknown capability, 3 = handle failed) |

Examples:

```json
{ "requestId": "c-42", "ok": true, "cap": 65281, "value": 1 }
```

```json
{ "requestId": "c-43", "ok": true, "cap": 61441, "value": 23.5 }
```

```json
{ "requestId": "c-45", "ok": false, "error": 2 }
```

A command targeting a capability the device did not declare at provisioning must be answered with
`ok: false` and an error code — never silently dropped.

### Event (device → gateway)

Unsolicited messages from the device: periodic sensor readings and edge-triggered occurrences
(e.g. a button press).

| Field | Type | Required | Meaning |
| --- | --- | --- | --- |
| `cap` | int | yes | Capability the value belongs to |
| `value` | number | yes | Reading or event value |

Example:

```json
{ "cap": 61441, "value": 23.5 }
```

Events carry **no timestamp** — devices have no reliable clock. The gateway stamps arrival time
when it records the value.

## Delivery rules

- Commands use QoS 1. Replies and events are published at QoS 0 — the firmware MQTT library
  (PubSubClient) can only publish at QoS 0; the gateway's reply timeout covers a lost reply.
  Nothing is retained.
- The gateway correlates replies by `requestId` and applies a timeout; a command with no reply
  within the timeout is reported as failed to the caller. Because commands are QoS 1, firmware
  should treat command handling as effectively idempotent where possible.
- A reply with an unknown `requestId` (e.g. arriving after timeout) is logged and dropped by the
  gateway.
- Devices must answer malformed or incomplete commands with `ok: false` and an error code when a
  `requestId` is recoverable, or drop the message when it is not.

## Responsibilities

Gateway (Java service):

- Publishes commands and awaits replies (`requestId` → future, with timeout), mirroring the
  `BlemqttClient` pattern.
- Subscribes to `reply` and `event` wildcards, maintains the last-value state cache per
  `(deviceId, capabilityId)`, and exposes control and state over the HTTP API.

Firmware (`meo-3-arduino`):

- After Wi-Fi is up, connects to the broker, subscribes to its `command` topic, and dispatches
  commands to maker-registered capability handlers.
- Builds and publishes the reply for every command automatically.
- Exposes a simple publish API for readings/events (`cap` + `value`).
