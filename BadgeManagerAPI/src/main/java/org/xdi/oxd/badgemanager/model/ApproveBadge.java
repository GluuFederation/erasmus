package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 14/4/17.
 */
public class ApproveBadge {

    private String inum, privacy;
    private int validity;

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
