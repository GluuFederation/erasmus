package org.xdi.oxd.badgemanager.model;

/**
 * Created by Arvind Tomar on 10/4/17.
 */
public class IssuerBadgeRequest {
    private String issuer,type;

    public IssuerBadgeRequest(String iss, String type){
        this.issuer=iss;
        this.type=type;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
