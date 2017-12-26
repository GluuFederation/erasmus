package net.gluu.erasmus.model;

/**
 * Created by Arvind Tomar on 10/6/17.
 */

public class NotificationRequest {
    private String badge;
    private String opHost;
    private String participant;
    private String validatorToken;

    public NotificationRequest(String badge, String opHost, String participant, String validatorToken) {
        this.badge = badge;
        this.opHost = opHost;
        this.participant = participant;
        this.validatorToken = validatorToken;
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

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }
}
