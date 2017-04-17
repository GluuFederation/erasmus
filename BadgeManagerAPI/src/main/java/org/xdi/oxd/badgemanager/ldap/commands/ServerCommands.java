package org.xdi.oxd.badgemanager.ldap.commands;

import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.xdi.ldap.model.SearchScope;
import org.xdi.oxd.badgemanager.ldap.models.Organizations;

import java.util.List;

public class ServerCommands {

    /**
     * @param ldapEntryManager
     * @return
     * @throws Exception fetch root organization's Inum
     */
    public static String getRootOrgranizationInum(LdapEntryManager ldapEntryManager) throws Exception {

        List<Organizations> organizations = ldapEntryManager.findEntries("o=gluu", Organizations.class, null, SearchScope.BASE);
        for (Organizations o : organizations) {
            if (o.getO() != null) {
                System.out.print("Root Organization found");
                return o.getO();
            }
        }
        throw new Exception("Organization inum not found");
    }
}
