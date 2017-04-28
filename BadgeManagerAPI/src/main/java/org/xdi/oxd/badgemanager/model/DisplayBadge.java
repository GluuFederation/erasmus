package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 28/4/17.
 */
public class DisplayBadge {
    private String qrCode, expiresAt, badgeTitle, badgePublicURL;
    private Recipient recipient;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getBadgeTitle() {
        return badgeTitle;
    }

    public void setBadgeTitle(String badgeTitle) {
        this.badgeTitle = badgeTitle;
    }

    public String getBadgePublicURL() {
        return badgePublicURL;
    }

    public void setBadgePublicURL(String badgePublicURL) {
        this.badgePublicURL = badgePublicURL;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }
}
