package gluu.oxd.org.badgemanager.models;


/**
 * Created by Arvind Tomar on 4/10/16.
 */
public class Organizations {

    String displayName;

    String picture;
    String description;

    String inum;

    String o;

    String gluuManager;
    String dn;


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


    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
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
