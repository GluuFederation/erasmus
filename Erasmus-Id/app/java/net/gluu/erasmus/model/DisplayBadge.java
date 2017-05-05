package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DisplayBadge {

    @SerializedName("qrCode")
    @Expose
    private String qrCode;
    @SerializedName("expiresAt")
    @Expose
    private String expiresAt;
    @SerializedName("badgeTitle")
    @Expose
    private String badgeTitle;
    @SerializedName("badgePublicURL")
    @Expose
    private String badgePublicURL;
    @SerializedName("recipient")
    @Expose
    private Recipient recipient;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public DisplayBadge withQrCode(String qrCode) {
        this.qrCode = qrCode;
        return this;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public DisplayBadge withExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public String getBadgeTitle() {
        return badgeTitle;
    }

    public void setBadgeTitle(String badgeTitle) {
        this.badgeTitle = badgeTitle;
    }

    public DisplayBadge withBadgeTitle(String badgeTitle) {
        this.badgeTitle = badgeTitle;
        return this;
    }

    public String getBadgePublicURL() {
        return badgePublicURL;
    }

    public void setBadgePublicURL(String badgePublicURL) {
        this.badgePublicURL = badgePublicURL;
    }

    public DisplayBadge withBadgePublicURL(String badgePublicURL) {
        this.badgePublicURL = badgePublicURL;
        return this;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public DisplayBadge withRecipient(Recipient recipient) {
        this.recipient = recipient;
        return this;
    }

}