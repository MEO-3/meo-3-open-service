package org.thingai.app.meo.blemqtt;

import com.google.gson.Gson;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.thingai.base.log.ILog;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlemqttClient {
    private static final String TAG = "BlemqttClient";

    private final BlemqttConfig config;
    private final Gson gson = new Gson();
    private final Map<String, CompletableFuture<BlemqttReply>> pendingReplies = new ConcurrentHashMap<>();
    private final List<BlemqttCallback<BlemqttEvent>> eventCallbacks = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private MqttClient client;

    public BlemqttClient(BlemqttConfig config) {
        this.config = config;
    }

    public synchronized void connect() throws MqttException {
        if (client != null && client.isConnected()) {
            ILog.d(TAG, "connect", "already connected");
            return;
        }

        ILog.i(TAG, "connect", config.getBrokerUrl());
        client = new MqttClient(config.getBrokerUrl(), config.getClientId(), new MemoryPersistence());
        client.setCallback(new MqttCallback() {
            @Override
            public void disconnected(MqttDisconnectResponse disconnectResponse) {
                ILog.w(TAG, "disconnected", String.valueOf(disconnectResponse));
                failPendingReplies(new IllegalStateException("blemqtt MQTT v5 client disconnected"));
            }

            @Override
            public void mqttErrorOccurred(MqttException exception) {
                ILog.e(TAG, "mqttErrorOccurred", exception);
                failPendingReplies(exception);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                handleMessage(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttToken token) {
            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                ILog.i(TAG, "connectComplete", "reconnect=" + reconnect, serverURI);
            }

            @Override
            public void authPacketArrived(int reasonCode, MqttProperties properties) {
            }
        });

        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(true);
        options.setCleanStart(true);

        client.connect(options);
        client.subscribe(BlemqttTopics.REPLY_WILDCARD, config.getQos());
        client.subscribe(BlemqttTopics.EVENT, config.getQos());
        ILog.i(TAG, "subscribed", BlemqttTopics.REPLY_WILDCARD, BlemqttTopics.EVENT);
    }

    public synchronized void disconnect() throws MqttException {
        if (client != null && client.isConnected()) {
            ILog.i(TAG, "disconnect");
            client.disconnect();
        }
        failPendingReplies(new IllegalStateException("blemqtt client disconnected"));
    }

    public CompletableFuture<BlemqttReply> send(BlemqttCommand command) {
        CompletableFuture<BlemqttReply> future = new CompletableFuture<>();
        pendingReplies.put(command.getRequestId(), future);

        scheduler.schedule(() -> {
            CompletableFuture<BlemqttReply> pending = pendingReplies.remove(command.getRequestId());
            if (pending != null) {
                pending.completeExceptionally(new BlemqttTimeoutException(command.getRequestId()));
            }
        }, config.getRequestTimeoutMillis(), TimeUnit.MILLISECONDS);

        try {
            ensureConnected();
            byte[] payload = gson.toJson(command).getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(payload);
            message.setQos(config.getQos());
            ILog.d(TAG, "send", command.getRequestId(), command.getOp());
            client.publish(BlemqttTopics.COMMAND, message);
        } catch (Exception e) {
            ILog.e(TAG, "send failed", e);
            pendingReplies.remove(command.getRequestId());
            future.completeExceptionally(e);
        }

        return future;
    }

    public void onEvent(BlemqttCallback<BlemqttEvent> callback) {
        eventCallbacks.add(callback);
    }

    public void removeEventCallback(BlemqttCallback<BlemqttEvent> callback) {
        eventCallbacks.remove(callback);
    }

    private void handleMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);

        if (topic.startsWith(BlemqttTopics.REPLY_PREFIX)) {
            BlemqttReply reply = gson.fromJson(payload, BlemqttReply.class);
            CompletableFuture<BlemqttReply> pending = pendingReplies.remove(reply.getRequestId());
            if (pending != null) {
                ILog.d(TAG, "reply", reply.getRequestId(), "ok=" + reply.isOk());
                pending.complete(reply);
            }
            return;
        }

        if (BlemqttTopics.EVENT.equals(topic)) {
            BlemqttEvent event = gson.fromJson(payload, BlemqttEvent.class);
            ILog.d(TAG, "event", event.getEventType());
            for (BlemqttCallback<BlemqttEvent> callback : eventCallbacks) {
                callback.handle(event);
            }
        }
    }

    private void ensureConnected() {
        if (client == null || !client.isConnected()) {
            throw new IllegalStateException("blemqtt client is not connected");
        }
    }

    private void failPendingReplies(Throwable cause) {
        for (CompletableFuture<BlemqttReply> future : pendingReplies.values()) {
            future.completeExceptionally(cause);
        }
        pendingReplies.clear();
    }
}
