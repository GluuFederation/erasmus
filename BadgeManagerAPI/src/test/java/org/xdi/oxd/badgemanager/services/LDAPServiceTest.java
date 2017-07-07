package org.xdi.oxd.badgemanager.services;

import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.junit.Assert;
import org.xdi.oxd.badgemanager.OxdSpringApplication;
import org.xdi.oxd.badgemanager.ldap.LDAPInitializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OxdSpringApplication.class)
public class LDAPServiceTest {

    @Test
    public void getLdapEntryNManger() {
        new LDAPInitializer(new LDAPInitializer.ldapConnectionListner() {
            @Override
            public void ldapConnected(boolean isConnected, LdapEntryManager ldapEntryManager) {
                Assert.assertTrue(isConnected);
                System.out.println("isConnected = [" + isConnected + "], ldapEntryManager = [" + ldapEntryManager + "]");
            }
        });
    }
}
