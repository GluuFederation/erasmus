package org.xdi.oxd.badgemanager.ldap.service;

import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.xdi.oxd.badgemanager.ldap.LDAPInitializer;
import org.xdi.oxd.badgemanager.web.BadgeRequestController;

/**
 * Created by Arvind Tomar on 22/4/17.
 */

@Configuration
public class LDAPService {

    public static boolean connected = false;
    public static LdapEntryManager ldapEntryManager;

    private static final Logger logger = LoggerFactory.getLogger(LDAPService.class);

    public LDAPService() {

    }

    public static boolean isConnected() {

        if (connected || ldapEntryManager != null) {
            ldapEntryManager.destroy();
            LDAPService.ldapEntryManager.destroy();
            connected = false;
            logger.info("Existing LDAP connection closed");
        }

        new LDAPInitializer(new LDAPInitializer.ldapConnectionListner() {
            @Override
            public void ldapConnected(boolean isConnected, LdapEntryManager ldapEntryManager) {
                if (isConnected) {
                    LDAPService.ldapEntryManager = ldapEntryManager;
                    connected = isConnected;
                }
            }
        });

        return connected;
    }

    public static void setConnected(boolean connected) {
        LDAPService.connected = connected;
    }
}
