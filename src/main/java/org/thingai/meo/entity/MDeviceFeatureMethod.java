package org.thingai.meo.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device_feature_method")
public class MDeviceFeatureMethod {
    @DaoColumn(name = "device_id", primaryKey = true)
    private String deviceId;

    @DaoColumn(name = "feature_method")
    private String featureMethod;

    public MDeviceFeatureMethod(String deviceId, String featureMethod) {
        this.deviceId = deviceId;
        this.featureMethod = featureMethod;
    }

    public MDeviceFeatureMethod() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFeatureMethod() {
        return featureMethod;
    }

    public void setFeatureMethod(String featureMethod) {
        this.featureMethod = featureMethod;
    }
}
