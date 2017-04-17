package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.jboss.resteasy.spi.NotFoundException;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.Person;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;

import java.util.List;

/**
 * Created by Arvind Tomar on 13/10/16.
 */
public class PersonCommands {

    /**
     * Get person object by email.
     *
     * @param ldapEntryManager ldapEntryManager
     * @param email            email of the user to be retrieved
     * @return
     * @throws Exception
     */
    public static Person getPersonByEmail(LdapEntryManager ldapEntryManager, String email) throws Exception {
        Person person = new Person();
        person.setEmail(email);
        try {
            List<Person> persons = ldapEntryManager.findEntries("ou=people,o=" + DefaultConfig.config_organization + ",o=gluu", Person.class, Filter.create("mail=" + person.getEmail()));
            if (!persons.isEmpty()) {
                return persons.get(0);
            } else {
                throw new NotFoundException("User with specified email does not exist in system");
            }

        } catch (LDAPException e) {
            e.printStackTrace();
            throw new NotFoundException("User with specified email does not exist in system");
        }
    }

    /**
     * Get person object by dn.
     *
     * @param ldapEntryManager ldapEntryManager
     * @param dn               dn of the user to be retrieved
     * @return
     * @throws Exception
     */
    public static Person findPersonfromDN(LdapEntryManager ldapEntryManager, String dn) {
        try {
            List<Person> persons = ldapEntryManager.findEntries(dn, Person.class, null);
            if (persons.size() > 0) {
                return persons.get(0);
            } else {
                throw new NotFoundException("No person found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No person found");
        }
    }

    /**
     * Update PersonEntry
     * @param ldapEntryManager
     * @param person
     * @return
     * @throws Exception
     */
    public static boolean updatePerson(LdapEntryManager ldapEntryManager, Person person) throws Exception {

        Person p = getPersonByEmail(ldapEntryManager, person.getEmail());

        if (p != null) {
            p.setRole(person.getRole());
            MergeService.merge(person,p);
            ldapEntryManager.merge(p);
            return true;
        } else {
            throw new NotFoundException("no user available with provided email address");

        }
    }

    /**
     * .Delete Person entry
     * @param ldapEntryManager
     * @param person
     * @return
     * @throws Exception
     */
    public static boolean deletePerson(LdapEntryManager ldapEntryManager, Person person) throws Exception {

        Person p = getPersonByEmail(ldapEntryManager, person.getEmail());

        if (p != null) {
            person.setDn(p.getDn());
            ldapEntryManager.remove(person);
            return true;
        } else {
            throw new NotFoundException("no user available with provided email address");

        }
    }

    /**
     * fetch list of all Persons
     * @param ldapEntryManager
     * @return
     */
    public static final List<Person> getPersonList(LdapEntryManager ldapEntryManager) {
        try {
            List<Person> persons = ldapEntryManager.findEntries("ou=people,o=" + DefaultConfig.config_organization + ",o=gluu", Person.class, null);

            if (persons.size() > 0) {
                return persons;
            } else {
                throw new NotFoundException("No persons found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No persons found");
        }
    }
}
