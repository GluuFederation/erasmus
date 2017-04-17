package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.Organizations;
import org.xdi.oxd.badgemanager.ldap.models.Person;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;

import java.util.List;

/**
 * Created by Arvind Tomar on 4/10/16.
 */
@Component
@Configuration
@PropertySource("classpath:application.properties")
public class LdapCommands {

    @Autowired
    Environment env;


    /**
     * Finind person By Inum
     * @param ldapEntryManager
     * @param person
     * @return
     */
    public List<Person> getPersonByInum(LdapEntryManager ldapEntryManager, Person person) {

        try {
            return ldapEntryManager.findEntries(person.getDn(), Person.class, Filter.create("(inum=" + person.getInum() + ")"));
        } catch (LDAPException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * create new Person entry or update if exists
     * @param ldapEntryManager
     * @param person
     * @return
     */

    public boolean createOrUpdatePerson(LdapEntryManager ldapEntryManager, Person person) {
        if (person.getSub() != null || person.getSub().length() > 2) {
            try {
                person.setDn("inum=" + person.getSub() + ",ou=people,o=" + DefaultConfig.config_organization + ",o=gluu");
                person.setInum(person.getSub());
                if (!(ldapEntryManager.contains(person.getDn(), Person.class, Filter.create("(inum=" + person.getInum() + ")")))) {
                    ldapEntryManager.persist(person);
                    System.out.println("new organization entry");
                    return true;
                } else {
                    Person p = ldapEntryManager.findEntries(person.getDn(), Person.class, Filter.create("(inum=" + person.getInum() + ")")).get(0);
                    if (p.getEmail() != null && p.getEmail().length() > 0 && person.getEmail() != null && person.getEmail().length() > 0) {
                        person.setEmail(p.getEmail());
                    } else {
                        MergeService.merge(p,person);
                        ldapEntryManager.merge(person);
                    }
                    System.out.println("Use modified with dn" + person.getDn());
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("person inum/sub not available");
            return false;
        }
    }

    /**
     *
     * @param ldapEntryManager
     * @param person
     * @return
     */
    public boolean createOrUpdatePersonOrganization(LdapEntryManager ldapEntryManager, Person person) {
        Organizations org = new Organizations();
        org.setDn("inum=" + person.getSub() + ",ou=organizations,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
        try {
            if (ldapEntryManager.contains(org.getDn(), Organizations.class, Filter.create("(inum=" + org.getO() + ")"))) {
                ldapEntryManager.persist(org);
                System.out.println("new organization entry for organization");
                return true;
            } else {
                org.setDescription("this test organization");
                org.setDisplayName("test display name");
                org.setPicture(person.getPicture());
                org.setO(person.getInum());
                ldapEntryManager.persist(org);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
