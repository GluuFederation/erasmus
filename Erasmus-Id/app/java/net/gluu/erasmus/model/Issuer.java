package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Issuer {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("verification")
    @Expose
    private Verification verification;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Issuer withId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Issuer withName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Issuer withType(String type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Issuer withUrl(String url) {
        this.url = url;
        return this;
    }

    public Verification getVerification() {
        return verification;
    }

    public void setVerification(Verification verification) {
        this.verification = verification;
    }

    public Issuer withVerification(Verification verification) {
        this.verification = verification;
        return this;
    }

}