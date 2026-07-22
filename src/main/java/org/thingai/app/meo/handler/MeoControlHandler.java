package org.thingai.app.meo.handler;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.thingai.app.meo.callback.RequestCallback;
import org.thingai.app.meo.define.ErrorCode;
import org.thingai.app.meo.define.MeoCmd;
import org.thingai.app.meo.define.MeoCmdErrCode;
import org.thingai.app.meo.messaging.MeoCommandFrame;
import org.thingai.app.meo.messaging.MeoReplyFrame;
import org.thingai.app.meo.messaging.MeoTopics;
import org.thingai.base.log.ILog;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

// Device control over MQTT (docs/mqtt_messaging.md): publish a command frame,
// block for the device's reply, hand the value back.
public class MeoControlHandler {
    private static final String TAG = "MeoControlHandler";

    private static final long REPLY_TIMEOUT_MS = 10_000;
    private static final int COMMAND_QOS = 1;
    private static final int REPLY_QOS = 0;

    private final MqttClient mqttClient;
    private final MeoDeviceHandler deviceHandler;

    // Keyed by deviceId + requestId so a stray reply cannot complete another
    // device's request.
    private final Map<String, CompletableFuture<MeoReplyFrame>> pendingReplies = new ConcurrentHashMap<>();

    // Rotating request id from 0 -> 65535
    private final AtomicInteger requestIds = new AtomicInteger();

    public MeoControlHandler(MqttClient mqttClient, MeoDeviceHandler deviceHandler) {
        this.mqttClient = mqttClient;
        this.deviceHandler = deviceHandler;
    }

    // Subscribe to every device's reply topic. The persistent session keeps it
    // across reconnects, so this runs once.
    //
    // Must use the MqttSubscription[] overload: in Paho 1.2.5 the String/String[]
    // subscribe-with-listener forms recurse into themselves and blow the stack.
    public void start() throws MqttException {
        mqttClient.subscribe(
                new MqttSubscription[]{new MqttSubscription(MeoTopics.REPLY_WILDCARD, REPLY_QOS)},
                new IMqttMessageListener[]{this::onReply});
        ILog.i(TAG, "start", "subscribed", MeoTopics.REPLY_WILDCARD);
    }

    // For READ and MEO_CMD_* capabilities, which carry no value.
    public void sendCommand(String deviceId, int cap, RequestCallback<Double> callback) {
        sendCommand(deviceId, cap, 0, callback);
    }

    // Send a command and wait for the reply. The capability id decides whether
    // this reads, writes, or runs a generic command.
    public void sendCommand(String deviceId, int cap, int value, RequestCallback<Double> callback) {
        if (isEmpty(deviceId)) {
            callback.onFailure(ErrorCode.CONTROL_FAILED, "device id is required");
            return;
        }
        if (cap < 0 || cap > 0xFFFF) {
            callback.onFailure(ErrorCode.CONTROL_FAILED, "capability out of range: " + cap);
            return;
        }
        // Events are device to gateway only.
        if (MeoCmd.isEvent(cap)) {
            callback.onFailure(ErrorCode.CONTROL_FAILED, "event capability cannot be commanded: " + hex(cap));
            return;
        }
        if (deviceHandler.getDevice(deviceId) == null) {
            callback.onFailure(ErrorCode.DEVICE_NOT_FOUND, "device not found: " + deviceId);
            return;
        }
        try {
            MeoReplyFrame reply = sendBlocking(deviceId, cap, value);
            if (!reply.isOk()) {
                ILog.w(TAG, "sendCommand", deviceId, hex(cap), "device error=" + reply.getError());
                callback.onFailure(deviceErrorCode(reply.getError()), deviceErrorMessage(reply.getError()));
                return;
            }
            callback.onResult(resolveValue(cap, reply), "command sent");
        } catch (TimeoutException e) {
            ILog.w(TAG, "sendCommand", "no reply", deviceId, hex(cap));
            callback.onFailure(ErrorCode.CONTROL_TIMEOUT,
                    "device did not reply within " + REPLY_TIMEOUT_MS + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            callback.onFailure(ErrorCode.CONTROL_FAILED, "interrupted while waiting for reply");
        } catch (Exception e) {
            ILog.e(TAG, "sendCommand failed", e);
            callback.onFailure(ErrorCode.CONTROL_FAILED, failureMessage(e, "command failed"));
        }
    }

    // Publish the frame and block for its reply. The finally keeps a timed-out
    // request from leaking its pending entry.
    private MeoReplyFrame sendBlocking(String deviceId, int cap, int value) throws Exception {
        int requestId = nextRequestId();
        String key = pendingKey(deviceId, requestId);
        CompletableFuture<MeoReplyFrame> future = new CompletableFuture<>();
        pendingReplies.put(key, future);
        try {
            MqttMessage message = new MqttMessage(new MeoCommandFrame(requestId, cap, value).toBytes());
            message.setQos(COMMAND_QOS);
            ILog.d(TAG, "send", deviceId, hex(cap), "requestId=" + requestId, "value=" + value);
            mqttClient.publish(MeoTopics.command(deviceId), message);
            return future.get(REPLY_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } finally {
            pendingReplies.remove(key);
        }
    }

    // Reply subscription callback. Runs on the MQTT thread.
    private void onReply(String topic, MqttMessage message) {
        String deviceId = MeoTopics.deviceId(topic);
        if (deviceId == null) {
            return;
        }

        MeoReplyFrame reply;
        try {
            reply = MeoReplyFrame.parse(message.getPayload());
        } catch (IllegalArgumentException e) {
            ILog.w(TAG, "reply", "dropping malformed frame", topic, e.getMessage());
            return;
        }

        CompletableFuture<MeoReplyFrame> pending =
                pendingReplies.remove(pendingKey(deviceId, reply.getRequestId()));
        if (pending == null) {
            // Already timed out, or a duplicate.
            ILog.d(TAG, "reply", "no pending request", deviceId, "requestId=" + reply.getRequestId());
            return;
        }
        ILog.d(TAG, "reply", deviceId, "requestId=" + reply.getRequestId(), "ok=" + reply.isOk());
        pending.complete(reply);
    }

    // READ replies carry float32, everything else int32.
    private double resolveValue(int cap, MeoReplyFrame reply) {
        return MeoCmd.isRead(cap) ? reply.asFloat() : reply.asInt();
    }

    private int nextRequestId() {
        return requestIds.getAndIncrement() & 0xFFFF;
    }

    private String pendingKey(String deviceId, int requestId) {
        return deviceId + "#" + requestId;
    }

    // The device is the authority on which capabilities it implements.
    private int deviceErrorCode(int error) {
        return error == MeoCmdErrCode.ERR_UNKNOWN_CAP
                ? ErrorCode.CONTROL_CAP_NOT_SUPPORTED
                : ErrorCode.CONTROL_DEVICE_ERROR;
    }

    private String deviceErrorMessage(int error) {
        if (error == MeoCmdErrCode.ERR_BAD_REQUEST) {
            return "device rejected the command as malformed";
        }
        if (error == MeoCmdErrCode.ERR_UNKNOWN_CAP) {
            return "device does not implement the requested capability";
        }
        if (error == MeoCmdErrCode.ERR_HANDLE_FAILED) {
            return "device failed to execute the command";
        }
        return "device reported error " + error;
    }

    private String failureMessage(Throwable t, String fallback) {
        return t.getMessage() != null ? t.getMessage() : fallback;
    }

    private static String hex(int cap) {
        return String.format("cap=0x%04X", cap);
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
