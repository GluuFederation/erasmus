package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.gluu.site.ldap.persistence.exception.EntryPersistenceException;
import org.jboss.resteasy.spi.NotFoundException;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.Organizations;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;

import java.util.List;

/**
 * Created by Arvind Tomar on 8/10/16.
 */
public class OrganizationCommands {

    /**
     * Create a new organization
     *
     * @param ldapEntryManager ldapEntryManager
     * @param organization     object of class organization which mush have gluuManager attribute
     * @return
     * @throws Exception
     */
    public static Organizations createOrganization(LdapEntryManager ldapEntryManager, Organizations organization) throws Exception {

        if (PersonCommands.getPersonByEmail(ldapEntryManager, organization.getGluuManager()) != null) {
            String inum = InumService.getInum(InumService.organizationPrefix);
            organization.setDn("inum=" + inum + ",ou=organizations,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            organization.setInum(inum);
            if (!(ldapEntryManager.contains(organization.getDn(), Organizations.class, Filter.create("(inum=" + organization.getInum() + ")")))) {
                if (!(ldapEntryManager.contains("ou=organizations,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", Organizations.class, Filter.create("(displayName=" + organization.getDisplayName() + ")")))) {
                    ldapEntryManager.persist(organization);
                    System.out.println("new organization entry");
                } else {
                    throw new EntryPersistenceException("Already exists with same display name");
                }
                return organization;
            } else {
                createOrganization(ldapEntryManager, organization);
            }
        } else {
            System.out.println("Error occurred");
            throw new NotFoundException("User with specified email does not exist in system");
        }
        throw new NotFoundException("There was error creating new organization");
    }

    /**
     * To update an organization
     *
     * @param ldapEntryManager ldapEntryManager
     * @param organization     pass organization object with updated properties
     * @return
     */
    public static Organizations updateOrganization(LdapEntryManager ldapEntryManager, Organizations organization) throws Exception {

        if (PersonCommands.getPersonByEmail(ldapEntryManager, organization.getGluuManager()) != null) {
            organization.setDn("inum=" + organization.getInum() + ",ou=organizations,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            organization.setInum(organization.getInum());
            if (ldapEntryManager.contains(organization.getDn(), Organizations.class, Filter.create("(inum=" + organization.getInum() + ")"))) {
                MergeService.merge(organization,ldapEntryManager.findEntries(organization.getDn(), Organizations.class, Filter.create("(inum=" + organization.getInum() + ")")).get(0));
                ldapEntryManager.merge(organization);
                return organization;
            } else {
                throw new NotFoundException("No such organization found");
            }
        } else {
            throw new NotFoundException("User with specified email does not exist in system");
        }
    }

    /**
     * Get organization information according to Inum of the organization
     *
     * @param ldapEntryManager ldapEntryManager
     * @param Inum             Inum of the organization to be retrieved
     * @return
     */
    public static Organizations getOrganizationByInum(LdapEntryManager ldapEntryManager, String Inum) {
        try {

            Organizations organization = new Organizations();
            organization.setInum(Inum);
            organization.setDn("inum=" + Inum + ",ou=organizations,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            if (ldapEntryManager.contains(organization.getDn(), Organizations.class, Filter.create("(inum=" + organization.getInum() + ")"))) {

                List<Organizations> orgs = ldapEntryManager.findEntries(organization.getDn(), Organizations.class, Filter.create("(inum=" + organization.getInum() + ")"));

                if (orgs.size() > 0)
                    return orgs.get(0);
                else
                    return null;

            } else {

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete an organization by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param Inum             Inum of the organization to be deleted
     * @return
     */
    public static boolean removeOrganization(LdapEntryManager ldapEntryManager, String Inum) {
        try {

            Organizations organization = getOrganizationByInum(ldapEntryManager, Inum);

            if (organization != null) {
                ldapEntryManager.remove(organization);
                System.out.println("Deleted entry ");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieve an organization according to DN.
     *
     * @param ldapEntryManager
     * @param dn               dn of the organization to be retrieved
     * @return
     */
    public static Organizations findOrganizationfromDN(LdapEntryManager ldapEntryManager, String dn) {
        try {
            List<Organizations> organizations = ldapEntryManager.findEntries(dn, Organizations.class, null);
            if (organizations.size() > 0) {
                return organizations.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Organizations> getOrganizations(LdapEntryManager ldapEntryManager) {
        Organizations organization = new Organizations();
        organization.setDn("ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
        return (ldapEntryManager.findEntries(organization.getDn(), Organizations.class, null));
    }
}
