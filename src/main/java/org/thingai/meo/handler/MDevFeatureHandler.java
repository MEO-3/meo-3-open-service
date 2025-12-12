package org.thingai.meo.handler;

import org.thingai.base.dao.Dao;
import org.thingai.meo.entity.MDevice;
import org.thingai.meo.entity.MDeviceFeatureMethod;

public class MDevFeatureHandler {
    private static final String TAG = "MDevFeatureHandler";

    private Dao dao;

    public MDevFeatureHandler(Dao dao) {
        this.dao = dao;
    }

    public void updateDeviceFeature(MDevice device) {
        String deviceId = device.getId();
        for (String featureMethod : device.getFeatureMethods()) {
            dao.insertOrUpdate(new MDeviceFeatureMethod(deviceId, featureMethod));
        }
    }

    public void invokeDeviceFeature(String deviceId, String featureName) {

    }

    public void invokeDeviceFeature(String deviceId, String featureName, Object... params) {
        // Implementation for invoking a device feature
    }
}
