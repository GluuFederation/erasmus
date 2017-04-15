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
@LdapObjectClass(values = {"top", "gluuBadgeRequest"})
public class BadgeRequests {

    @LdapDN
    private String dn;

    @LdapAttribute(name = "inum")
    private String inum;

    @LdapAttribute(name = "masterBadgeId")
    private String templateBadgeId;

    @LdapAttribute(name = "masterBadgeTitle")
    private String templateBadgeTitle;

    @LdapAttribute(name = "organization")
    private String participant;

    @LdapAttribute(name = "status")
    private String status;

    @LdapAttribute(name = "gluuBadgeRequester")
    private String gluuBadgeRequester;

    @LdapAttribute(name = "validity")
    private int validity;

    @LdapAttribute(name = "creationDate")
    Date creationDate;

    @LdapAttribute(name = "updatedAt")
    Date updatedAt;

    public BadgeRequests() {
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getGluuBadgeRequester() {
        return gluuBadgeRequester;
    }

    public void setGluuBadgeRequester(String gluuBadgeRequester) {
        this.gluuBadgeRequester = gluuBadgeRequester;
    }

    public String getTemplateBadgeId() {
        return templateBadgeId;
    }

    public void setTemplateBadgeId(String templateBadgeId) {
        this.templateBadgeId = templateBadgeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTemplateBadgeTitle() {
        return templateBadgeTitle;
    }

    public void setTemplateBadgeTitle(String templateBadgeTitle) {
        this.templateBadgeTitle = templateBadgeTitle;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}