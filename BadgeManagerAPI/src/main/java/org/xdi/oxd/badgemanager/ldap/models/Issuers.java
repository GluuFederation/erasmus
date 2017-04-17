package org.xdi.oxd.badgemanager.ldap.models;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

/**
 * Created by Arvind Tomar on 4/10/16.
 * Updated by Arvind Tomar on 7/10/16.
 */
@LdapEntry
@LdapObjectClass(values = {"top", "gluuIssuer"})
public class Issuers extends SimpleUser {

    @LdapAttribute(name = "displayName")
    String displayName;

    @LdapAttribute(name = "picture")
    String picture;

    @LdapAttribute(name = "description")
    String description;

    @LdapAttribute(name = "inum")
    String inum;

    @LdapAttribute(name = "owner")
    String owner;

    @LdapAttribute(name = "gluuStatus")
    boolean active;

    @LdapAttribute(name = "gluuAssociatedOrganization")
    String organization;

    String email;
    Organizations gluuAssociatedOrganizationDetail;
    Person ownerDetails;

    public Issuers() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Organizations getGluuAssociatedOrganizationDetail() {
        return gluuAssociatedOrganizationDetail;
    }

    public void setGluuAssociatedOrganizationDetail(Organizations gluuAssociatedOrganizationDetail) {
        this.gluuAssociatedOrganizationDetail = gluuAssociatedOrganizationDetail;
    }

    public Person getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(Person ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"displayName\":\"").append(this.displayName).append("\"");
        sb.append(", \"picture\":\"").append(this.picture).append("\"");
        sb.append(", \"description\":\"").append(this.description).append("\"");
        sb.append(", \"inum\":\"").append(this.inum).append("\"");
        sb.append(", \"owner\":\"").append(this.owner).append("\"");
        sb.append(", \"active\":\"").append(this.active).append("\"");
        sb.append(", \"gluuAssociatedOrganization\":\"").append(this.organization).append("\"");
        sb.append('}');
        return sb.toString();


    }
}