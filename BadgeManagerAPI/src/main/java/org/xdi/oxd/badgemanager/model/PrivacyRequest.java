package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 3/5/17.
 */
public class PrivacyRequest {
    private String badgeRequestInum, privacy, opHost;

    public String getBadgeRequestInum() {
        return badgeRequestInum;
    }

    public void setBadgeRequestInum(String badgeRequestInum) {
        this.badgeRequestInum = badgeRequestInum;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getOpHost() {
        return opHost;
    }

    public void setOpHost(String opHost) {
        this.opHost = opHost;
    }
}
