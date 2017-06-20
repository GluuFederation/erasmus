package org.xdi.oxd.badgemanager.model;

import java.util.List;

/**
 * Created by Arvind Tomar on 27/4/17.
 */
public class BadgeRequestResponse {
    private List<CreateBadgeResponse> pendingBadgeRequests;
    private List<CreateBadgeResponse> approvedBadgerequests;

    public List<CreateBadgeResponse> getPendingBadgeRequests() {
        return pendingBadgeRequests;
    }

    public void setPendingBadgeRequests(List<CreateBadgeResponse> pendingBadgeRequests) {
        this.pendingBadgeRequests = pendingBadgeRequests;
    }

    public List<CreateBadgeResponse> getApprovedBadgerequests() {
        return approvedBadgerequests;
    }

    public void setApprovedBadgerequests(List<CreateBadgeResponse> approvedBadgerequests) {
        this.approvedBadgerequests = approvedBadgerequests;
    }
}
