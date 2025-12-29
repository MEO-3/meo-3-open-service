package org.thingai.meo.common.entity;

import org.thingai.base.dao.annotations.DaoColumn;
import org.thingai.base.dao.annotations.DaoTable;

@DaoTable(name = "device_feature_method")
public class MDeviceFeatureMethod extends MDeviceFeature {
    @DaoColumn(name = "device_id", primaryKey = true)
    private String deviceId;

    @DaoColumn(name = "feature_method")
    private String featureName;

    public MDeviceFeatureMethod(String deviceId, String featureName) {
        this.deviceId = deviceId;
        this.featureName = featureName;
    }

    public MDeviceFeatureMethod() {

    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }
}
