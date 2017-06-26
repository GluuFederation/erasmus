package net.gluu.erasmus.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadgeRequests {

    @SerializedName("pendingBadgeRequests")
    @Expose
    private List<PendingBadgeRequest> pendingBadgeRequests = null;
    @SerializedName("approvedBadgeRequests")
    @Expose
    private List<ApprovedBadgeRequest> approvedBadgeRequests = null;

    public List<PendingBadgeRequest> getPendingBadgeRequests() {
        return pendingBadgeRequests;
    }

    public void setPendingBadgeRequests(List<PendingBadgeRequest> pendingBadgeRequests) {
        this.pendingBadgeRequests = pendingBadgeRequests;
    }

    public BadgeRequests withPendingBadgeRequests(List<PendingBadgeRequest> pendingBadgeRequests) {
        this.pendingBadgeRequests = pendingBadgeRequests;
        return this;
    }

    public List<ApprovedBadgeRequest> getApprovedBadgeRequests() {
        return approvedBadgeRequests;
    }

    public void setApprovedBadgeRequests(List<ApprovedBadgeRequest> approvedBadgeRequests) {
        this.approvedBadgeRequests = approvedBadgeRequests;
    }

    public BadgeRequests withApprovedBadgerequests(List<ApprovedBadgeRequest> approvedBadgeRequests) {
        this.approvedBadgeRequests = approvedBadgeRequests;
        return this;
    }

}
