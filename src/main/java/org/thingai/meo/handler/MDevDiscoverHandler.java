package org.thingai.meo.handler;

import org.thingai.meo.entity.MDeviceDiscoverInfo;

import java.util.LinkedList;

public class MDevDiscoverHandler {
    private static final String TAG = "MDevDiscoverHandler";

    private final LinkedList<MDeviceDiscoverInfo> deviceDiscoverInfos;
    private final int maxSize;

    public MDevDiscoverHandler(int maxSize) {
        this.maxSize = maxSize;
        this.deviceDiscoverInfos = new LinkedList<>();
    }

    public synchronized boolean addDevice(MDeviceDiscoverInfo deviceInfo) {
        if (deviceDiscoverInfos.size() >= maxSize) {
            return false;
        }
        for (MDeviceDiscoverInfo info : deviceDiscoverInfos) {
            if (info.getMacAddress().equals(deviceInfo.getMacAddress())) {
                return false;
            }
        }
        deviceDiscoverInfos.add(deviceInfo);
        return true;
    }

    public synchronized MDeviceDiscoverInfo[] getDevices() {
        return deviceDiscoverInfos.toArray(new MDeviceDiscoverInfo[0]);
    }

    public synchronized void clearDevices() {
        deviceDiscoverInfos.clear();
    }
}
