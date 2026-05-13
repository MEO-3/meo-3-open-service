package org.thingai.app.meo.blemqtt;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class BlemqttEvent {
    @SerializedName("type")
    private String eventType;
    private JsonElement payload;

    public String getEventType() {
        return eventType;
    }

    public JsonElement getPayload() {
        return payload;
    }
}
