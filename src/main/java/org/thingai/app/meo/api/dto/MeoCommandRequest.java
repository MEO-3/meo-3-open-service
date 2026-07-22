package org.thingai.app.meo.api.dto;

// Request body for device control. The capability id encodes the action, so
// there is no verb; value is only read for WRITE capabilities.
public class MeoCommandRequest {
    private int cap;
    private int value;

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
