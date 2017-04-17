package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.jboss.resteasy.spi.NotFoundException;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.models.Issuers;
import org.xdi.oxd.badgemanager.ldap.models.Person;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;

import java.util.List;

/**
 * Created by Arvind Tomar on 10/7/16.
 */
public class IssuersCommands {

    /**
     * Creates new badge that belongs to an organization
     *
     * @param ldapEntryManager ldapEntryManager
     * @param issuers          Object of the badge that is to be created
     * @return
     * @throws Exception
     */
    public static Issuers createIssuer(LdapEntryManager ldapEntryManager, Issuers issuers) throws Exception {

        String inum = InumService.getInum(InumService.badgeIssuerPrefix);
        issuers.setDn("inum=" + inum + ",ou=gluuIssuer,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
        issuers.setInum(inum);

        if (issuers.getEmail() != null || issuers.getEmail() != null) {
            Person person = PersonCommands.getPersonByEmail(ldapEntryManager, issuers.getEmail());

            if (person != null) {
                issuers.setOwner(person.getDn());
                if (OrganizationCommands.findOrganizationfromDN(ldapEntryManager, issuers.getOrganization()) != null && PersonCommands.findPersonfromDN(ldapEntryManager, issuers.getOwner()) != null) {
                    if (!(ldapEntryManager.contains(issuers.getDn(), Issuers.class, Filter.create("(inum=" + issuers.getInum() + ")")))) {
                        ldapEntryManager.persist(issuers);
                        issuers.setOwnerDetails(PersonCommands.getPersonByEmail(ldapEntryManager, issuers.getEmail()));
                        issuers.setGluuAssociatedOrganizationDetail(OrganizationCommands.findOrganizationfromDN(ldapEntryManager, issuers.getOrganization()));
                        return issuers;
                    } else {
                        createIssuer(ldapEntryManager, issuers);
                    }
                } else {
                    throw new NotFoundException("No matching organization or owner found");
                }
            } else {
                throw new NotFoundException("No matching User or owner found");

            }
        } else {
            throw new NotFoundException("No matching User or owner found");

        }
        throw new Exception("There was problem creating a issuer");
    }

    /**
     * Updates a badge
     *
     * @param ldapEntryManager ldapEntryManager
     * @param issuers          Pass badge class object with updated properties to update it.
     * @return
     * @throws Exception
     */
    public static boolean updateIssuers(LdapEntryManager ldapEntryManager, Issuers issuers) throws Exception {
        if (OrganizationCommands.findOrganizationfromDN(ldapEntryManager, issuers.getOrganization()) != null && PersonCommands.findPersonfromDN(ldapEntryManager, issuers.getOwner()) != null) {
            String inum = issuers.getInum();
            issuers.setDn("inum=" + inum + ",ou=gluuIssuer,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            if (ldapEntryManager.contains(issuers.getDn(), Issuers.class, Filter.create("(inum=" + issuers.getInum() + ")"))) {
                MergeService.merge(issuers,ldapEntryManager.findEntries(issuers.getDn(), Issuers.class, Filter.create("(inum=" + issuers.getInum() + ")")).get(0));
                ldapEntryManager.merge(issuers);
                System.out.println("updated entry ");
                return true;
            } else {
                throw new NotFoundException("No such badge found");
            }
        } else {
            throw new NotFoundException("No matching organization found");
        }
    }

    /**
     * Retrieves a issuer by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param Inum             Inum of the badge that is to be retrieved.
     * @return
     */
    public static Issuers getIssuerByInum(LdapEntryManager ldapEntryManager, String Inum) {
        try {

            Issuers issuers = new Issuers();
            issuers.setInum(Inum);
            issuers.setDn("inum=" + Inum + ",ou=gluuIssuer,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            if (ldapEntryManager.contains(issuers.getDn(), Badges.class, Filter.create("(inum=" + issuers.getInum() + ")"))) {

                List<Issuers> issuersList = ldapEntryManager.findEntries(issuers.getDn(), Issuers.class, Filter.create("(inum=" + issuers.getInum() + ")"));

                if (issuersList.size() > 0)
                    return issuersList.get(0);
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
     * Deletes issuer by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param inum             inum of the badge to be deleted
     * @return
     */
    public static boolean deleteIssuerByInum(LdapEntryManager ldapEntryManager, String inum) {
        try {
            Issuers issuers = new Issuers();
            issuers.setDn("inum=" + inum + ",ou=gluuIssuer,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            issuers.setInum(inum);
            issuers.setInum(inum);
            if (ldapEntryManager.contains(issuers.getDn(), Issuers.class, Filter.create("(inum=" + issuers.getInum() + ")"))) {
                ldapEntryManager.remove(issuers);
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
     * Lists all the badge that are into the systeam
     *
     * @param ldapEntryManager ldapEntryManager
     * @return
     */
    public static List<Issuers> getAllIssuers(LdapEntryManager ldapEntryManager) {

        Issuers issuers = new Issuers();
        issuers.setDn("ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

        return (ldapEntryManager.findEntries(issuers.getDn(), Issuers.class, null));
    }


    /**
     * Find Issuer object from DN
     * @param ldapEntryManager
     * @param dn
     * @return
     */
    public static Issuers findIsuerfromDN(LdapEntryManager ldapEntryManager, String dn) {
        try {
            List<Issuers> issuers = ldapEntryManager.findEntries(dn, Issuers.class, null);
            if (issuers.size() > 0) {
                return issuers.get(0);
            } else {
                throw new NotFoundException("No issuer found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No issuer found");
        }
    }

}
