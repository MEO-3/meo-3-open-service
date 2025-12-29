package org.thingai.app.meo.handler.telemetry;

import com.google.gson.JsonObject;
import org.thingai.base.log.ILog;

public class MTelemetryHandler {
    private static final String TAG = "MTelemetryHandler";

    public void handleDeviceEvent(String deviceId, String eventName, JsonObject rawJson) {
        if (eventName == null) {
            ILog.w(TAG, "Received event without event_name from device " + deviceId);
            return;
        }

        ILog.i(TAG, "Telemetry from device " + deviceId +
                ", event=" + eventName + ", payload=" + rawJson.toString());

        // TODO: write to InfluxDB, Node-RED, etc.
    }
}
