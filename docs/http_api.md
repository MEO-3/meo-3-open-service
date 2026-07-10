# MEO Open Service — HTTP API

Base URL: `http://<gateway>:7070` (port from `MEO_SERVICE_PORT`, default `7070`). All request and
response bodies are JSON. CORS is open (any host).

Interactive docs are served by the running service itself:

| Path | Description |
| --- | --- |
| `/openapi.json` | OpenAPI 3 specification (generated from controller annotations) |
| `/swagger` | Swagger UI for the spec above |

## Error model

Failed requests return an HTTP error status with a `MeoErrorResponse` body. `errorCode` is a
MEO application code (see `define/ErrorCode.java`), independent of the HTTP status:

```json
{ "errorCode": 200, "error": "device not found" }
```

| Code | Meaning |
| --- | --- |
| 0 | Generic error |
| 100–104 | Provisioning: generic / scan / connect / setup / persist failed |
| 200 | Device not found |
| 201 | Device update failed |

## Health

### `GET /`

Liveness check. Returns `"meow"`.

## Devices

CRUD over provisioned devices. Devices are **created by the provisioning flow** (below); these
endpoints list, edit user metadata, and remove them.

Device responses use the `MeoDeviceResponse` read model — the device row joined with its
capability ids:

```json
{
  "deviceId": "AA:BB:CC:DD:EE:FF",
  "name": "kitchen sensor",
  "description": "on the shelf",
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "deviceType": 2,
  "transportType": 1,
  "model": "meo-c3",
  "fwVersion": "1.0.0",
  "capabilities": [1, 3]
}
```

### `GET /api/v1/devices`

List all provisioned devices. Returns `MeoDeviceResponse[]` (empty array when none).

### `GET /api/v1/devices/{deviceId}`

Get one device. `404` with `errorCode` 200 if unknown.

### `PUT /api/v1/devices/{deviceId}`

Update a device's **user metadata only**: `name`, `description`, `deviceType`. Identity
(`deviceId`, `macAddress`) and firmware-reported fields (`model`, `fwVersion`, `transportType`)
are owned by the provisioning flow — if present in the body they are ignored. Returns the updated
`MeoDeviceResponse`; `404` if unknown.

```json
{ "name": "kitchen sensor", "description": "on the shelf", "deviceType": 2 }
```

### `DELETE /api/v1/devices/{deviceId}`

Delete a device and its capability rows. Returns the deleted `MeoDeviceResponse`; `404` if unknown.

## Provisioning

Stepped BLE provisioning: **scan → connect → setup → persist**. One in-flight session is shared
across the steps (BLE is single-device). The step calls block while the gateway's BLE service does
the work; open the SSE stream first to watch progress live.

### `GET /api/v1/provision/scan`

Step 1 — scan for nearby MEO devices advertising the provisioning BLE service.

Query parameters:

| Name | Type | Description |
| --- | --- | --- |
| `timeoutMs` | int | Scan duration in milliseconds (default `8000`) |
| `namePrefix` | string | Only return devices whose name starts with this prefix |

Returns the discovered devices as reported by the BLE service. `500` on scan failure.

### `POST /api/v1/provision/connect`

Step 2 — connect to a scanned device over BLE and read its identity (MAC address, model, firmware
version, capabilities).

```json
{ "bleAddress": "<address from a scan result>" }
```

Returns the provisioning session (`MeoDeviceProvision`). `400` if `bleAddress` is missing, `500`
on connect failure.

### `POST /api/v1/provision/setup`

Step 3 — write Wi-Fi credentials to the connected device and wait for its provision status.

```json
{ "ssid": "MyNetwork", "password": "optional for open networks" }
```

Returns the updated session. `400` if `ssid` is missing; `409` if the device rejected the
configuration or no session is in flight.

### `POST /api/v1/provision/persist`

Step 4 — save the successfully provisioned device to the gateway database and close the session.
No request body. Returns the persisted device as `MeoDeviceResponse`; `409` if there is no
provisioned device in flight.

### `GET /api/v1/provision/events` (SSE)

Server-sent events mirroring provisioning progress. Open the stream, then drive the step calls and
watch:

| Event | Payload |
| --- | --- |
| `scan.started` / `scan.completed` | scan lifecycle |
| `scan.device_found` | a discovered device |
| `provision.status` | session snapshot (`MeoDeviceProvision`) on every status change |
| `device.persisted` | the saved device |

On connect, the current in-flight session (if any) is replayed as a `provision.status` event, so
late or reconnecting clients see where the flow stands.

`MeoDeviceProvision.status` values (see `define/ProvisionStatus.java`): 0 generic, 1 created,
2 scanning, 3 connecting BLE, 4 connected, 5 reading MAC, 6 reading capabilities, 7 writing Wi-Fi,
8 reading status, 9 disconnecting, 10 disconnected, 11 **failed**, 12 **provisioned**.
