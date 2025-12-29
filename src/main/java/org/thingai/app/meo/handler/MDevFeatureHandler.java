package org.thingai.app.meo.handler;

import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;
import org.thingai.meo.common.entity.MDevice;
import org.thingai.meo.common.entity.MDeviceFeatureEvent;
import org.thingai.meo.common.entity.MDeviceFeatureMethod;

public class MDevFeatureHandler {
    private static final String TAG = "MDevFeatureHandler";

    private final Dao dao;

    public MDevFeatureHandler(Dao dao) {
        this.dao = dao;
    }

    public void updateDeviceFeature(MDevice device) {
        ILog.d(TAG,"updateDeviceFeature", device.getFeatureMethods().length + " methods, " + device.getFeatureEvents().length + " events");
        String deviceId = device.getId();

        for (String featureMethod : device.getFeatureMethods()) {
            dao.insertOrUpdate(new MDeviceFeatureMethod(deviceId, featureMethod));
        }

        for (String featureEvent : device.getFeatureEvents()) {
            dao.insertOrUpdate(new MDeviceFeatureEvent(deviceId, featureEvent));
        }
    }

    public MDeviceFeatureMethod[] getDeviceFeatureMethods(String deviceId) {
        return dao.query(MDeviceFeatureMethod.class, "device_id", deviceId);
    }

    public MDeviceFeatureEvent[] getDeviceFeatureEvents(String deviceId) {
        return dao.query(MDeviceFeatureEvent.class, "device_id", deviceId);
    }

    public void removeDeviceFeatures(String deviceId) {
        dao.deleteByColumn(MDeviceFeatureMethod.class, "device_id", deviceId);
        dao.deleteByColumn(MDeviceFeatureEvent.class, "device_id", deviceId);
    }

    public void invokeDeviceFeature(String deviceId, String featureName) {

    }

    public void invokeDeviceFeature(String deviceId, String featureName, Object... params) {
        // Implementation for invoking a device feature
    }
}
