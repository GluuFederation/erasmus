package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 19/4/17.
 */
public class BadgeResponse {
    private String context, id, type, issuedOn, expires;

    private Recipient recipient;

    private BadgeVerification verification;

    private BadgeClassResponse badge;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(String issuedOn) {
        this.issuedOn = issuedOn;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public BadgeVerification getVerification() {
        return verification;
    }

    public void setVerification(BadgeVerification verification) {
        this.verification = verification;
    }

    public BadgeClassResponse getBadge() {
        return badge;
    }

    public void setBadge(BadgeClassResponse badge) {
        this.badge = badge;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
