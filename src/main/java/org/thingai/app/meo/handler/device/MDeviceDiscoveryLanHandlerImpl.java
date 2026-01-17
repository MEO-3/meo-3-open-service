package org.thingai.app.meo.handler.device;

import org.thingai.meo.common.callback.RequestCallback;
import org.thingai.meo.common.callback.SetupDeviceCallback;
import org.thingai.meo.common.entity.device.MDevice;
import org.thingai.meo.common.entity.device.MDeviceConfigLan;
import org.thingai.meo.common.handler.MDeviceDiscoveryLanHandler;


public class MDeviceDiscoveryLanHandlerImpl extends MDeviceDiscoveryLanHandler {

    @Override
    public boolean discovery() {
        return false;
    }

    @Override
    public boolean closeDiscovery() {
        return false;
    }

    @Override
    public void onDeviceFound(RequestCallback<MDeviceConfigLan> callback) {

    }

    @Override
    public void connectAndIdentifyDevice(MDeviceConfigLan deviceConfig, SetupDeviceCallback callback) {

    }

    @Override
    public void setupAndSyncDeviceLocal(String label, RequestCallback<MDevice> callback) {

    }

    @Override
    public void setupAndSyncDeviceToCloud(String label, RequestCallback<MDevice> callback) {

    }
}
