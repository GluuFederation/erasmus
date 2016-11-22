package gluu.oxd.org.badgemanager.models;


import java.util.Date;

/**
 * Created by Arvind Tomar on 4/10/16.
 * Updated by Arvind Tomar on 7/10/16.
 */

public class Badges {

    String displayName;

    String picture;

    String description;

    String owner;

    boolean active = true;

    String inum;

    String gluuAssociatedOrganization;

    Date gluuBadgeExpiryDate;

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    Date creationDate = new Date();


    Organizations gluuAssociatedOrganizationDetail = new Organizations();
    Person manager;

    public Badges() {
    }

    public Badges(Badges badges) {
        this.displayName = badges.displayName;
        this.picture = badges.picture;
        this.description = badges.description;
        this.owner = badges.owner;
        this.active = badges.active;
        this.inum = badges.inum;
        this.gluuAssociatedOrganization = badges.gluuAssociatedOrganization;
        this.gluuBadgeExpiryDate = badges.gluuBadgeExpiryDate;
        this.creationDate = badges.creationDate;
        this.gluuAssociatedOrganizationDetail = badges.gluuAssociatedOrganizationDetail;
        this.manager = badges.manager;
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

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "Badges{" +
                "displayName:'" + displayName + '\'' +
                ", picture:'" + picture + '\'' +
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