package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgeRequest {

    @SerializedName("inum")
    @Expose
    private String inum;
    @SerializedName("participant")
    @Expose
    private String participant;
    @SerializedName("templateBadgeId")
    @Expose
    private String templateBadgeId;
    @SerializedName("templateBadgeTitle")
    @Expose
    private String templateBadgeTitle;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("requesterEmail")
    @Expose
    private String requesterEmail;

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public BadgeRequest withInum(String inum) {
        this.inum = inum;
        return this;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public BadgeRequest withParticipant(String participant) {
        this.participant = participant;
        return this;
    }

    public String getTemplateBadgeId() {
        return templateBadgeId;
    }

    public void setTemplateBadgeId(String templateBadgeId) {
        this.templateBadgeId = templateBadgeId;
    }

    public BadgeRequest withTemplateBadgeId(String templateBadgeId) {
        this.templateBadgeId = templateBadgeId;
        return this;
    }

    public String getTemplateBadgeTitle() {
        return templateBadgeTitle;
    }

    public void setTemplateBadgeTitle(String templateBadgeTitle) {
        this.templateBadgeTitle = templateBadgeTitle;
    }

    public BadgeRequest withTemplateBadgeTitle(String templateBadgeTitle) {
        this.templateBadgeTitle = templateBadgeTitle;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BadgeRequest withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public BadgeRequest withRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
        return this;
    }

}