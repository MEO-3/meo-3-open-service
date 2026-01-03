package org.thingai.app.meo.handler.device;

import org.thingai.base.dao.Dao;
import org.thingai.base.log.ILog;
import org.thingai.meo.common.entity.MDevice;
import org.thingai.meo.common.entity.MDeviceFeatureEvent;
import org.thingai.meo.common.entity.MDeviceFeatureMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDeviceHandler {
    private static final String TAG = "MDeviceHandler";

    private static MDeviceFeatureHandler featureHandler;

    private final Dao dao;

    public MDeviceHandler(Dao dao, MDeviceFeatureHandler featureHandler) {
        MDeviceHandler.featureHandler = featureHandler;
        this.dao = dao;
    }

    public static MDeviceFeatureHandler getFeatureHandler() {
        return featureHandler;
    }

    public static void setFeatureHandler(MDeviceFeatureHandler featureHandler) {
        MDeviceHandler.featureHandler = featureHandler;
    }

    public void addDevice(MDevice device) {
        dao.insertOrUpdate(device);
        featureHandler.updateDeviceFeature(device);
    }

    public MDevice getDevice(String id) {
        MDevice[] devices = dao.query(MDevice.class, "id", id);
        if (devices.length == 0) {
            return null;
        }

        MDeviceFeatureEvent[] featureEvents = featureHandler.getDeviceFeatureEvents(id);
        MDeviceFeatureMethod[] featureMethods = featureHandler.getDeviceFeatureMethods(id);

        String[] events = new String[featureEvents.length];
        for (int i = 0; i < featureEvents.length; i++) {
            events[i] = featureEvents[i].getFeatureName();
        }
        ILog.d(TAG, "Feature events for device " + id + ": " + String.join(", ", events));

        String[] methods = new String[featureMethods.length];
        for (int i = 0; i < featureMethods.length; i++) {
            methods[i] = featureMethods[i].getFeatureName();
        }
        ILog.d(TAG, "Feature methods for device " + id + ": " + String.join(", ", methods));

        devices[0].setFeatureEvents(events);
        devices[0].setFeatureMethods(methods);

        return devices[0];
    }

    public MDevice[] getAllDevices() {
        Map<String, Object>[] maps = dao.queryRaw(
                "SELECT * FROM device p " +
                "JOIN device_feature_method fm ON p.id = fm.device_id " +
                "JOIN device_feature_event fe ON p.id = fe.device_id");

        ILog.d(TAG, "getAllDevices", "total " + maps.length);

        HashMap<String, MDevice> deviceMap = new HashMap<>();

        for (Map<String, Object> map : maps) {
            String deviceId = (String) map.get("id");
            MDevice device;
            if (deviceMap.containsKey(deviceId)) {
                device = deviceMap.get(deviceId);
            } else {
                device = new MDevice();
                device.setId(deviceId);
                device.setLabel((String) map.get("label"));
                device.setModel((String) map.get("model"));
                device.setManufacturer((String) map.get("manufacturer"));
                device.setFeatureEvents(new String[]{});
                device.setFeatureMethods(new String[]{});
                deviceMap.put(deviceId, device);
            }

            // Add feature method
            String featureMethod = (String) map.get("feature_method");
            List<String> featureMethods = new ArrayList<>(List.of(device.getFeatureMethods()));
            if (!featureMethods.contains(featureMethod)) {
                featureMethods.add(featureMethod);
                device.setFeatureMethods(featureMethods.toArray(new String[0]));
            }

            // Add feature event
            String featureEvent = (String) map.get("feature_event");
            List<String> featureEvents = new ArrayList<>(List.of(device.getFeatureEvents()));
            if (!featureEvents.contains(featureEvent)) {
                featureEvents.add(featureEvent);
                device.setFeatureEvents(featureEvents.toArray(new String[0]));
            }
        }

        return deviceMap.values().toArray(new MDevice[0]);
    }

    public void deleteDevice(String id) {
        dao.delete(MDevice.class, id);
    }

    public void updateDeviceLabel(String devId, String label) {
        MDevice device = getDevice(devId);
        if (device != null) {
            device.setLabel(label);
            dao.insertOrUpdate(device);
        }
    }
}
