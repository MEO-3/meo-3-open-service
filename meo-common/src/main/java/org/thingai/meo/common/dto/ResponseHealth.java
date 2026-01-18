package org.thingai.meo.common.dto;

public class ResponseHealth {
    private String status;

    public ResponseHealth() {}

    public ResponseHealth(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
