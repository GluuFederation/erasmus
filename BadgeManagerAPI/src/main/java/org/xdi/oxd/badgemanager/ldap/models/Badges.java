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
@LdapObjectClass(values = {"top", "gluuBadgeAssertion"})
public class Badges  {

    @LdapDN
    private String dn;

    @LdapAttribute(name = "gluuContext")
    String context;

    @LdapAttribute(name = "gluuBadgeClassInum")
    String badgeClassInum;

    @LdapAttribute(name = "gluuExpires")
    Date expires = new Date();

    @LdapAttribute(name = "gluuId")
    String id;

    @LdapAttribute(name = "inum")
    String inum;

    @LdapAttribute(name = "gluuImage")
    String image;

    @LdapAttribute(name = "gluuIssuedOn")
    Date issuedOn = new Date();

    @LdapAttribute(name = "gluuRecipientIdentity")
    String recipientIdentity;

    @LdapAttribute(name = "gluuRecipientType")
    String recipientType;

    @LdapAttribute(name = "gluuType")
    String type;

    @LdapAttribute(name = "gluuVerificationType")
    String verificationType;

    @LdapAttribute(name = "gluuBadgeAssertionId")
    String guid;

    @LdapAttribute(name = "gluuBadgeAssertionKey")
    String key;

    public Badges() {
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getBadgeClassInum() {
        return badgeClassInum;
    }

    public void setBadgeClassInum(String badgeClassInum) {
        this.badgeClassInum = badgeClassInum;
    }

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(Date issuedOn) {
        this.issuedOn = issuedOn;
    }

    public String getRecipientIdentity() {
        return recipientIdentity;
    }

    public void setRecipientIdentity(String recipientIdentity) {
        this.recipientIdentity = recipientIdentity;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
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

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }
}