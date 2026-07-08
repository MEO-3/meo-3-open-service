package org.thingai.app.meo.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.thingai.app.meo.blemqtt.BlemqttCallback;
import org.thingai.app.meo.blemqtt.BlemqttClient;
import org.thingai.app.meo.blemqtt.BlemqttCommand;
import org.thingai.app.meo.blemqtt.BlemqttError;
import org.thingai.app.meo.blemqtt.BlemqttEvent;
import org.thingai.app.meo.blemqtt.BlemqttOp;
import org.thingai.app.meo.blemqtt.BlemqttReply;
import org.thingai.app.meo.define.BleUuid;
import org.thingai.app.meo.define.ProvisionStatus;
import org.thingai.app.meo.define.TransportType;
import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.app.meo.entity.MeoDeviceProvision;
import org.thingai.app.meo.handler.callback.RequestCallback;
import org.thingai.app.meo.util.JsonUtil;
import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// Gateway-led BLE provisioning. Each method runs synchronously: it blocks on the
// blemqtt command reply (`send(...).get()`) and reads as straight-line code.
// The two exceptions are scan results and Wi-Fi status, which arrive as events
// on the MQTT callback thread; those use a thread-safe collection and a blocking
// poll/sleep to wait, not async composition.
public class MeoProvisionHandler {
    private static final String TAG = "MeoProvisionHandler";
    private static final String DEFAULT_ENCODING = "utf8";
    private static final String EVENT_SCAN_DEVICE_FOUND = "scan.device_found";
    private static final String EVENT_GATT_NOTIFICATION = "gatt.notification";
    private static final String STATE_CONNECTED = "connected";
    private static final String STATE_FAILED = "failed";

    // How long to wait for the device to report a terminal Wi-Fi join state after
    // credentials are written.
    private static final long WIFI_JOIN_TIMEOUT_MS = 45_000;

    private final BlemqttClient blemqttClient;
    private final Dao dao;

    public MeoProvisionHandler(BlemqttClient blemqttClient, Dao dao) {
        this.blemqttClient = blemqttClient;
        this.dao = dao;
    }

    // Scan for MEO provisionable devices. Results arrive as `scan.device_found`
    // events, so accumulate them for `timeoutMs`, dedup by address, and return
    // the raw blemqtt payloads. Callback-based so the body can later become async
    // without changing the signature; the implementation is single-threaded for now.
    public void scan(int timeoutMs, String namePrefix, RequestCallback<List<JsonObject>> callback) {
        ILog.i(TAG, "scan", "timeoutMs=" + timeoutMs, "namePrefix=" + namePrefix);

        JsonObject params = new JsonObject();
        params.addProperty("timeoutMs", timeoutMs);
        params.addProperty("serviceUuid", BleUuid.MEO_DEVICE_PROVISION_SERVICE);
        if (namePrefix != null && !namePrefix.isEmpty()) {
            params.addProperty("namePrefix", namePrefix);
        }

        Map<String, JsonObject> found = new ConcurrentHashMap<>();
        BlemqttCallback<BlemqttEvent> scanListener = event -> collectScanResult(event, found);
        blemqttClient.onEvent(scanListener);
        try {
            sendBlocking(BlemqttCommand.create(BlemqttOp.SCAN_START, params));
            Thread.sleep(timeoutMs);
            ILog.i(TAG, "scan complete", "count=" + found.size());
            callback.onResult(new ArrayList<>(found.values()), "scan complete");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            callback.onFailure(e, "scan interrupted");
        } catch (RuntimeException e) {
            callback.onFailure(e, "scan");
        } finally {
            blemqttClient.removeEventCallback(scanListener);
        }
    }

    // Full gateway-led provisioning: connect -> read MAC -> subscribe to status
    // -> write Wi-Fi -> await terminal join state -> disconnect -> persist device.
    // Callback-based so the body can later become async without changing the
    // signature; the implementation is single-threaded for now.
    public void provision(MeoDeviceProvision provision, String ssid, String password, RequestCallback<MeoDevice> callback) {
        ILog.i(TAG, "provision", addressLog(provision));
        if (provision == null || isEmpty(provision.getBleAddress())) {
            callback.onFailure(new IllegalArgumentException("ble address is required"), "provision");
            return;
        }
        if (isEmpty(ssid)) {
            callback.onFailure(new IllegalArgumentException("wifi ssid is required"), "provision");
            return;
        }

        BlockingQueue<Object> terminalState = new LinkedBlockingQueue<>();
        BlemqttCallback<BlemqttEvent> statusListener = event -> onStatusNotification(provision, event, terminalState);
        blemqttClient.onEvent(statusListener);

        boolean connected = false;
        try {
            connect(provision);
            connected = true;
            provision.setMacAddress(readMac(provision));
            subscribeStatus(provision);
            writeWifi(provision, ssid, password);
            awaitWifiJoin(provision, terminalState);

            MeoDevice device = persistDevice(provision);
            provision.setStatus(ProvisionStatus.STATUS_PROVISIONED);
            provision.setMessage("device provisioned");
            callback.onResult(device, "device provisioned");
        } catch (RuntimeException e) {
            ILog.e(TAG, "provision failed", e);
            provision.setStatus(ProvisionStatus.STATUS_FAILED);
            provision.setMessage(e.getMessage());
            callback.onFailure(e, "provision");
        } finally {
            blemqttClient.removeEventCallback(statusListener);
            if (connected) {
                safeDisconnect(provision);
            }
        }
    }

