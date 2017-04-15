package org.xdi.oxd.badgemanager.ldap.models;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

/**
 * Created by Arvind Tomar on 4/10/16.
 */
@LdapEntry
@LdapObjectClass(values = {"top", "gluuOrganization"})
public class Organizations extends SimpleUser {

    @LdapAttribute(name = "displayName")
    String displayName;

    @LdapAttribute(name = "picture")
    String picture;

    @LdapAttribute(name = "description")
    String description;

    @LdapAttribute(name = "inum")
    String inum;

    @LdapAttribute(name = "o")
    String o;

    @LdapAttribute(name = "gluuManager")
    String gluuManager;


    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
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

    public String getGluuManager() {
        return gluuManager;
    }

    public void setGluuManager(String gluuManager) {
        this.gluuManager = gluuManager;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"displayName\":\"").append(this.displayName).append("\"");
        sb.append(", \"picture\":\"").append(this.picture).append("\"");
        sb.append(", \"description\":\"").append(this.description).append("\"");
        sb.append(", \"inum\":\"").append(this.inum).append("\"");
        sb.append(", \"o\":\"").append(this.o).append("\"");
        sb.append(", \"gluuManager\":\"").append(this.gluuManager).append("\"");
        sb.append('}');
        return sb.toString();
    }
}
