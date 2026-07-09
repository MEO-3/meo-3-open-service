package org.thingai.app.meo.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.thingai.app.meo.blemqtt.BlemqttClient;
import org.thingai.app.meo.blemqtt.BlemqttCommand;
import org.thingai.app.meo.blemqtt.BlemqttError;
import org.thingai.app.meo.blemqtt.BlemqttEvent;
import org.thingai.app.meo.blemqtt.BlemqttOp;
import org.thingai.app.meo.blemqtt.BlemqttReply;
import org.thingai.app.meo.define.BleUuid;
import org.thingai.app.meo.define.ErrorCode;
import org.thingai.app.meo.define.ProvisionStatus;
import org.thingai.app.meo.define.TransportType;
import org.thingai.app.meo.api.dto.MeoDeviceResponse;
import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.app.meo.entity.MeoDeviceCapability;
import org.thingai.app.meo.entity.MeoDeviceProvision;
import org.thingai.app.meo.callback.ProvisionEventListener;
import org.thingai.app.meo.callback.RequestCallback;
import org.thingai.app.meo.util.JsonUtil;
import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// Gateway-led BLE provisioning. Each method runs synchronously: it blocks on the
// blemqtt command reply (`send(...).get()`) and reads as straight-line code.
// The one exception is the Wi-Fi join status, which arrives as gatt.notification
// events on the MQTT callback thread; setupDevice waits on those with a
// blocking poll, not async composition.
public class MeoProvisionHandler {
    private static final String TAG = "MeoProvisionHandler";
    private static final String DEFAULT_ENCODING = "utf8";
    private static final String EVENT_GATT_NOTIFICATION = "gatt.notification";
    private static final String STATE_CONNECTED = "connected";
    private static final String STATE_FAILED = "failed";

    // Progress events pushed to the registered listener (the SSE endpoint).
    public static final String EVENT_PROVISION_STATUS = "provision.status";
    public static final String EVENT_SCAN_STARTED = "scan.started";
    public static final String EVENT_SCAN_DEVICE_FOUND = "scan.device_found";
    public static final String EVENT_SCAN_COMPLETED = "scan.completed";
    public static final String EVENT_DEVICE_PERSISTED = "device.persisted";

    // How long to wait for the device to report a terminal Wi-Fi join state after
    // credentials are written.
    private static final long WIFI_JOIN_TIMEOUT_MS = 45_000;

    private final BlemqttClient blemqttClient;
    private final Dao dao;

    // Single in-flight provisioning session (buffer). BLE is one device at a
    // time: connect opens it, setupDevice advances it, persistDevice clears it.
    // null when idle.
    private MeoDeviceProvision session;

    // Optional progress observer (the SSE endpoint). Emits are fire-and-forget
    // and never affect the provisioning flow; may be invoked from request
    // threads and the MQTT callback thread.
    private volatile ProvisionEventListener eventListener;

    public MeoProvisionHandler(BlemqttClient blemqttClient, Dao dao) {
        this.blemqttClient = blemqttClient;
        this.dao = dao;
    }

    public void setEventListener(ProvisionEventListener listener) {
        this.eventListener = listener;
    }

    // Snapshot of the in-flight session for late/reconnecting SSE clients.
    public synchronized MeoDeviceProvision currentSession() {
        return session;
    }

    // Scan for MEO provisionable devices. The BLE service runs the whole
    // discovery window itself and returns the devices found in the scan.start
    // reply (`result.devices`), so this blocks on the reply for ~timeoutMs — no
    // events are involved. timeoutMs must stay below the blemqtt request
    // timeout (15s default) or the reply future times out first. Synchronized
    // like the session steps: BLE is one radio, and serializing every op lets
    // the blemqtt client hold a single event callback.
    public synchronized void scan(int timeoutMs, String namePrefix, RequestCallback<JsonObject[]> callback) {
        ILog.i(TAG, "scan", "timeoutMs=" + timeoutMs, "namePrefix=" + namePrefix);

        JsonObject params = new JsonObject();
        params.addProperty("timeoutMs", timeoutMs);
        params.addProperty("serviceUuid", BleUuid.MEO_DEVICE_PROVISION_SERVICE);
        if (namePrefix != null && !namePrefix.isEmpty()) {
            params.addProperty("namePrefix", namePrefix);
        }

        emit(EVENT_SCAN_STARTED, params);
        try {
            BlemqttReply reply = sendBlocking(BlemqttCommand.create(BlemqttOp.SCAN_START, params));
            JsonObject[] devices = parseScanDevices(reply);
            for (JsonObject device : devices) {
                emit(EVENT_SCAN_DEVICE_FOUND, device);
            }
            ILog.i(TAG, "scan complete", "count=" + devices.length);
            emit(EVENT_SCAN_COMPLETED, devices);
            callback.onResult(devices, "scan complete");
        } catch (RuntimeException e) {
            ILog.e(TAG, "scan failed", e);
            callback.onFailure(ErrorCode.PROV_SCAN_FAILED, failureMessage(e, "scan failed"));
        }
    }

