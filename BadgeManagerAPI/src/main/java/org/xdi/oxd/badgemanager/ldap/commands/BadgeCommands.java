package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.jboss.resteasy.spi.NotFoundException;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;
import org.xdi.oxd.badgemanager.model.BadgeClassResponse;
import org.xdi.oxd.badgemanager.model.BadgeResponse;
import org.xdi.oxd.badgemanager.model.BadgeVerification;
import org.xdi.oxd.badgemanager.model.Recipient;

import java.util.List;

/**
 * Created by Arvind Tomar on 10/7/16.
 */
public class BadgeCommands {

    /**
     * Creates new badge that belongs to an organization
     *
     * @param ldapEntryManager ldapEntryManager
     * @param badges           Object of the badge that is to be created
     * @return
     * @throws Exception
     */
    public static Badges createBadge(LdapEntryManager ldapEntryManager, Badges badges) throws Exception {

        String inum = InumService.getInum(InumService.badgePrefix);
        badges.setDn("inum=" + inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
        badges.setInum(inum);

        if (!(ldapEntryManager.contains("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", Badges.class, Filter.create("(inum=" + badges.getInum() + ")")))) {
            if (!ldapEntryManager.contains("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(&(gluuBadgeClassInum=" + badges.getBadgeClassInum() + ")(gluuRecipientIdentity=" + badges.getRecipientIdentity() + "))"))) {
                ldapEntryManager.persist(badges);
                System.out.println("new badge entry");
                return badges;
            } else {
                throw new Exception("Badge already exists");
            }
        } else {
            createBadge(ldapEntryManager, badges);
        }
        throw new Error("There was problem creating a badge");
    }

    /**
     * Updates a badge
     *
     * @param ldapEntryManager ldapEntryManager
     * @param badges           Pass badge class object with updated properties to update it.
     * @return
     * @throws Exception
     */
    public static boolean updateBadge(LdapEntryManager ldapEntryManager, Badges badges) throws Exception {
        String inum = badges.getInum();
        badges.setDn("inum=" + inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
        if (ldapEntryManager.contains(badges.getDn(), Badges.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
            MergeService.merge(badges, ldapEntryManager.findEntries(badges.getDn(), Badges.class, Filter.create("(inum=" + badges.getInum() + ")")).get(0));
            ldapEntryManager.merge(badges);
            System.out.println("updated entry ");
            return true;
        } else {
            throw new NotFoundException("No such badge found");
        }
    }

    /**
     * Retrieves a badge by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param Inum             Inum of the badge that is to be retrieved.
     * @return
     */
    public static Badges getBadgeByInum(LdapEntryManager ldapEntryManager, String Inum) {
        try {

            Badges badge = new Badges();
            badge.setInum(Inum);
            badge.setDn("inum=" + Inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            if (ldapEntryManager.contains(badge.getDn(), Badges.class, Filter.create("(inum=" + badge.getInum() + ")"))) {

                List<Badges> badges = ldapEntryManager.findEntries(badge.getDn(), Badges.class, Filter.create("(inum=" + badge.getInum() + ")"));

                if (badges.size() > 0)
                    return badges.get(0);
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
     * Retrieves a badge by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param Inum             Inum of the badge that is to be retrieved.
     * @return
     */
    public static BadgeResponse getBadgeResponseByInum(LdapEntryManager ldapEntryManager, String Inum) {
        try {

            Badges objBadges = new Badges();
            objBadges.setInum(Inum);
            objBadges.setDn("inum=" + Inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            if (ldapEntryManager.contains(objBadges.getDn(), Badges.class, Filter.create("(inum=" + objBadges.getInum() + ")"))) {
                List<Badges> badges = ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(inum=" + objBadges.getInum() + ")"));

                if (badges.size() > 0) {
                    objBadges = badges.get(0);
                    return GetBadgeResponse(ldapEntryManager, objBadges);
                } else
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
     * Retrieves a badge by Id
     *
     * @param ldapEntryManager ldapEntryManager
     * @param id               GUID of the badge
     * @param key              key of the badge that is to be retrieved.
     * @return
     */
    public static BadgeResponse getBadgeResponseById(LdapEntryManager ldapEntryManager, String id, String key) {
        try {

            Badges objBadges = new Badges();
            objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            List<Badges> lstBadges = ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuGUID=" + id + ")(gluuKey=" + key + "))"));

            if (lstBadges.size() > 0) {
                objBadges = lstBadges.get(0);
                return GetBadgeResponse(ldapEntryManager, objBadges);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes badge by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param inum             inum of the badge to be deleted
     * @return
     */
    public static boolean deleteBadgeByInum(LdapEntryManager ldapEntryManager, String inum) {
        try {
            Badges badges = new Badges();
            badges.setDn("inum=" + inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            badges.setInum(inum);
            if (ldapEntryManager.contains(badges.getDn(), Badges.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                ldapEntryManager.remove(badges);
                System.out.println("Deleted badge entry ");
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
     * Lists all the badge that are into the system
     *
     * @param ldapEntryManager ldapEntryManager
     * @return
     */
    public static List<Badges> getAllBadges(LdapEntryManager ldapEntryManager) {

        Badges badges = new Badges();
        badges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

        return (ldapEntryManager.findEntries(badges.getDn(), Badges.class, null));
    }

    public static Badges findBadgefromDN(LdapEntryManager ldapEntryManager, String dn) {
        try {
            List<Badges> badges = ldapEntryManager.findEntries(dn, Badges.class, null);
            if (badges.size() > 0) {
                return badges.get(0);
            } else {
                throw new NotFoundException("No badge found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No badge found");
        }
    }

    private static BadgeResponse GetBadgeResponse(LdapEntryManager ldapEntryManager, Badges objBadges) {
        try {
            BadgeResponse objBadge = new BadgeResponse();
            objBadge.setType(objBadges.getType());
            objBadge.setId(objBadges.getId());
            objBadge.setContext(objBadges.getContext());

            Recipient recipient = new Recipient();
            recipient.setType(objBadges.getRecipientType());
            recipient.setIdentity(objBadges.getRecipientIdentity());
            objBadge.setRecepient(recipient);

            objBadge.setIssuedOn(objBadges.getIssuedOn().toString());
            objBadge.setExpires(objBadges.getExpires().toString());

            BadgeVerification verification = new BadgeVerification();
            verification.setType(objBadges.getVerificationType());
            objBadge.setVerification(verification);

            BadgeClassResponse badge = BadgeClassesCommands.getBadgeClassResponseByInum(ldapEntryManager, objBadges.getBadgeClassInum());
            if (badge != null) {
                objBadge.setBadge(badge);
            }

            return objBadge;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}