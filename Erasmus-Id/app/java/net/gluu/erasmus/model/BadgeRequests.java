package net.gluu.erasmus.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgeRequests {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("errorMsg")
    @Expose
    private String errorMsg;
    @SerializedName("badgeRequests")
    @Expose
    private List<BadgeRequest> badgeRequests = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public BadgeRequests withError(Boolean error) {
        this.error = error;
        return this;
    }

    public List<BadgeRequest> getBadgeRequests() {
        return badgeRequests;
    }

    public void setBadgeRequests(List<BadgeRequest> badgeRequests) {
        this.badgeRequests = badgeRequests;
    }

    public BadgeRequests withBadgeRequests(List<BadgeRequest> badgeRequests) {
        this.badgeRequests = badgeRequests;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public BadgeRequests withErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

}