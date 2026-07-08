package org.thingai.app.meo.api.dto;

// Request body for POST /api/v1/provision.
public class MeoProvisionRequest {
    private String bleAddress;
    private String ssid;
    private String password;

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
