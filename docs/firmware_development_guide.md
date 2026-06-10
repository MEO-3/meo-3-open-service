# Firmware Development Guide

> This guide describes the current MEO BLE provisioning contract. It may change as the hardware, firmware, and service architecture evolve.

## Provisioning Overview

MEO devices are expected to ship with firmware already flashed. When Wi-Fi is not configured, the device should enter BLE provisioning mode. The gateway discovers the device over BLE, then uses the Rust `blemqtt` service to read device identity, resolve the device profile, and write Wi-Fi settings.

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
| Provision status | `7f5a0003-0f23-4b6a-9f5e-3c2a9f7e0100` | Read, Notify recommended |
| Profile ID | `7f5a0004-0f23-4b6a-9f5e-3c2a9f7e0100` | Read |

## Data Format

Use UTF-8 strings for this version.

Device MAC read result:

```text
AA:BB:CC:DD:EE:FF
```

Profile ID read result:

```text
meo-profile-temp-v1
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

Recommended states:

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
3. Gateway reads device MAC.
4. Gateway reads profile ID.
5. Gateway writes Wi-Fi config.
6. Firmware stores credentials and attempts to join Wi-Fi.
7. Gateway reads or subscribes to provisioning status.
8. Firmware switches to normal online mode after successful Wi-Fi connection.

## Device Profile Contract

MEO is moving toward profile-driven DIY devices. A device profile is the reusable definition of a project: what the hardware exposes, what values can be read or written, and how the gateway can route device data.

The firmware does not need to store the full profile. For this version, firmware only needs to expose a `profileId` during BLE provisioning. The gateway uses that ID to find an imported `device-profile.json` or `device-profile.yaml`.

Use profiles for project identity, not fixed factory identity. A handmade classroom sensor, robot, or LED controller can all use profiles created by teachers, students, or makers.

Current profile fields:

- `profileId`
- `name`: friendly project name, such as `Classroom Temperature Sensor`
- `description`: short human-readable summary
- `version`
- `provisionTransportType`
- `transportType`
- `deviceType`
- `capabilities`

Example profile:

```json
{
  "profileId": "meo-profile-temp-v1",
  "name": "Classroom Temperature Sensor",
  "description": "Reads temperature from a DIY sensor board.",
  "version": 1,
  "provisionTransportType": 2,
  "transportType": 1,
  "deviceType": 1,
  "capabilities": [
    {
      "capabilityId": 1,
      "permission": 4,
      "name": "Temperature",
      "description": "Current room temperature.",
      "valueType": "number",
      "unit": "celsius",
      "minValue": "-20",
      "maxValue": "80",
      "stepValue": "0.1"
    }
  ]
}
```

## Capability Concept

A capability is one thing the device can expose. It can be a sensor value, actuator value, or action.

Capability fields:

- `capabilityId`
- `permission`
- `name`
- `description`
- `valueType`
- `unit`
- `minValue`
- `maxValue`
- `stepValue`

`permission` is an access mode, not a user/account system. Use the simple bit flags from `PermissionType`:

| Permission | Value | Meaning |
| --- | ---: | --- |
| `NONE` | `0` | Capability is declared but not accessible yet |
| `EXECUTE` | `1` | Capability can run an action |
| `WRITE` | `2` | Capability accepts values from the service |
| `READ` | `4` | Capability publishes or returns values |

Permissions can be combined. For example, a readable and writable LED brightness capability uses `READ + WRITE`, so `permission` is `6`.

Recommended capability examples:

| Capability | Permission | Value type | Unit |
| --- | ---: | --- | --- |
| Temperature sensor | `4` | `number` | `celsius` |
| LED brightness | `6` | `number` | `percent` |
| Buzzer beep action | `1` | `action` | empty |
| Button state | `4` | `boolean` | empty |

Keep capability names friendly because they may appear in child-facing UI. Keep `capabilityId` stable because transport payloads can use it to identify which value changed.

## Firmware Behavior

When Wi-Fi is missing or invalid, start BLE provisioning mode automatically.

After receiving Wi-Fi config:

1. Validate the payload.
2. Save credentials only if the payload is valid.
3. Update provision status to `received`.
4. Attempt Wi-Fi connection.
5. Update status to `connected` or `failed`.
6. On success, stop provisioning mode or reboot into normal mode.

If connection fails, keep BLE provisioning available so the gateway can retry.

## Notes

- The BLE address is only a temporary transport address.
- The MAC address is the stable device identity for this version.
- The profile ID tells the service which DIY device profile is being provisioned.
- Do not require cloud access for provisioning.
- Keep provisioning responses short and easy to parse.
