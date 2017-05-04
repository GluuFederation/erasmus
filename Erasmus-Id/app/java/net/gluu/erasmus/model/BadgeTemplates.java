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

}