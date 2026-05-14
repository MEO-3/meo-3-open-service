package org.thingai.app.meo.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.thingai.app.meo.blemqtt.BlemqttClient;
import org.thingai.app.meo.blemqtt.BlemqttCommand;
import org.thingai.app.meo.blemqtt.BlemqttError;
import org.thingai.app.meo.blemqtt.BlemqttOp;
import org.thingai.app.meo.blemqtt.BlemqttReply;
import org.thingai.app.meo.define.BleUuid;
import org.thingai.app.meo.entity.MeoDeviceProvision;
import org.thingai.app.meo.handler.callback.RequestCallback;
import org.thingai.app.meo.util.JsonUtil;

public class MeoProvisionHandler {
    private static final String DEFAULT_ENCODING = "utf8";

    private final BlemqttClient blemqttClient;

    public MeoProvisionHandler(BlemqttClient blemqttClient) {
        this.blemqttClient = blemqttClient;
    }

    public void scanProvisionableDevices(int timeoutMs, String namePrefix, RequestCallback<BlemqttReply> callback) {
        JsonObject params = new JsonObject();
        params.addProperty("timeoutMs", timeoutMs);
        params.addProperty("serviceUuid", BleUuid.MEO_DEVICE_PROVISION_SERVICE);
        if (namePrefix != null && !namePrefix.isEmpty()) {
            params.addProperty("namePrefix", namePrefix);
        }

        sendRaw(BlemqttCommand.create(BlemqttOp.SCAN_START, params), callback, "scan provisionable devices");
    }

    public void connect(MeoDeviceProvision provision, RequestCallback<MeoDeviceProvision> callback) {
        if (!validateAddress(provision, callback)) {
            return;
        }

        provision.setStatus(MeoDeviceProvision.STATUS_CONNECTING_BLE);
        send(provision, BlemqttCommand.create(BlemqttOp.DEVICE_CONNECT, addressParams(provision)), callback, "connect BLE device",
                reply -> {
                    provision.setStatus(MeoDeviceProvision.STATUS_CONNECTED_BLE);
                    provision.setMessage("BLE device connected");
                    return provision;
                });
    }

    public void readDeviceMac(MeoDeviceProvision provision, RequestCallback<MeoDeviceProvision> callback) {
        if (!validateAddress(provision, callback)) {
            return;
        }

        provision.setStatus(MeoDeviceProvision.STATUS_READING_MAC);
        send(provision, gattRead(provision, BleUuid.MEO_DEVICE_MAC_CHAR), callback, "read device MAC",
                reply -> {
                    provision.setMacAddress(readReplyValue(reply));
                    provision.setMessage("device MAC read");
                    return provision;
                });
    }

    public void readProductId(MeoDeviceProvision provision, RequestCallback<MeoDeviceProvision> callback) {
        if (!validateAddress(provision, callback)) {
            return;
        }

        provision.setStatus(MeoDeviceProvision.STATUS_READING_PRODUCT_ID);
        send(provision, gattRead(provision, BleUuid.MEO_DEVICE_PRODUCT_ID_CHAR), callback, "read product ID",
                reply -> {
                    provision.setProductId(readReplyValue(reply));
                    provision.setMessage("product ID read");
                    return provision;
                });
    }

    public void writeWifiConfig(
            MeoDeviceProvision provision,
            String ssid,
            String password,
            RequestCallback<MeoDeviceProvision> callback
    ) {
        if (!validateAddress(provision, callback)) {
            return;
        }
        if (ssid == null || ssid.isEmpty()) {
            fail(provision, callback, new IllegalArgumentException("wifi ssid is required"), "write Wi-Fi config");
            return;
        }

        JsonObject wifiConfig = new JsonObject();
        wifiConfig.addProperty("ssid", ssid);
        wifiConfig.addProperty("password", password != null ? password : "");

        JsonObject params = gattParams(provision, BleUuid.MEO_WIFI_CONFIG_CHAR);
        params.addProperty("encoding", DEFAULT_ENCODING);
        params.addProperty("value", JsonUtil.toJson(wifiConfig));

        provision.setWifiSsid(ssid);
        provision.setStatus(MeoDeviceProvision.STATUS_WRITING_WIFI);
        send(provision, BlemqttCommand.create(BlemqttOp.GATT_WRITE, params), callback, "write Wi-Fi config",
                reply -> {
                    provision.setMessage("Wi-Fi config written");
                    return provision;
                });
    }

