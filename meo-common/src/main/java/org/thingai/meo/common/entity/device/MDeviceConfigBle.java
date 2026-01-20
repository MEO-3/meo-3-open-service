package org.thingai.meo.common.entity.device;

public class MDeviceConfigBle extends MDeviceConfig {
    private String bleAddress;
    private String bleName;
    private String serviceUuid;
    private int rssi;
    private boolean hasConfigService;

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public boolean isHasConfigService() {
        return hasConfigService;
    }

    public void setHasConfigService(boolean hasConfigService) {
        this.hasConfigService = hasConfigService;
    }
}
