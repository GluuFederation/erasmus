package org.xdi.oxd.badgemanager.ldap.service;

import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.springframework.context.annotation.Configuration;
import org.xdi.oxd.badgemanager.ldap.LDAPInitializer;

/**
 * Created by Arvind Tomar on 22/4/17.
 */

@Configuration
public class LDAPService {

    public static boolean connected = false;
    public static LdapEntryManager ldapEntryManager;

    public LDAPService() {

    }

    public static boolean isConnected() {
        if (ldapEntryManager == null || !connected) {
            new LDAPInitializer(new LDAPInitializer.ldapConnectionListner() {
                @Override
                public void ldapConnected(boolean isConnected, LdapEntryManager ldapEntryManager) {
                    if (isConnected) {
                        LDAPService.ldapEntryManager = ldapEntryManager;
                        connected = isConnected;
                    }
                }
            });
        }

        return connected;
    }

    public static void setConnected(boolean connected) {
        LDAPService.connected = connected;
    }
}