    private JsonObject[] parseScanDevices(BlemqttReply reply) {
        JsonElement result = reply.getResult();
        if (result == null || !result.isJsonObject()) {
            return new JsonObject[0];
        }
        JsonElement devices = result.getAsJsonObject().get("devices");
        if (devices == null || !devices.isJsonArray()) {
            return new JsonObject[0];
        }
        JsonObject[] parsed = JsonUtil.fromJson(devices.toString(), JsonObject[].class);
        return parsed != null ? parsed : new JsonObject[0];
    }

    // Connect to a scanned device over BLE and read its identity + capabilities.
    // Opens the provisioning session (one device at a time) and leaves BLE
    // connected for setupDevice. Any prior unfinished session is reclaimed first,
    // since BLE is single-device. Callback-based to match scan/setupDevice.
    public synchronized void connect(String bleAddress, RequestCallback<MeoDeviceProvision> callback) {
        ILog.i(TAG, "connect", "bleAddress=" + bleAddress);
        if (isEmpty(bleAddress)) {
            callback.onFailure(ErrorCode.PROV_CONNECT_FAILED, "ble address is required");
            return;
        }
        reset();

        MeoDeviceProvision provision = new MeoDeviceProvision();
        provision.setBleAddress(bleAddress);
        try {
            bleConnect(provision);
            readMac(provision);
            readCapabilities(provision);
            updateStatus(provision, ProvisionStatus.STATUS_CONNECTED_BLE, "device connected");
            session = provision;
            callback.onResult(provision, "device connected");
        } catch (RuntimeException e) {
            ILog.e(TAG, "connect failed", e);
            updateStatus(provision, ProvisionStatus.STATUS_FAILED, failureMessage(e, "connect failed"));
            safeDisconnect(provision);
            callback.onFailure(ErrorCode.PROV_CONNECT_FAILED, failureMessage(e, "connect failed"));
        }
    }

    // Write Wi-Fi (and future device config) to the connected device and wait for
    // it to join. Requires an open session from connect(). On success the device
    // is online, BLE is released, and the session is kept (status = provisioned)
    // for persistDevice. On failure the session stays open with BLE connected so
    // the client can retry with corrected credentials.
    public synchronized void setupDevice(String ssid, String password, RequestCallback<MeoDeviceProvision> callback) {
        ILog.i(TAG, "setupDevice", addressLog(session));
        if (session == null) {
            callback.onFailure(ErrorCode.PROV_SETUP_FAILED, "no device connected; call connect first");
            return;
        }
        if (isEmpty(ssid)) {
            callback.onFailure(ErrorCode.PROV_SETUP_FAILED, "wifi ssid is required");
            return;
        }

        MeoDeviceProvision current = session;
        BlockingQueue<Object> terminalState = new LinkedBlockingQueue<>();
        blemqttClient.onEvent(event -> onStatusNotification(current, event, terminalState));
        try {
            subscribeStatus(current);
            writeWifi(current, ssid, password);
            awaitWifiJoin(current, terminalState);
            updateStatus(current, ProvisionStatus.STATUS_PROVISIONED, "device provisioned");
            safeDisconnect(current);
            callback.onResult(current, "device provisioned");
        } catch (RuntimeException e) {
            ILog.e(TAG, "setupDevice failed", e);
            updateStatus(current, ProvisionStatus.STATUS_FAILED, failureMessage(e, "setup failed"));
            callback.onFailure(ErrorCode.PROV_SETUP_FAILED, failureMessage(e, "setup failed"));
        } finally {
            blemqttClient.removeEventCallback();
        }
    }

