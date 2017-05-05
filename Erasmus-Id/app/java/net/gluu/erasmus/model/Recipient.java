package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipient {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("identity")
    @Expose
    private String identity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Recipient withType(String type) {
        this.type = type;
        return this;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Recipient withIdentity(String identity) {
        this.identity = identity;
        return this;
    }

}