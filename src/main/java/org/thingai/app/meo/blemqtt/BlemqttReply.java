package org.thingai.app.meo.blemqtt;

import com.google.gson.JsonElement;

public class BlemqttReply {
    private String requestId;
    private boolean ok;
    private JsonElement result;
    private BlemqttError error;

    public String getRequestId() {
        return requestId;
    }

    public boolean isOk() {
        return ok;
    }

    public JsonElement getResult() {
        return result;
    }

    public BlemqttError getError() {
        return error;
    }
}
