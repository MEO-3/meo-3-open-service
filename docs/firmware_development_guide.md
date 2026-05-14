# Firmware Development Guide

> This guide describes the current MEO BLE provisioning contract. It may change as the hardware, firmware, and service architecture evolve.

## Provisioning Overview

MEO devices are expected to ship with firmware already flashed. When Wi-Fi is not configured, the device should enter BLE provisioning mode. The gateway discovers the device over BLE, then uses the Rust `blemqtt` service to read device identity and write Wi-Fi settings.

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
| Product ID | `7f5a0004-0f23-4b6a-9f5e-3c2a9f7e0100` | Read |

## Data Format

Use UTF-8 strings for this version.

Device MAC read result:

```text
AA:BB:CC:DD:EE:FF
```

Product ID read result:

```text
meo-temp-v1
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
4. Gateway reads product ID.
5. Gateway writes Wi-Fi config.
6. Firmware stores credentials and attempts to join Wi-Fi.
7. Gateway reads or subscribes to provisioning status.
8. Firmware switches to normal online mode after successful Wi-Fi connection.

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
- The product ID tells the service what kind of MEO device is being provisioned.
- Do not require cloud access for provisioning.
- Keep provisioning responses short and easy to parse.
