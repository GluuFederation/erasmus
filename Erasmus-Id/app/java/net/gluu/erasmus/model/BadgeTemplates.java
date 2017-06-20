package net.gluu.erasmus.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgeTemplates {

    @SerializedName("badges")
    @Expose
    private List<Badge> badges = null;
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("errorMsg")
    @Expose
    private String errorMsg;

    public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }

    public BadgeTemplates withBadges(List<Badge> badges) {
        this.badges = badges;
        return this;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public BadgeTemplates withError(Boolean error) {
        this.error = error;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public BadgeTemplates withErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}