    // Persist the provisioned device (row + capability rows) from the session
    // buffer and return its view. Requires setupDevice to have completed
    // (buffered status = provisioned). Clears the session on success.
    public synchronized void persistDevice(RequestCallback<MeoDeviceResponse> callback) {
        ILog.i(TAG, "persistDevice", addressLog(session));
        if (session == null) {
            callback.onFailure(ErrorCode.PROV_PRESIST_FAILED, "no device connected; call connect first");
            return;
        }
        if (session.getStatus() != ProvisionStatus.STATUS_PROVISIONED) {
            callback.onFailure(ErrorCode.PROV_PRESIST_FAILED, "device not set up; call setupDevice first");
            return;
        }

        MeoDeviceProvision current = session;
        try {
            MeoDevice device = saveDevice(current);
            persistCapabilities(device.getDeviceId(), current.getCapabilities());
            session = null;
            MeoDeviceResponse response = MeoDeviceResponse.of(device, current.getCapabilities());
            emit(EVENT_DEVICE_PERSISTED, response);
            callback.onResult(response, "device persisted");
        } catch (RuntimeException e) {
            ILog.e(TAG, "persistDevice failed", e);
            callback.onFailure(ErrorCode.PROV_PRESIST_FAILED, failureMessage(e, "persist failed"));
        }
    }

    // Release any in-flight session and its BLE link. BLE is single-device, so a
    // new connect reclaims a previous, unfinished session.
    private void reset() {
        if (session != null) {
            safeDisconnect(session);
            session = null;
        }
    }

    // Upsert the device row (identity + model/firmware). The MAC is the stable
    // device identity; capability rows are written separately.
    private MeoDevice saveDevice(MeoDeviceProvision provision) {
        if (isEmpty(provision.getMacAddress())) {
            throw new IllegalStateException("device MAC is required to persist device");
        }
        MeoDevice device = new MeoDevice();
        device.setDeviceId(provision.getMacAddress());
        device.setMacAddress(provision.getMacAddress());
        device.setTransportType(TransportType.WIFI_LAN);
        device.setModel(provision.getModel());
        device.setFwVersion(provision.getFwVersion());

        dao.insertOrUpdate(device);
        ILog.i(TAG, "persistDevice", "persisted deviceId=" + device.getDeviceId());
        return device;
    }

    // Replace the device's capability rows with the reported set (delete-all
    // then insert), so re-provisioning refreshes rather than accumulates.
    private void persistCapabilities(String deviceId, int[] capabilities) {
        dao.deleteByColumn(MeoDeviceCapability.class, "deviceId", deviceId);
        if (capabilities == null || capabilities.length == 0) {
            return;
        }
        MeoDeviceCapability[] rows = new MeoDeviceCapability[capabilities.length];
        for (int i = 0; i < capabilities.length; i++) {
            MeoDeviceCapability row = new MeoDeviceCapability();
            row.setDeviceId(deviceId);
            row.setCapabilityId(capabilities[i]);
            rows[i] = row;
        }
        dao.insertBatch(rows);
        ILog.i(TAG, "persistCapabilities", "deviceId=" + deviceId, "count=" + capabilities.length);
    }

    // Set the buffered status (and message, when given), then push the session
    // snapshot to the listener as a provision.status event.
    private void updateStatus(MeoDeviceProvision provision, int status, String message) {
        provision.setStatus(status);
        if (message != null) {
            provision.setMessage(message);
        }
        emit(EVENT_PROVISION_STATUS, provision);
    }

    private void emit(String event, Object payload) {
        ProvisionEventListener listener = eventListener;
        if (listener == null) {
            return;
        }
        try {
            listener.onEvent(event, payload);
        } catch (RuntimeException e) {
            ILog.w(TAG, "emit", "event listener failed", e.getMessage());
        }
    }

    // --- Steps ----------------------------------------------------------------

    private void bleConnect(MeoDeviceProvision provision) {
        updateStatus(provision, ProvisionStatus.STATUS_CONNECTING_BLE, null);
        sendBlocking(BlemqttCommand.create(BlemqttOp.DEVICE_CONNECT, addressParams(provision)));
        ILog.i(TAG, "connect", "connected", addressLog(provision));
    }

    private void readMac(MeoDeviceProvision provision) {
        updateStatus(provision, ProvisionStatus.STATUS_READING_MAC, null);
        String mac = readReplyValue(sendBlocking(gattRead(provision, BleUuid.MEO_DEVICE_MAC_CHAR)));
        provision.setMacAddress(mac);
        ILog.i(TAG, "readDeviceMac", "macAddress=" + mac);
    }

