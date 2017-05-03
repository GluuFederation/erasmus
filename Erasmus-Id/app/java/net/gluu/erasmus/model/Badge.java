package net.gluu.erasmus.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Badge {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("narrative")
    @Expose
    private String narrative;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("issuer")
    @Expose
    private Issuer issuer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Badge withId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Badge withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Badge withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Badge withImage(String image) {
        this.image = image;
        return this;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public Badge withNarrative(String narrative) {
        this.narrative = narrative;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Badge withType(String type) {
        this.type = type;
        return this;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public Badge withIssuer(Issuer issuer) {
        this.issuer = issuer;
        return this;
    }

}
