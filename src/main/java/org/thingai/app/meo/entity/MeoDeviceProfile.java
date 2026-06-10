package org.thingai.app.meo.entity;

public class MeoDeviceProfile {
    private String profileId;
    private String name;
    private String description;
    private int version;
    private int provisionTransportType;
    private int transportType;
    private int deviceType;
    private MeoDeviceCapability[] capabilities;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getProvisionTransportType() {
        return provisionTransportType;
    }

    public void setProvisionTransportType(int provisionTransportType) {
        this.provisionTransportType = provisionTransportType;
    }

    public int getTransportType() {
        return transportType;
    }

    public void setTransportType(int transportType) {
        this.transportType = transportType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public MeoDeviceCapability[] getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(MeoDeviceCapability[] capabilities) {
        this.capabilities = capabilities;
    }
}
