package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.jboss.resteasy.spi.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;
import org.xdi.oxd.badgemanager.model.BadgeClassResponse;
import org.xdi.oxd.badgemanager.model.BadgeResponse;
import org.xdi.oxd.badgemanager.model.BadgeVerification;
import org.xdi.oxd.badgemanager.model.Recipient;
import org.xdi.oxd.badgemanager.util.Utils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

/**
 * Created by Arvind Tomar on 10/7/16.
 */
public class BadgeCommands {

    private static final Logger logger = LoggerFactory.getLogger(BadgeCommands.class);

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
        logger.info("LDAP started making badge assertion entry");
        if (!(ldapEntryManager.contains("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", Badges.class, Filter.create("(inum=" + badges.getInum() + ")")))) {
            if (!ldapEntryManager.contains("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(&(gluuBadgeClassInum=" + badges.getBadgeClassInum() + ")(gluuRecipientIdentity=" + badges.getRecipientIdentity() + "))"))) {
                ldapEntryManager.persist(badges);
                logger.info("LDAP completed making badge assertion entry");
                logger.info("new badge entry");
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
     * @param badges           badge class object with updated properties.
     * @return
     * @throws Exception
     */
    public static boolean updateBadge(Badges badges) throws Exception {
        if (LDAPService.isConnected()) {
            logger.info("LDAP connected in updateBadge() in BadgeCommands");

            String inum = badges.getInum();
            badges.setDn("inum=" + inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            logger.info("LDAP started updating badge");
            if (LDAPService.ldapEntryManager.contains(badges.getDn(), Badges.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                MergeService.merge(badges, LDAPService.ldapEntryManager.findEntries(badges.getDn(), Badges.class, Filter.create("(inum=" + badges.getInum() + ")")).get(0));
                LDAPService.ldapEntryManager.merge(badges);
                logger.info("LDAP completed updating badge");
                logger.info("updated badge entry ");
                return true;
            } else {
                logger.info("No such badge found");
                return false;
            }
        } else {
            logger.error("Error in connecting database in updateBadge() in BadgeCommands");
            return false;
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
     * Retrieves a badge by badge class Inum.
     *
     * @param Inum Inum of the badge class.
     * @return
     */
    public static Badges getBadgeByBadgeClassInum(String Inum) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeByBadgeClassInum() in BadgeCommands");

                Badges badge = new Badges();
                badge.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving badge");
                if (LDAPService.ldapEntryManager.contains(badge.getDn(), Badges.class, Filter.create("(gluuBadgeClassInum=" + Inum + ")"))) {
                    List<Badges> badges = LDAPService.ldapEntryManager.findEntries(badge.getDn(), Badges.class, Filter.create("(gluuBadgeClassInum=" + Inum + ")"));
                    logger.info("LDAP completed retrieving badge");
                    if (badges.size() > 0)
                        return badges.get(0);
                    else
                        return null;
                } else {
                    return null;
                }
            } else {
                logger.error("Error in connecting database in getBadgeByBadgeClassInum() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge in LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param Inum             Inum of the badge that is to be retrieved.
     * @param utils
     * @param request          @return
     */
    public static BadgeResponse getBadgeResponseByInum(LdapEntryManager ldapEntryManager, String Inum, Utils utils, HttpServletRequest request) {
        try {

            Badges objBadges = new Badges();
            objBadges.setInum(Inum);
            objBadges.setDn("inum=" + Inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            if (ldapEntryManager.contains(objBadges.getDn(), Badges.class, Filter.create("(inum=" + objBadges.getInum() + ")"))) {
                List<Badges> badges = ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(inum=" + objBadges.getInum() + ")"));

                if (badges.size() > 0) {
                    objBadges = badges.get(0);
                    return GetBadgeResponse(ldapEntryManager, objBadges, utils, request);
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
     * @param utils
     * @param request
     * @return
     */
    public static BadgeResponse getBadgeResponseById(LdapEntryManager ldapEntryManager, String id, Utils utils, HttpServletRequest request) {
        try {

            Badges objBadges = new Badges();
            objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            logger.info("LDAP started retrieving badge");
            List<Badges> lstBadges = ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + "))"));
            logger.info("LDAP completed retrieving badge");
            if (lstBadges.size() > 0) {
                return GetBadgeResponse(ldapEntryManager, lstBadges.get(0), utils, request);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge by Id and key
     *
     * @param ldapEntryManager ldapEntryManager
     * @param id               GUID of the badge
     * @param key              key of the badge that is to be retrieved.
     * @param utils
     * @param request
     * @return
     */
    public static BadgeResponse getBadgeResponseByIdAndKey(LdapEntryManager ldapEntryManager, String id, String key, Utils utils, HttpServletRequest request) {
        try {

            Badges objBadges = new Badges();
            objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            logger.info("LDAP started retrieving badge");
            List<Badges> lstBadges = ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + ")(gluuBadgeAssertionKey=" + key + "))"));
            logger.info("LDAP completed retrieving badge");
            if (lstBadges.size() > 0) {
                return GetBadgeResponse(ldapEntryManager, lstBadges.get(0), utils, request);
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge in LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge by Id
     *
     * @param ldapEntryManager ldapEntryManager
     * @param id               GUID of the badge
     * @param utils
     * @param request
     * @return
     */
    public static Badges getBadgeById(LdapEntryManager ldapEntryManager, String id, Utils utils, HttpServletRequest request) {
        try {

            Badges objBadges = new Badges();
            objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            List<Badges> lstBadges = ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + "))"));

            if (lstBadges.size() > 0) {
                return lstBadges.get(0);
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
            logger.info("LDAP started deleting badge");
            if (ldapEntryManager.contains(badges.getDn(), Badges.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                ldapEntryManager.remove(badges);
                logger.info("LDAP completed deleting badge");
                logger.info("Deleted badge entry ");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in deleting badge in LDAP: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes badge by badge class Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param inum             inum of the badge class
     * @return
     */
    public static boolean deleteBadgeByBadgeClassInum(LdapEntryManager ldapEntryManager, String inum) {
        try {
            Badges badges = new Badges();
            badges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            logger.info("LDAP started deleting badge");
            if (ldapEntryManager.contains(badges.getDn(), Badges.class, Filter.create("(gluuBadgeClassInum=" + inum + ")"))) {
                Badges badge = BadgeCommands.getBadgeByBadgeClassInum(inum);
                if (badge != null && badge.getInum() != null) {
                    if (BadgeCommands.deleteBadgeByInum(ldapEntryManager, badge.getInum())) {
                        logger.info("LDAP completed deleting badge");
                        logger.info("Badge entry deleted successfully");
                        return true;
                    } else {
                        logger.info("Badge entry not deleted");
                        return false;
                    }
                } else {
                    logger.info("Badge entry not found");
                    return true;
                }
            } else {
                logger.info("Badge entry not found");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in deleting badge in LDAP");
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
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No badge found");
        }
    }

    private static BadgeResponse GetBadgeResponse(LdapEntryManager ldapEntryManager, Badges objBadges, Utils utils, HttpServletRequest request) {
        try {
            BadgeResponse objBadge = new BadgeResponse();
            objBadge.setType(objBadges.getType());
            objBadge.setId(objBadges.getId());
            objBadge.setContext(objBadges.getContext());
//            if (objBadges.getImage() != null) {
//                objBadge.setImage(utils.getBaseURL(request) + File.separator + "images" + File.separator + objBadges.getImage());
//            } else {
//                objBadge.setImage("");
//            }

            Recipient recipient = new Recipient();
            recipient.setType(objBadges.getRecipientType());
            recipient.setIdentity(objBadges.getRecipientIdentity());
            objBadge.setRecipient(recipient);

            objBadge.setIssuedOn(objBadges.getIssuedOn().toString());
            objBadge.setExpires("");

            if (objBadges.getBadgePrivacy().equalsIgnoreCase("private")) {
                objBadge.setExpires(objBadges.getExpires().toString());
            }

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