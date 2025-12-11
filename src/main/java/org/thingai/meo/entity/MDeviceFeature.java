package org.thingai.meo.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "m_device_feature")
public class MDeviceFeature {
    @DaoColumn(name = "device_id", primaryKey = true)
    private String deviceId;

    @DaoColumn(name = "feature_methods")
    private String featureMethods;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFeatureMethods() {
        return featureMethods;
    }

    public void setFeatureMethods(String featureMethods) {
        this.featureMethods = featureMethods;
    }
}
