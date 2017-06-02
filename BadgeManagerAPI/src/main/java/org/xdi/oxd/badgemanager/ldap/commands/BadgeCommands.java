package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.BadgeClass;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;
import org.xdi.oxd.badgemanager.model.*;
import org.xdi.oxd.badgemanager.util.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arvind Tomar on 10/7/16.
 */
public class BadgeCommands {

    private static final Logger logger = LoggerFactory.getLogger(BadgeCommands.class);

    /**
     * Creates new badge that belongs to an organization
     *
     * @param badges Object of the badge that is to be created
     * @return
     * @throws Exception
     */
    public static Badges createBadge(Badges badges) throws Exception {

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in createBadge() in BadgeCommands");
                String inum = InumService.getInum(InumService.badgePrefix);
                badges.setDn("inum=" + inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                badges.setInum(inum);
                logger.info("LDAP started making badge assertion entry");
                if (!(LDAPService.ldapEntryManager.contains("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", Badges.class, Filter.create("(inum=" + badges.getInum() + ")")))) {
                    if (!LDAPService.ldapEntryManager.contains("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(&(gluuBadgeClassInum=" + badges.getBadgeClassInum() + ")(gluuRecipientIdentity=" + badges.getRecipientIdentity() + "))"))) {
                        LDAPService.ldapEntryManager.persist(badges);
                        logger.info("LDAP completed making badge assertion entry");
                        logger.info("new badge entry");
                        return badges;
                    } else {
                        throw new Exception("Badge already exists");
                    }
                } else {
                    createBadge(badges);
                }
            } else {
                logger.error("Error in connecting database in createBadge() in BadgeCommands");
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception creating badge assertion entry in createBadge() in BadgeCommands:" + ex.getMessage());
            return null;
        }

