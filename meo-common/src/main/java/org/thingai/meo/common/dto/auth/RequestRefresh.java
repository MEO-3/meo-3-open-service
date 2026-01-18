package org.thingai.meo.common.dto.auth;

public class RequestRefresh {
    private String refreshToken;

    public RequestRefresh() {}

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
