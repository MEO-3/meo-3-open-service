package org.thingai.meo.common.entity.device;

public class MDeviceConfigLan extends MDeviceConfig {
    private String[] mDnsServices;
    private String ipAddress;
    private String macAddress;
    private int port;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String[] getmDnsServices() {
        return mDnsServices;
    }

    public void setmDnsServices(String[] mDnsServices) {
        this.mDnsServices = mDnsServices;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}
