package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 5/6/17.
 */
public class AccessRequest {
    private String badge, access, opHost;

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getOpHost() {
        return opHost;
    }

    public void setOpHost(String opHost) {
        this.opHost = opHost;
    }
}
