package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.jboss.resteasy.spi.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.model.util.Base64Util;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.Person;
import org.xdi.oxd.badgemanager.ldap.models.fido.u2f.DeviceRegistration;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;
import org.xdi.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arvind Tomar on 13/10/16.
 */
public class PersonCommands {

    private static final Logger logger = LoggerFactory.getLogger(PersonCommands.class);

    /**
     * Get person object by email.
     *
     * @param email email of the user to be retrieved
     * @return
     * @throws Exception
     */
    public static Person getPersonByEmail(String email) throws Exception {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getPersonByEmail() in PersonCommands");
                Person person = new Person();
                person.setEmail(email);
                logger.info("LDAP started retrieving person");
                List<Person> persons = LDAPService.ldapEntryManager.findEntries("ou=people,o=" + DefaultConfig.config_organization + ",o=gluu", Person.class, Filter.create("mail=" + person.getEmail()));
                logger.info("LDAP completed retrieving person");
                if (!persons.isEmpty()) {
                    return persons.get(0);
                } else
                    return null;
            } else {
                logger.error("Error in connecting database in getPersonByEmail() in PersonCommands");
                return null;
            }
        } catch (LDAPException e) {
            e.printStackTrace();
            logger.info("Exception in retrieving person in LDAP: " + e.getMessage());
            return null;
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
     *
     * @param ldapEntryManager
     * @param person
     * @return
     * @throws Exception
     */
    public static boolean updatePerson(LdapEntryManager ldapEntryManager, Person person) throws Exception {

        Person p = getPersonByEmail(person.getEmail());

        if (p != null) {
            p.setRole(person.getRole());
            MergeService.merge(person, p);
            ldapEntryManager.merge(p);
            return true;
        } else {
            throw new NotFoundException("no user available with provided email address");

        }
    }

    /**
     * .Delete Person entry
     *
     * @param ldapEntryManager
     * @param person
     * @return
     * @throws Exception
     */
    public static boolean deletePerson(LdapEntryManager ldapEntryManager, Person person) throws Exception {

        Person p = getPersonByEmail(person.getEmail());

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
     *
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

    /**
     * Get device registration object by user inum.
     *
     * @param userInum inum of the user
     * @return
     * @throws Exception
     */
    public static List<DeviceRegistration> getDeviceRegistrationByPerson(String userInum) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getDeviceByPerson() in PersonCommands");
                DeviceRegistration deviceRegistration = new DeviceRegistration();
                deviceRegistration.setDn("ou=fido,inum=" + userInum + ",ou=people,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving devices");
                List<DeviceRegistration> deviceRegistrations = LDAPService.ldapEntryManager.findEntries(deviceRegistration.getDn(), DeviceRegistration.class, null);
                logger.info("LDAP completed retrieving devices");
                return deviceRegistrations;
            } else {
                logger.error("Error in connecting database in getDeviceByPerson() in PersonCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving device in LDAP: " + e.getMessage());
            return null;
        }
    }
}
