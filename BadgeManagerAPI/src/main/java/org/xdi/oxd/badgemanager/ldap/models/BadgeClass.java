package org.xdi.oxd.badgemanager.ldap.models;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapDN;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

import java.util.Date;

/**
 * Created by Arvind Tomar on 4/10/16.
 * Updated by Arvind Tomar on 7/10/16.
 */

@LdapEntry
@LdapObjectClass(values = {"top", "gluuBadgeClass"})
public class BadgeClass {

    @LdapDN
    private String dn;

    @LdapAttribute(name = "gluuType")
    String type;

    @LdapAttribute(name = "name")
    String name;

    @LdapAttribute(name = "description")
    String description;

    @LdapAttribute(name = "gluuImage")
    String image;

    @LdapAttribute(name = "inum")
    String inum;

    @LdapAttribute(name = "creationDate")
    Date creationDate = new Date();

    @LdapAttribute(name = "gluuTemplateBadgeId")
    String templateBadgeId;

    @LdapAttribute(name = "gluuBadgeRequestInum")
    String badgeRequestInum;

    @LdapAttribute(name = "gluuId")
    String id;

    @LdapAttribute(name = "gluuBadgeClassId")
    String guid;

    @LdapAttribute(name = "gluuBadgeClassKey")
    String key;

    public BadgeClass() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTemplateBadgeId() {
        return templateBadgeId;
    }

    public void setTemplateBadgeId(String templateBadgeId) {
        this.templateBadgeId = templateBadgeId;
    }

    public String getBadgeRequestInum() {
        return badgeRequestInum;
    }

    public void setBadgeRequestInum(String badgeRequestInum) {
        this.badgeRequestInum = badgeRequestInum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}