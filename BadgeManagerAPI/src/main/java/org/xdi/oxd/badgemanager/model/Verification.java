package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 19/4/17.
 */
public class Verification {
    private String allowedOrigins, type;

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
