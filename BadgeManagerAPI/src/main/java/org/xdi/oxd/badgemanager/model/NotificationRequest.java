package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 5/6/17.
 */
public class NotificationRequest {
    private String badge, opHost, participant;

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
