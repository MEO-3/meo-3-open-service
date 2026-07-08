# Firmware Development Guide

> This guide describes the current MEO BLE provisioning contract. It may change as the hardware, firmware, and service architecture evolve.

## Provisioning Overview

MEO devices are expected to ship with firmware already flashed. When Wi-Fi is not configured, the device should enter BLE provisioning mode. The gateway discovers the device over BLE, then uses the Rust `blemqtt` service to read the device identity and write Wi-Fi settings.

Firmware does not talk to MQTT directly during provisioning. MQTT is only used between the Java service and the gateway BLE service.

## BLE Advertising

In provisioning mode, the device should advertise the MEO provisioning service UUID:

```text
7f5a0000-0f23-4b6a-9f5e-3c2a9f7e0100
```

The advertised name may use a setup-friendly prefix such as:

```text
MEO-Setup-
```

The service UUID is the primary discovery filter. The name prefix is optional and should only help humans recognize devices.

## GATT Service

Expose one primary provisioning service:

```text
MEO_DEVICE_PROVISION_SERVICE
7f5a0000-0f23-4b6a-9f5e-3c2a9f7e0100
```

Required characteristics:

| Purpose | UUID | Access |
| --- | --- | --- |
| Device MAC | `7f5a0001-0f23-4b6a-9f5e-3c2a9f7e0100` | Read |
| Wi-Fi config | `7f5a0002-0f23-4b6a-9f5e-3c2a9f7e0100` | Write |
| Provision status | `7f5a0003-0f23-4b6a-9f5e-3c2a9f7e0100` | Read, Notify |

The provision status characteristic must support Notify so the gateway can subscribe and receive live join progress instead of polling.

## Data Format

Use UTF-8 strings for this version.

Device MAC read result:

```text
AA:BB:CC:DD:EE:FF
```

Wi-Fi config write payload:

```json
{
  "ssid": "Classroom WiFi",
  "password": "secret"
}
```

Provision status read or notify payload:

```json
{
  "state": "connecting"
}
```

Status states, in order:

```text
received
connecting
connected
failed
```

## blemqtt Provision Flow

The Java service calls `blemqtt` commands through MQTT. The firmware only sees normal BLE operations.

1. Gateway scans for devices advertising the provisioning service UUID.
2. Gateway connects to the selected BLE device.
3. Gateway reads the device MAC.
4. Gateway subscribes to the provision status characteristic.
5. Gateway writes the Wi-Fi config.
6. Firmware stores the credentials and attempts to join Wi-Fi, notifying `received` -> `connecting` -> `connected`/`failed`.
7. Gateway waits for a terminal status notification, then disconnects.
8. Firmware switches to normal online mode after a successful Wi-Fi connection.

## Device Identity

- The BLE address is only a temporary transport address.
- The MAC address is the stable device identity. The gateway records a provisioned device by its MAC.
- Provisioning does not carry any product/profile identity. A device does not register a profile with the gateway.

## Device Capabilities

MEO devices no longer ship a per-product "device profile" or capability catalog. Instead, a device exposes what it can do through a **fixed, generic command catalog** shared by both sides:

- Firmware: `meo-3-arduino/lib/meo/define/Meo3_Cmd.h`
- Gateway: `org.thingai.app.meo.define.MeoCmd`

The two are kept in sync value-for-value, so a command means the same thing on both ends without a per-product translation table. The catalog has generic command verbs (`READ`, `WRITE`, `EXECUTE`, `EXECUTE_WITH_VAR`, `STOP`) and typed read/write targets (for example `READ_TEMP`, `READ_CO2`, `WRITE_LED`, `WRITE_SERVO`). A maker binds a new device to these existing constants rather than defining and registering a new product, so no profile import step is needed.

This runtime command exchange happens over MQTT after the device is online and is separate from the BLE provisioning contract above.

## Firmware Behavior

When Wi-Fi is missing or invalid, start BLE provisioning mode automatically.

After receiving Wi-Fi config:

1. Validate the payload.
2. Save credentials only if the payload is valid.
3. Update provision status to `received`.
4. Attempt the Wi-Fi connection.
5. Update status to `connected` or `failed`.
6. On success, stop provisioning mode or reboot into normal mode.

If connection fails, keep BLE provisioning available so the gateway can retry.

## Notes

- Do not require cloud access for provisioning.
- Keep provisioning responses short and easy to parse.
- Keep the provision status characteristic notifying on every state change.
```
