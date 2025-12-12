package org.thingai.meo.handler;

import org.thingai.base.dao.Dao;
import org.thingai.meo.entity.MDevice;

public class MDevMgmtHandler {
    private static final String TAG = "MDevMgmtHandler";

    private final Dao dao;

    public MDevMgmtHandler(Dao dao) {
        this.dao = dao;
    }

    public void addDevice(MDevice device) {
        dao.insertOrUpdate(device);
    }

    public MDevice getDevice(String id) {
        return dao.query(MDevice.class, "id", id)[0];
    }

    public MDevice[] getAllDevices() {
        return dao.readAll(MDevice.class);
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
