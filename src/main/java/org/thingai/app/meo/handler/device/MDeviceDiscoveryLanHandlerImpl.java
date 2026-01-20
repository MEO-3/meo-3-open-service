package org.thingai.app.meo.handler.device;

import org.thingai.meo.common.callback.RequestCallback;
import org.thingai.meo.common.entity.device.MDevice;
import org.thingai.meo.common.entity.device.MDeviceConfigLan;
import org.thingai.meo.common.entity.device.MDeviceInfo;
import org.thingai.meo.common.handler.MDeviceDiscoveryHandlerLan;


public class MDeviceDiscoveryLanHandlerImpl extends MDeviceDiscoveryHandlerLan {

    @Override
    public boolean discovery() {
        return false;
    }

    @Override
    public boolean closeDiscovery() {
        return false;
    }

    @Override
    public void connectAndIdentifyDevice(MDeviceConfigLan mDeviceConfigLan, RequestCallback<MDeviceInfo> requestCallback) {

    }

    @Override
    public void setupAndSyncDeviceLocal(String label, RequestCallback<MDevice> callback) {

    }

    @Override
    public void setupAndSyncDeviceToCloud(String label, RequestCallback<MDevice> callback) {

    }
}
