package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingBadgeRequest {

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
    @SerializedName("privacy")
    @Expose
    private String privacy;

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public PendingBadgeRequest withInum(String inum) {
        this.inum = inum;
        return this;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public PendingBadgeRequest withParticipant(String participant) {
        this.participant = participant;
        return this;
    }

    public String getTemplateBadgeId() {
        return templateBadgeId;
    }

    public void setTemplateBadgeId(String templateBadgeId) {
        this.templateBadgeId = templateBadgeId;
    }

    public PendingBadgeRequest withTemplateBadgeId(String templateBadgeId) {
        this.templateBadgeId = templateBadgeId;
        return this;
    }

    public String getTemplateBadgeTitle() {
        return templateBadgeTitle;
    }

    public void setTemplateBadgeTitle(String templateBadgeTitle) {
        this.templateBadgeTitle = templateBadgeTitle;
    }

    public PendingBadgeRequest withTemplateBadgeTitle(String templateBadgeTitle) {
        this.templateBadgeTitle = templateBadgeTitle;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PendingBadgeRequest withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public PendingBadgeRequest withRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
        return this;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public PendingBadgeRequest withPrivacy(String privacy) {
        this.privacy = privacy;
        return this;
    }

}