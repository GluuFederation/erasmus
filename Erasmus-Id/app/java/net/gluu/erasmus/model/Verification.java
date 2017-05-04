package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Verification {

    @SerializedName("allowedOrigins")
    @Expose
    private String allowedOrigins;
    @SerializedName("type")
    @Expose
    private String type;

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public Verification withAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Verification withType(String type) {
        this.type = type;
        return this;
    }

}