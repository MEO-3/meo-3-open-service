package org.thingai.meo.handler;

import org.thingai.meo.entity.MDeviceDiscoverInfo;

public class MDevDiscoverHandler {
    private static final String TAG = "MDevDiscoverHandler";

    private final MDeviceDiscoverInfo[] deviceDiscoverInfos;
    private final int maxSize;

    public MDevDiscoverHandler(int maxSize) {
        this.maxSize = maxSize;
        this.deviceDiscoverInfos = new MDeviceDiscoverInfo[maxSize];
    }

    public synchronized boolean addDevice(MDeviceDiscoverInfo deviceInfo) {
        for (int i = 0; i < maxSize; i++) {
            if (deviceDiscoverInfos[i] == null) {
                deviceDiscoverInfos[i] = deviceInfo;
                return true;
            } else if (deviceDiscoverInfos[i].getMacAddress().equals(deviceInfo.getMacAddress())) {
                // Device already exists
                return false;
            }
        }
        // No space to add new device
        return false;
    }

    public synchronized MDeviceDiscoverInfo[] getDiscoveredDevices() {
        return deviceDiscoverInfos;
    }

    public synchronized void clearDevices() {
        for (int i = 0; i < maxSize; i++) {
            deviceDiscoverInfos[i] = null;
        }
    }
}
