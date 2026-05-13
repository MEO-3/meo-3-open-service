package org.thingai.app.meo.blemqtt;

import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.UUID;

public class BlemqttCommand {
    private final String requestId;
    private final String op;
    private final JsonObject params;

    public BlemqttCommand(String requestId, String op, JsonObject params) {
        this.requestId = Objects.requireNonNull(requestId, "requestId");
        this.op = Objects.requireNonNull(op, "op");
        this.params = params != null ? params : new JsonObject();
    }

    public static BlemqttCommand create(BlemqttOp op) {
        return create(op, new JsonObject());
    }

    public static BlemqttCommand create(BlemqttOp op, JsonObject params) {
        return new BlemqttCommand(newRequestId(), op.value(), params);
    }

    public static String newRequestId() {
        return "req-" + UUID.randomUUID();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOp() {
        return op;
    }

    public JsonObject getParams() {
        return params;
    }
}
