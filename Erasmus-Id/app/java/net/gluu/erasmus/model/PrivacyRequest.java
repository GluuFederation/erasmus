package net.gluu.erasmus.model;

/**
 * Created by Meghna Joshi on 6/5/17.
 */

public class PrivacyRequest {
    private String badgeRequestInum, opHost, privacy;

    public PrivacyRequest(String strBadgeRequestInum, String strOpHost, String strPrivacy) {
        this.badgeRequestInum = strBadgeRequestInum;
        this.opHost = strOpHost;
        this.privacy = strPrivacy;
    }

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
