package org.thingai.meo.common.dto.otp;

public class RequestOtpCreate {
    private String email;
    private Integer purpose;
    private String ttlMinutes;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPurpose() {
        return purpose;
    }

    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }

    public String getTtlMinutes() {
        return ttlMinutes;
    }

    public void setTtlMinutes(String ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }
}
