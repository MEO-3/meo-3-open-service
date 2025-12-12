package org.thingai.meo.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device_feature_event")
public class MDeviceFeatureEvent {
    @DaoColumn(name = "device_id", primaryKey = true)
    private String deviceId;

    @DaoColumn(name = "feature_event")
    private String featureEvent;

    public MDeviceFeatureEvent(String deviceId, String featureEvent) {
        this.deviceId = deviceId;
        this.featureEvent = featureEvent;
    }

    public MDeviceFeatureEvent() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFeatureEvent() {
        return featureEvent;
    }

    public void setFeatureEvent(String featureEvent) {
        this.featureEvent = featureEvent;
    }
}