    // Build a device from the provisioning result and persist it. Callback-based
    // so the body can later become async without changing the signature.
    public void syncDevice(MeoDeviceProvision provision, RequestCallback<MeoDevice> callback) {
        ILog.i(TAG, "syncDevice", addressLog(provision));
        try {
            callback.onResult(persistDevice(provision), "device synced");
        } catch (RuntimeException e) {
            callback.onFailure(e, "sync device");
        }
    }

    // The MAC is the stable device identity.
    private MeoDevice persistDevice(MeoDeviceProvision provision) {
        if (provision == null || isEmpty(provision.getMacAddress())) {
            throw new IllegalStateException("device MAC is required to sync device");
        }
        MeoDevice device = new MeoDevice();
        device.setDeviceId(provision.getMacAddress());
        device.setMacAddress(provision.getMacAddress());
        device.setTransportType(TransportType.WIFI_LAN);

        dao.insertOrUpdate(device);
        ILog.i(TAG, "syncDevice", "persisted deviceId=" + device.getDeviceId());
        return device;
    }

    // --- Steps ----------------------------------------------------------------

    private void connect(MeoDeviceProvision provision) {
        provision.setStatus(ProvisionStatus.STATUS_CONNECTING_BLE);
        sendBlocking(BlemqttCommand.create(BlemqttOp.DEVICE_CONNECT, addressParams(provision)));
        provision.setStatus(ProvisionStatus.STATUS_CONNECTED_BLE);
        provision.setMessage("BLE device connected");
        ILog.i(TAG, "connect", "connected", addressLog(provision));
    }

    private String readMac(MeoDeviceProvision provision) {
        provision.setStatus(ProvisionStatus.STATUS_READING_MAC);
        String mac = readReplyValue(sendBlocking(gattRead(provision, BleUuid.MEO_DEVICE_MAC_CHAR)));
        ILog.i(TAG, "readDeviceMac", "macAddress=" + mac);
        return mac;
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
        provision.setStatus(ProvisionStatus.STATUS_WRITING_WIFI);
        sendBlocking(BlemqttCommand.create(BlemqttOp.GATT_WRITE, params));
        provision.setMessage("Wi-Fi config written");
        ILog.i(TAG, "writeWifiConfig", "written", "ssid=" + ssid);
    }

    // Block until the device reports a terminal Wi-Fi state or the timeout hits.
    private void awaitWifiJoin(MeoDeviceProvision provision, BlockingQueue<Object> terminalState) {
        provision.setStatus(ProvisionStatus.STATUS_READING_STATUS);
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

    private void safeDisconnect(MeoDeviceProvision provision) {
        try {
            provision.setStatus(ProvisionStatus.STATUS_DISCONNECTING_BLE);
            sendBlocking(BlemqttCommand.create(BlemqttOp.DEVICE_DISCONNECT, addressParams(provision)));
            provision.setStatus(ProvisionStatus.STATUS_DISCONNECTED_BLE);
            ILog.i(TAG, "disconnect", "disconnected", addressLog(provision));
        } catch (RuntimeException e) {
            ILog.w(TAG, "disconnect failed", e);
        }
    }

    // --- Events ---------------------------------------------------------------

    private void collectScanResult(BlemqttEvent event, Map<String, JsonObject> found) {
        if (event == null || !EVENT_SCAN_DEVICE_FOUND.equals(event.getEventType())) {
            return;
        }
        JsonElement payload = event.getPayload();
        if (payload == null || !payload.isJsonObject()) {
            return;
        }
        JsonObject device = payload.getAsJsonObject();
        JsonElement address = device.get("address");
        if (address != null && !address.isJsonNull()) {
            found.put(address.getAsString(), device);
        }
    }

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