    public void readProvisionStatus(MeoDeviceProvision provision, RequestCallback<MeoDeviceProvision> callback) {
        if (!validateAddress(provision, callback)) {
            return;
        }

        provision.setStatus(MeoDeviceProvision.STATUS_READING_STATUS);
        send(provision, gattRead(provision, BleUuid.MEO_PROVISION_STATUS_CHAR), callback, "read provision status",
                reply -> {
                    String status = readReplyValue(reply);
                    provision.setProvisionStatus(status);
                    provision.setMessage(status);
                    return provision;
                });
    }

    public void disconnect(MeoDeviceProvision provision, RequestCallback<MeoDeviceProvision> callback) {
        if (!validateAddress(provision, callback)) {
            return;
        }

        provision.setStatus(MeoDeviceProvision.STATUS_DISCONNECTING_BLE);
        send(provision, BlemqttCommand.create(BlemqttOp.DEVICE_DISCONNECT, addressParams(provision)), callback, "disconnect BLE device",
                reply -> {
                    provision.setStatus(MeoDeviceProvision.STATUS_DISCONNECTED_BLE);
                    provision.setMessage("BLE device disconnected");
                    return provision;
                });
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

    private void sendRaw(BlemqttCommand command, RequestCallback<BlemqttReply> callback, String message) {
        blemqttClient.send(command).thenAccept(reply -> {
            if (reply.isOk()) {
                callback.onResult(reply, message);
            } else {
                callback.onFailure(toException(reply), message);
            }
        }).exceptionally(throwable -> {
            callback.onFailure(throwable, message);
            return null;
        });
    }

    private void send(
            MeoDeviceProvision provision,
            BlemqttCommand command,
            RequestCallback<MeoDeviceProvision> callback,
            String message,
            ReplyMapper mapper
    ) {
        blemqttClient.send(command).thenAccept(reply -> {
            if (!reply.isOk()) {
                fail(provision, callback, toException(reply), message);
                return;
            }
            try {
                callback.onResult(mapper.map(reply), message);
            } catch (Exception e) {
                fail(provision, callback, e, message);
            }
        }).exceptionally(throwable -> {
            fail(provision, callback, throwable, message);
            return null;
        });
    }

    private boolean validateAddress(MeoDeviceProvision provision, RequestCallback<MeoDeviceProvision> callback) {
        if (provision == null) {
            callback.onFailure(new IllegalArgumentException("provision is required"), "validate provision");
            return false;
        }
        if (provision.getBleAddress() == null || provision.getBleAddress().isEmpty()) {
            fail(provision, callback, new IllegalArgumentException("ble address is required"), "validate BLE address");
            return false;
        }
        return true;
    }

    private void fail(
            MeoDeviceProvision provision,
            RequestCallback<MeoDeviceProvision> callback,
            Throwable throwable,
            String message
    ) {
        if (provision != null) {
            provision.setStatus(MeoDeviceProvision.STATUS_FAILED);
            provision.setMessage(message);
        }
        callback.onFailure(throwable, message);
    }

    private RuntimeException toException(BlemqttReply reply) {
        BlemqttError error = reply.getError();
        if (error == null) {
            return new RuntimeException("blemqtt command failed");
        }
        return new RuntimeException(error.getCode() + ": " + error.getMessage());
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
        String[] fields = {"value", "macAddress", "mac", "productId", "status", "state", "message"};
        for (String field : fields) {
            JsonElement value = object.get(field);
            if (value != null && !value.isJsonNull()) {
                return value.isJsonPrimitive() ? value.getAsString() : value.toString();
            }
        }
        return object.toString();
    }

    private interface ReplyMapper {
        MeoDeviceProvision map(BlemqttReply reply);
    }
}
