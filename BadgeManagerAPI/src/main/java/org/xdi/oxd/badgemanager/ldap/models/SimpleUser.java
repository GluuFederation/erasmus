package org.xdi.oxd.badgemanager.ldap.models;

import com.google.gson.annotations.Expose;
import org.gluu.site.ldap.persistence.annotation.LdapCustomObjectClass;
import org.gluu.site.ldap.persistence.annotation.LdapDN;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;
import org.xdi.ldap.model.CustomAttribute;
import org.xdi.util.StringHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@LdapEntry
@LdapObjectClass(
    values = {"top"}
)
public class SimpleUser implements Serializable {
    private static final long serialVersionUID = -1634191420188575733L;
    @LdapDN
    private String dn;
//    @LdapAttribute(
//        name = "uid"
//    )
//    private String userId;
//    @LdapAttribute(
//        name = "oxAuthPersistentJWT"
//    )
//    private String[] oxAuthPersistentJwt;
//    @LdapAttributesList(
//        name = "name",
//        value = "values",
//        sortByName = true
//    )

    @Expose
    protected List<CustomAttribute> customAttributes = new ArrayList();

    @Expose
    @LdapCustomObjectClass
    private String[] customObjectClasses;

    public SimpleUser() {
    }

    public String getDn() {
        return this.dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

//    public String getUserId() {
//        return this.userId;
//    }

//    public void setUserId(String userId) {
//        this.userId = userId;
//    }

//    public String[] getOxAuthPersistentJwt() {
//        return this.oxAuthPersistentJwt;
//    }

//    public void setOxAuthPersistentJwt(String[] oxAuthPersistentJwt) {
//        this.oxAuthPersistentJwt = oxAuthPersistentJwt;
//    }

    public List<CustomAttribute> getCustomAttributes() {
        return this.customAttributes;
    }

    public void setCustomAttributes(List<CustomAttribute> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public String getAttribute(String ldapAttribute) {
        String attribute = null;
        if(ldapAttribute != null && !ldapAttribute.isEmpty()) {
            Iterator i$ = this.customAttributes.iterator();

            while(i$.hasNext()) {
                CustomAttribute customAttribute = (CustomAttribute)i$.next();
                if(customAttribute.getName().equals(ldapAttribute)) {
                    attribute = customAttribute.getValue();
                    break;
                }
            }
        }

        return attribute;
    }

    public List<String> getAttributeValues(String ldapAttribute) {
        List values = null;
        if(ldapAttribute != null && !ldapAttribute.isEmpty()) {
            Iterator i$ = this.customAttributes.iterator();

            while(i$.hasNext()) {
                CustomAttribute customAttribute = (CustomAttribute)i$.next();
                if(StringHelper.equalsIgnoreCase(customAttribute.getName(), ldapAttribute)) {
                    values = customAttribute.getValues();
                    break;
                }
            }
        }

        return values;
    }

    public String[] getCustomObjectClasses() {
        return this.customObjectClasses;
    }

    public void setCustomObjectClasses(String[] customObjectClasses) {
        this.customObjectClasses = customObjectClasses;
    }
}