        throw new Error("There was problem creating a badge");
    }

    /**
     * Updates a badge
     *
     * @param badges badge class object with updated properties.
     * @return
     * @throws Exception
     */
    public static boolean updateBadge(Badges badges) throws Exception {
        try {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception updating badge assertion entry in updateBadge() in BadgeCommands:" + ex.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a badge by Inum
     *
     * @param Inum Inum of the badge that is to be retrieved.
     * @return
     */
    public static Badges getBadgeByInum(String Inum) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeByInum() in BadgeCommands");
                Badges badge = new Badges();
                badge.setInum(Inum);
                badge.setDn("inum=" + Inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                if (LDAPService.ldapEntryManager.contains(badge.getDn(), Badges.class, Filter.create("(inum=" + badge.getInum() + ")"))) {
                    List<Badges> badges = LDAPService.ldapEntryManager.findEntries(badge.getDn(), Badges.class, Filter.create("(inum=" + badge.getInum() + ")"));
                    if (badges.size() > 0)
                        return badges.get(0);
                    else
                        return null;
                } else {
                    return null;
                }
            } else {
                logger.error("Error in connecting database in getBadgeByInum() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in retrieving badge assertion entry in getBadgeByInum() in BadgeCommands:" + e.getMessage());
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
     * @param Inum Inum of the badge that is to be retrieved.
     */
    public static BadgeResponse getBadgeResponseByInum(String Inum) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeResponseByInum() in BadgeCommands");
                Badges objBadges = new Badges();
                objBadges.setInum(Inum);
                objBadges.setDn("inum=" + Inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                if (LDAPService.ldapEntryManager.contains(objBadges.getDn(), Badges.class, Filter.create("(inum=" + objBadges.getInum() + ")"))) {
                    List<Badges> badges = LDAPService.ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(inum=" + objBadges.getInum() + ")"));

                    if (badges.size() > 0) {
                        objBadges = badges.get(0);
                        return GetBadgeResponse(objBadges);
                    } else
                        return null;
                } else {
                    return null;
                }
            } else {
                logger.error("Error in connecting database in getBadgeResponseByInum() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in retrieving badge in getBadgeResponseByInum() in BadgeCommands");
            return null;
        }
    }

    /**
     * Retrieves a badge by Id
     *
     * @param id GUID of the badge
     * @return
     */
    public static BadgeResponse getBadgeResponseById(String id) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeResponseById() in BadgeCommands");
                Badges objBadges = new Badges();
                objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving badge");
                List<Badges> lstBadges = LDAPService.ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + "))"));
                logger.info("LDAP completed retrieving badge");
                if (lstBadges.size() > 0) {
                    revokeBadgeAccess(lstBadges.get(0).getBadgeClassInum());
                    return GetBadgeResponse(lstBadges.get(0));
                } else
                    return null;
            } else {
                logger.error("Error in connecting database in getBadgeResponseById() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge by Id
     *
     * @param id GUID of the badge
     * @return
     */
    public static BadgeResponse getBadgeResponseByIdNew(String id) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeResponseByIdNew() in BadgeCommands");
                Badges objBadges = new Badges();
                objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving badge");
                List<Badges> lstBadges = LDAPService.ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + "))"));
                logger.info("LDAP completed retrieving badge");
                if (lstBadges.size() > 0) {
                    return GetBadgeResponse(lstBadges.get(0));
                } else
                    return null;
            } else {
                logger.error("Error in connecting database in getBadgeResponseByIdNew() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge by Id and key
     *
     * @param id  GUID of the badge
     * @param key key of the badge that is to be retrieved.
     * @return
     */
    public static BadgeResponse getBadgeResponseByIdAndKey(String id, String key) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeResponseByIdAndKey() in BadgeCommands");
                Badges objBadges = new Badges();
                objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving badge");
                List<Badges> lstBadges = LDAPService.ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + ")(gluuBadgeAssertionKey=" + key + "))"));
                logger.info("LDAP completed retrieving badge");
                if (lstBadges.size() > 0) {
                    revokeBadgeAccess(lstBadges.get(0).getBadgeClassInum());
                    return GetBadgeResponse(lstBadges.get(0));
                } else
                    return null;
            } else {
                logger.error("Error in connecting database in getBadgeResponseByIdAndKey() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge in LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge by Id and key
     *
     * @param id  GUID of the badge
     * @param key key of the badge that is to be retrieved.
     * @return
     */
    public static Badges getBadgeByIdAndKey(String id, String key) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeResponseByIdAndKey() in BadgeCommands");
                Badges objBadges = new Badges();
                objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving badge");
                List<Badges> lstBadges = LDAPService.ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + ")(gluuBadgeAssertionKey=" + key + "))"));
                logger.info("LDAP completed retrieving badge");
                if (lstBadges.size() > 0) {
                    return lstBadges.get(0);
                } else
                    return null;
            } else {
                logger.error("Error in connecting database in getBadgeResponseByIdAndKey() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge in LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge by Id
     *
     * @param id GUID of the badge
     * @return
     */
    public static Badges getBadgeById(String id) {
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeById() in BadgeCommands");
                Badges objBadges = new Badges();
                objBadges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                List<Badges> lstBadges = LDAPService.ldapEntryManager.findEntries(objBadges.getDn(), Badges.class, Filter.create("(&(gluuBadgeAssertionId=" + id + "))"));

                if (lstBadges.size() > 0) {
                    return lstBadges.get(0);
                } else
                    return null;
            } else {
                logger.error("Error in connecting database in getBadgeById() in BadgeCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge in LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes badge by Inum
     *
     * @param inum inum of the badge to be deleted
     * @return
     */
    public static boolean deleteBadgeByInum(String inum) {
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in deleteBadgeByInum() in BadgeCommands");
                Badges badges = new Badges();
                badges.setDn("inum=" + inum + ",ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                badges.setInum(inum);
                logger.info("LDAP started deleting badge");
                if (LDAPService.ldapEntryManager.contains(badges.getDn(), Badges.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                    LDAPService.ldapEntryManager.remove(badges);
                    logger.info("LDAP completed deleting badge");
                    logger.info("Deleted badge entry ");
                    return true;
                } else {
                    return false;
                }
            } else {
                logger.error("Error in connecting database in deleteBadgeByInum() in BadgeCommands");
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
     * @param inum inum of the badge class
     * @return
     */
    public static boolean deleteBadgeByBadgeClassInum(String inum) {
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in deleteBadgeByBadgeClassInum() in BadgeCommands");
                Badges badges = new Badges();
                badges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started deleting badge");
                if (LDAPService.ldapEntryManager.contains(badges.getDn(), Badges.class, Filter.create("(gluuBadgeClassInum=" + inum + ")"))) {
                    Badges badge = BadgeCommands.getBadgeByBadgeClassInum(inum);
                    if (badge != null && badge.getInum() != null) {
                        if (BadgeCommands.deleteBadgeByInum(badge.getInum())) {
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
            } else {
                logger.error("Error in connecting database in deleteBadgeByBadgeClassInum() in BadgeCommands");
                return false;
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
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getAllBadges() in BadgeCommands");
                Badges badges = new Badges();
                badges.setDn("ou=badgeAssertions,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                return (ldapEntryManager.findEntries(badges.getDn(), Badges.class, null));
            } else {
                logger.error("Error in connecting database in getAllBadges() in BadgeCommands");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in retrieving badges in LDAP:" + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static BadgeResponse GetBadgeResponse(Badges objBadges) {
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

            if (objBadges.getBadgePrivacy().equalsIgnoreCase("private") && objBadges.getExpires() != null) {
                objBadge.setExpires(objBadges.getExpires().toString());
            }

            BadgeVerification verification = new BadgeVerification();
            verification.setType(objBadges.getVerificationType());
            objBadge.setVerification(verification);

            BadgeClassResponse badge = BadgeClassesCommands.getBadgeClassResponseByInum(objBadges.getBadgeClassInum());
            if (badge != null) {
                objBadge.setBadge(badge);
            }

            return objBadge;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static boolean revokeBadgeAccess(String inum) {
        try {
            BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByInum(inum);
            if (badgeClass != null && badgeClass.getInum() != null) {
                BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestByInum(badgeClass.getBadgeRequestInum());
                if (badgeRequests != null && badgeRequests.getInum() != null) {
                    badgeRequests.setFidesAccess(false);
                    if (BadgeRequestCommands.updateBadgeRequest(badgeRequests)) {
                        logger.info("Access revoked successfully");
                        return true;
                    } else {
                        logger.info("Access revoke failed");
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in revoking badge access: " + e.getMessage());
            return false;
        }
    }
}