    // Read the capability report characteristic and record model, firmware
    // version, and the capability ids on the provision. Non-fatal: a
    // firmware/read/parse issue leaves the device provisionable with an empty
    // capability set, since the device is still usable on Wi-Fi and
    // re-provisioning refreshes it. Ids are kept verbatim (unknown ids are not
    // filtered) and later persisted one-per-row by persistCapabilities.
    private void readCapabilities(MeoDeviceProvision provision) {
        updateStatus(provision, ProvisionStatus.STATUS_READING_CAPABILITIES, null);
        try {
            String raw = readReplyValue(sendBlocking(gattRead(provision, BleUuid.MEO_DEVICE_CAPABILITIES_CHAR)));
            JsonObject report = JsonParser.parseString(raw).getAsJsonObject();

            JsonElement model = report.get("model");
            if (model != null && !model.isJsonNull()) {
                provision.setModel(model.getAsString());
            }
            JsonElement fw = report.get("fw");
            if (fw != null && !fw.isJsonNull()) {
                provision.setFwVersion(fw.getAsString());
            }

            provision.setCapabilities(parseCapabilities(report.get("capabilities")));
            ILog.i(TAG, "readCapabilities", "model=" + provision.getModel(),
                    "fw=" + provision.getFwVersion(), "count=" + provision.getCapabilities().length);
        } catch (RuntimeException e) {
            provision.setCapabilities(new int[0]);
            ILog.w(TAG, "readCapabilities", "failed; continuing with empty capabilities", e.getMessage());
        }
    }

