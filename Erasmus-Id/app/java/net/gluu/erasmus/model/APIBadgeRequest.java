package net.gluu.erasmus.model;

/**
 * Created by Meghna Joshi on 5/5/17.
 */

public class APIBadgeRequest {
    private String opHost, status, participant, templateBadgeId, templateBadgeTitle;

    public APIBadgeRequest(String strOpHost, String strStatus) {
        this.opHost = strOpHost;
        this.status = strStatus;
    }

    public APIBadgeRequest(String strOpHost, String strParticipant, String strTemplateBadgeId, String strTemplateBadgeTitle) {
        this.opHost = strOpHost;
        this.participant = strParticipant;
        this.templateBadgeId = strTemplateBadgeId;
        this.templateBadgeTitle = strTemplateBadgeTitle;
    }

    public String getOpHost() {
        return opHost;
    }

    public void setOpHost(String opHost) {
        this.opHost = opHost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getTemplateBadgeId() {
        return templateBadgeId;
    }

    public void setTemplateBadgeId(String templateBadgeId) {
        this.templateBadgeId = templateBadgeId;
    }

    public String getTemplateBadgeTitle() {
        return templateBadgeTitle;
    }

    public void setTemplateBadgeTitle(String templateBadgeTitle) {
        this.templateBadgeTitle = templateBadgeTitle;
    }
}
