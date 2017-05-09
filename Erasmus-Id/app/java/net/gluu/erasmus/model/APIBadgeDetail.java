package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lcom16 on 9/5/17.
 */

public class APIBadgeDetail {

    @SerializedName("badgeRequestInum")
    @Expose
    private String badgeRequestInum;
    @SerializedName("opHost")
    @Expose
    private String opHost;

    public String getBadgeRequestInum() {
        return badgeRequestInum;
    }

    public void setBadgeRequestInum(String badgeRequestInum) {
        this.badgeRequestInum = badgeRequestInum;
    }

    public String getOpHost() {
        return opHost;
    }

    public void setOpHost(String opHost) {
        this.opHost = opHost;
    }

}