    private int[] parseCapabilities(JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return new int[0];
        }
        JsonArray array = element.getAsJsonArray();
        int[] capabilities = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            capabilities[i] = array.get(i).getAsInt();
        }
        return capabilities;
    }

    private void subscribeStatus(MeoDeviceProvision provision) {
        sendBlocking(BlemqttCommand.create(BlemqttOp.GATT_SUBSCRIBE, gattParams(provision, BleUuid.MEO_PROVISION_STATUS_CHAR)));
        ILog.i(TAG, "subscribeStatus", "subscribed", addressLog(provision));
    }

    private void writeWifi(MeoDeviceProvision provision, String ssid, String password) {
        JsonObject wifiConfig = new JsonObject();
        wifiConfig.addProperty("ssid", ssid);
        wifiConfig.addProperty("password", password != null ? password : "");

        JsonObject params = gattParams(provision, BleUuid.MEO_WIFI_CONFIG_CHAR);
        params.addProperty("encoding", DEFAULT_ENCODING);
        params.addProperty("value", JsonUtil.toJson(wifiConfig));

        provision.setWifiSsid(ssid);
        updateStatus(provision, ProvisionStatus.STATUS_WRITING_WIFI, null);
        sendBlocking(BlemqttCommand.create(BlemqttOp.GATT_WRITE, params));
        updateStatus(provision, ProvisionStatus.STATUS_WRITING_WIFI, "Wi-Fi config written");
        ILog.i(TAG, "writeWifiConfig", "written", "ssid=" + ssid);
    }

    // Block until the device reports a terminal Wi-Fi state or the timeout hits.
    private void awaitWifiJoin(MeoDeviceProvision provision, BlockingQueue<Object> terminalState) {
        updateStatus(provision, ProvisionStatus.STATUS_READING_STATUS, null);
        Object result;
        try {
            result = terminalState.poll(WIFI_JOIN_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("interrupted while waiting for Wi-Fi join", e);
        }
        if (result == null) {
            throw new RuntimeException("timed out waiting for device to join Wi-Fi");
        }
        if (result instanceof Throwable) {
            throw new RuntimeException("device reported Wi-Fi join failed");
        }
    }

    // Release the BLE link. Transport cleanup only — it does not touch the
    // provisioning status, which the buffer must retain (e.g. PROVISIONED) for
    // persistDevice.
    private void safeDisconnect(MeoDeviceProvision provision) {
        try {
            sendBlocking(BlemqttCommand.create(BlemqttOp.DEVICE_DISCONNECT, addressParams(provision)));
            ILog.i(TAG, "disconnect", "disconnected", addressLog(provision));
        } catch (RuntimeException e) {
            ILog.w(TAG, "disconnect failed", e);
        }
    }

    // --- Events ---------------------------------------------------------------

    private void onStatusNotification(MeoDeviceProvision provision, BlemqttEvent event, BlockingQueue<Object> terminalState) {
        if (event == null || !EVENT_GATT_NOTIFICATION.equals(event.getEventType())) {
            return;
        }
        JsonElement payload = event.getPayload();
        if (payload == null || !payload.isJsonObject()) {
            return;
        }
        JsonObject notification = payload.getAsJsonObject();
        if (!matches(notification, "address", provision.getBleAddress())
                || !matches(notification, "characteristicUuid", BleUuid.MEO_PROVISION_STATUS_CHAR)) {
            return;
        }

        String state = extractState(notification.get("value"));
        if (state == null) {
            return;
        }
        provision.setProvisionStatus(state);
        provision.setMessage(state);
        ILog.i(TAG, "status", state, addressLog(provision));
        emit(EVENT_PROVISION_STATUS, provision);

        if (STATE_CONNECTED.equalsIgnoreCase(state)) {
            terminalState.offer(Boolean.TRUE);
        } else if (STATE_FAILED.equalsIgnoreCase(state)) {
            terminalState.offer(new RuntimeException("device reported Wi-Fi join failed"));
        }
    }

    // --- blemqtt helpers ------------------------------------------------------

    // Send a command and block for its reply. Throws if the command fails; the
    // blemqtt client applies its own request timeout.
    private BlemqttReply sendBlocking(BlemqttCommand command) {
        ILog.d(TAG, "send", command.getOp(), command.getRequestId());
        try {
            BlemqttReply reply = blemqttClient.send(command).get();
            if (!reply.isOk()) {
                throw toException(reply);
            }
            return reply;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("interrupted while sending " + command.getOp(), e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw cause instanceof RuntimeException ? (RuntimeException) cause : new RuntimeException(cause);
        }
    }

    private BlemqttCommand gattRead(MeoDeviceProvision provision, String characteristicUuid) {
        JsonObject params = gattParams(provision, characteristicUuid);
        params.addProperty("encoding", DEFAULT_ENCODING);
        return BlemqttCommand.create(BlemqttOp.GATT_READ, params);
    }

    private JsonObject gattParams(MeoDeviceProvision provision, String characteristicUuid) {
        JsonObject params = addressParams(provision);
        params.addProperty("serviceUuid", BleUuid.MEO_DEVICE_PROVISION_SERVICE);
        params.addProperty("characteristicUuid", characteristicUuid);
        return params;
    }

    private JsonObject addressParams(MeoDeviceProvision provision) {
        JsonObject params = new JsonObject();
        params.addProperty("address", provision.getBleAddress());
        return params;
    }

    private RuntimeException toException(BlemqttReply reply) {
        BlemqttError error = reply.getError();
        if (error == null) {
            return new RuntimeException("blemqtt command failed");
        }
        return new RuntimeException(error.getCode() + ": " + error.getMessage());
    }

    // Returns true when the field is absent or equals the expected value
    // (case-insensitive) — a missing field is not treated as a mismatch.
    private boolean matches(JsonObject object, String field, String expected) {
        JsonElement value = object.get(field);
        if (value == null || value.isJsonNull() || expected == null) {
            return true;
        }
        return expected.equalsIgnoreCase(value.getAsString());
    }

    private String extractState(JsonElement valueElement) {
        if (valueElement == null || valueElement.isJsonNull()) {
            return null;
        }
        try {
            String raw = valueElement.isJsonPrimitive() ? valueElement.getAsString() : valueElement.toString();
            JsonObject stateObject = JsonParser.parseString(raw).getAsJsonObject();
            JsonElement state = stateObject.get("state");
            return state != null && !state.isJsonNull() ? state.getAsString() : null;
        } catch (Exception e) {
            ILog.w(TAG, "extractState", "unparsable status value", valueElement.toString());
            return null;
        }
    }

    private String readReplyValue(BlemqttReply reply) {
        JsonElement result = reply.getResult();
        if (result == null || result.isJsonNull()) {
            return "";
        }
        if (result.isJsonPrimitive()) {
            return result.getAsString();
        }
        if (!result.isJsonObject()) {
            return result.toString();
        }

        JsonObject object = result.getAsJsonObject();
        String[] fields = {"value", "macAddress", "mac", "status", "state", "message"};
        for (String field : fields) {
            JsonElement value = object.get(field);
            if (value != null && !value.isJsonNull()) {
                return value.isJsonPrimitive() ? value.getAsString() : value.toString();
            }
        }
        return object.toString();
    }

    private String failureMessage(Throwable t, String fallback) {
        return t.getMessage() != null ? t.getMessage() : fallback;
    }

    private String addressLog(MeoDeviceProvision provision) {
        if (provision == null) {
            return "bleAddress=null";
        }
        return "bleAddress=" + provision.getBleAddress();
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
