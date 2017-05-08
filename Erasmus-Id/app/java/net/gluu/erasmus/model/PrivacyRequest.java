package net.gluu.erasmus.model;

/**
 * Created by Meghna Joshi on 6/5/17.
 */

public class PrivacyRequest {
    private String badgeRequestInum, privacy;

    public PrivacyRequest(String strBadgeRequestInum, String strPrivacy) {
        this.badgeRequestInum = strBadgeRequestInum;
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
}
