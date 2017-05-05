package net.gluu.erasmus.model;

/**
 * Created by Meghna Joshi on 5/5/17.
 */

public class TemplateBadgeRequest {
    private String opHost, type;

    public TemplateBadgeRequest(String strOpHost, String strType) {
        this.opHost = strOpHost;
        this.type = strType;
    }

    public String getOpHost() {
        return opHost;
    }

    public void setOpHost(String opHost) {
        this.opHost = opHost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
