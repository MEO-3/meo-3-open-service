package org.thingai.meo.common.dto.auth;

public class RequestLogin {
    private String authUsername;
    private String password;

    public RequestLogin() {}

    public String getAuthUsername() { return authUsername; }
    public void setAuthUsername(String authUsername) { this.authUsername = authUsername; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
