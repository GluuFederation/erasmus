package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgeRequest {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("errorMsg")
    @Expose
    private String errorMsg;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("badgeRequests")
    @Expose
    private BadgeRequests badgeRequests;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public BadgeRequest withError(Boolean error) {
        this.error = error;
        return this;
    }

    public BadgeRequests getBadgeRequests() {
        return badgeRequests;
    }

    public void setBadgeRequests(BadgeRequests badgeRequests) {
        this.badgeRequests = badgeRequests;
    }

    public BadgeRequest withBadgeRequests(BadgeRequests badgeRequests) {
        this.badgeRequests = badgeRequests;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public BadgeRequest withErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BadgeRequest withMessage(String message) {
        this.message = message;
        return this;
    }
}