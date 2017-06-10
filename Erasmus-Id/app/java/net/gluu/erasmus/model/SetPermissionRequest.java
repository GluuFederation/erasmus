package net.gluu.erasmus.model;

/**
 * Created by Arvind Tomar on 10/6/17.
 */

public class SetPermissionRequest {
    private String access,badge,opHost;

    public SetPermissionRequest(String access, String badge, String opHost) {
        this.access = access;
        this.badge = badge;
        this.opHost = opHost;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getOpHost() {
        return opHost;
    }

    public void setOpHost(String opHost) {
        this.opHost = opHost;
    }
}
