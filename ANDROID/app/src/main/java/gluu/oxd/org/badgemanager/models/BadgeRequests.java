package gluu.oxd.org.badgemanager.models;

/**
 * Created by Arvind Tomar on 4/10/16.
 * Updated by Arvind Tomar on 7/10/16.
 */

public class BadgeRequests {

    String inum;

    String gluuBadgeClassDN;

    String description;

    String organization;

    boolean active = false;

    boolean requestStatus;

    String gluuBadgeRequester;

    Organizations gluuAssociatedOrganizationDetails = new Organizations();

    Person gluuBadgeRequesterDetail = new Person();

    Badges badgeDetail = new Badges();


    public BadgeRequests() {
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getGluuBadgeClassDN() {
        return gluuBadgeClassDN;
    }

    public void setGluuBadgeClassDN(String gluuBadgeClassDN) {
        this.gluuBadgeClassDN = gluuBadgeClassDN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public boolean isRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(boolean requestStatus) {
        this.requestStatus = requestStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getGluuBadgeRequester() {
        return gluuBadgeRequester;
    }

    public void setGluuBadgeRequester(String gluuBadgeRequester) {
        this.gluuBadgeRequester = gluuBadgeRequester;
    }

    public Organizations getGluuAssociatedOrganizationDetails() {
        return gluuAssociatedOrganizationDetails;
    }

    public void setGluuAssociatedOrganizationDetails(Organizations gluuAssociatedOrganizationDetails) {
        this.gluuAssociatedOrganizationDetails = gluuAssociatedOrganizationDetails;
    }

    public Person getGluuBadgeRequesterDetail() {
        return gluuBadgeRequesterDetail;
    }

    public void setGluuBadgeRequesterDetail(Person gluuBadgeRequesterDetail) {
        this.gluuBadgeRequesterDetail = gluuBadgeRequesterDetail;
    }

    public Badges getBadgeDetail() {
        return badgeDetail;
    }

    public void setBadgeDetail(Badges badgeDetail) {
        this.badgeDetail = badgeDetail;
    }
}