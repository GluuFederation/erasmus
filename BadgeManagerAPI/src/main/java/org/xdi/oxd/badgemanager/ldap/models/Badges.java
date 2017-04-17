package org.xdi.oxd.badgemanager.ldap.models;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

import java.util.Date;

/**
 * Created by Arvind Tomar on 4/10/16.
 * Updated by Arvind Tomar on 7/10/16.
 */

@LdapEntry
@LdapObjectClass(values = {"top", "gluuBadgeClass"})
public class Badges extends SimpleUser {

    @LdapAttribute(name = "displayName")
    String displayName;

    @LdapAttribute(name = "picture")
    String picture;

    @LdapAttribute(name = "description")
    String description;

    @LdapAttribute(name = "owner")
    String owner;

    @LdapAttribute(name = "gluuStatus")
    boolean active = true;

    @LdapAttribute(name = "inum")
    String inum;

    @LdapAttribute(name = "gluuAssociatedOrganization")
    String gluuAssociatedOrganization;

    @LdapAttribute(name = "gluuBadgeExpiryDate")
    Date gluuBadgeExpiryDate;

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @LdapAttribute(name = "creationDate")
    Date creationDate = new Date();

    @LdapAttribute(name = "qrCode")
    String qrCode;

    Organizations gluuAssociatedOrganizationDetail = new Organizations();


    public Badges() {
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getGluuAssociatedOrganization() {
        return gluuAssociatedOrganization;
    }

    public void setGluuAssociatedOrganization(String gluuAssociatedOrganization) {
        this.gluuAssociatedOrganization = gluuAssociatedOrganization;
    }

    public Date getGluuBadgeExpiryDate() {
        return gluuBadgeExpiryDate;
    }

    public void setGluuBadgeExpiryDate(Date gluuBadgeExpiryDate) {
        this.gluuBadgeExpiryDate = gluuBadgeExpiryDate;
    }

    public Date getCreationDate() {
        return creationDate;
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

    public Organizations getGluuAssociatedOrganizationDetail() {
        return gluuAssociatedOrganizationDetail;
    }

    public void setGluuAssociatedOrganizationDetail(Organizations gluuAssociatedOrganizationDetail) {
        this.gluuAssociatedOrganizationDetail = gluuAssociatedOrganizationDetail;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public String toString() {
        return "Badges{" +
                "displayName:'" + displayName + '\'' +
                ", picture:'" + picture + '\'' +
                ", qrCode:'" + qrCode + '\'' +
                ", description:'" + description + '\'' +
                ", owner:'" + owner + '\'' +
                ", active:" + active +
                ", inum:'" + inum + '\'' +
                ", gluuAssociatedOrganization:'" + gluuAssociatedOrganization + '\'' +
                ", gluuBadgeExpiryDate:" + gluuBadgeExpiryDate +
                ", creationDate:" + creationDate +
                ", gluuAssociatedOrganizationDetail:" + gluuAssociatedOrganizationDetail +
                '}';
    }
}