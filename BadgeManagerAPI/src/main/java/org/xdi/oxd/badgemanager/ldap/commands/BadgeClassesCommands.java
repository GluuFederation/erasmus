package org.xdi.oxd.badgemanager.ldap.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.jboss.resteasy.spi.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.models.BadgeClass;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.model.BadgeClassResponse;
import org.xdi.oxd.badgemanager.model.Criteria;
import org.xdi.oxd.badgemanager.model.Issuer;
import org.xdi.oxd.badgemanager.model.Verification;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
public class BadgeClassesCommands {

    private static final Logger logger = LoggerFactory.getLogger(BadgeRequestCommands.class);

    /**
     * Assign a badge to a user which has been requested by him or her
     *
     * @param badges badge class object
     */
    public static BadgeClass createBadgeClass(BadgeClass badges) throws Exception {

        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in createBadgeClass() in BadgeClassCommands");

                String inum = InumService.getInum(InumService.badgeInstancePrefix);
                badges.setDn("inum=" + inum + ",ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                badges.setInum(inum);
                logger.info("LDAP started making badge class entry");
                if (!(LDAPService.ldapEntryManager.contains("ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeClass.class, Filter.create("(inum=" + badges.getInum() + ")")))) {
                    if (!LDAPService.ldapEntryManager.contains("ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeClass.class, Filter.create("(&(gluuBadgeRequestInum=" + badges.getBadgeRequestInum() + ")(gluuTemplateBadgeId=" + badges.getTemplateBadgeId() + "))"))) {
                        LDAPService.ldapEntryManager.persist(badges);
                        logger.info("LDAP completed making badge class entry");
                        logger.info("New badge class entry");
                        return badges;
                    } else {
                        throw new Exception("Badge class already exists");
                    }
                } else {
                    createBadgeClass(badges);
                }

            } else {
                logger.error("Error in connecting database in createBadgeClass() in BadgeClassCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in badge class entry in LDAP: " + e.getMessage());
            throw new Exception("There was error creating badge class");
        }
        throw new Exception("There was error in persist a badge class");
    }

    /**
     * Delete a badge class by Inum.
     *
     * @param inum Inum of the badge class that is to be deleted.
     * @return
     */
    public static boolean deleteBadgeClassByInum(String inum) {
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in deleteBadgeClassByInum() in BadgeClassCommands");
                BadgeClass badges = new BadgeClass();
                badges.setDn("inum=" + inum + ",ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                badges.setInum(inum);
                logger.info("Started remove badge class in LDAP");
                if (LDAPService.ldapEntryManager.contains(badges.getDn(), BadgeClass.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                    LDAPService.ldapEntryManager.remove(badges);
                    logger.info("Completed remove badge class in LDAP");
                    logger.info("Deleted badge class entry ");
                    return true;
                } else {
                    return false;
                }
            } else {
                logger.error("Error in connecting database in  deleteBadgeClassByInum() in BadgeClassCommands");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in remove badge class in LDAP: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a badge class by badge request inum.
     *
     * @param inum Inum of the badge request.
     * @return
     */
    public static boolean deleteBadgeClassByBadgeRequestInum(String inum) {
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in deleteBadgeClassByBadgeRequestInum() in BadgeClassCommands");
                BadgeClass badges = new BadgeClass();
                badges.setDn("ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("Started remove badge class in LDAP");
                if (LDAPService.ldapEntryManager.contains(badges.getDn(), BadgeClass.class, Filter.create("(gluuBadgeRequestInum=" + inum + ")"))) {
                    BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(inum);
                    if (badgeClass != null && badgeClass.getInum() != null) {
                        if (BadgeCommands.deleteBadgeByBadgeClassInum(badgeClass.getInum())) {
                            if (BadgeClassesCommands.deleteBadgeClassByInum(badgeClass.getInum())) {
                                logger.info("Completed remove badge class in LDAP");
                                logger.info("Badge class entry deleted successfully");
                                return true;
                            } else {
                                logger.info("Badge class entry not deleted");
                                return false;
                            }
                        } else {
                            logger.info("Badge entry not deleted");
                            return false;
                        }
                    } else {
                        logger.info("Badge class entry not found");
                        return true;
                    }
                } else {
                    logger.info("Badge class entry not found");
                    return true;
                }
            } else {
                logger.error("Error in connecting database in deleteBadgeClassByBadgeRequestInum() in BadgeClassCommands");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in remove badge class in LDAP: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reterived a badge class by Inum.
     *
     * @param Inum Inum of the badge class that is to be retrieved.
     * @return
     */
    public static BadgeClass getBadgeClassByInum(String Inum) throws Exception {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeClassByInum() in BadgeClassesCommands");

                BadgeClass badge = new BadgeClass();
                badge.setInum(Inum);
                badge.setDn("inum=" + Inum + ",ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                if (LDAPService.ldapEntryManager.contains(badge.getDn(), BadgeClass.class, Filter.create("(inum=" + badge.getInum() + ")"))) {
                    List<BadgeClass> badges = LDAPService.ldapEntryManager.findEntries(badge.getDn(), BadgeClass.class, Filter.create("(inum=" + badge.getInum() + ")"));
                    if (badges.size() > 0)
                        return badges.get(0);
                    else
                        return null;
                } else {
                    throw new NotFoundException("No such badge class found");
                }
            } else {
                logger.error("Error in connecting database in getBadgeClassByInum() in BadgeClassesCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No such badge class found");
        }
    }

    /**
     * Retrieved a badge class by badge request Inum.
     *
     * @param Inum Inum of the badge request.
     * @return
     */
    public static BadgeClass getBadgeClassByBadgeRequestInum(String Inum) throws Exception {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeClassByBadgeRequestInum() in BadgeClassesCommands");

                BadgeClass badge = new BadgeClass();
                badge.setDn("ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving badge class");
                if (LDAPService.ldapEntryManager.contains(badge.getDn(), BadgeClass.class, Filter.create("(gluuBadgeRequestInum=" + Inum + ")"))) {
                    List<BadgeClass> badges = LDAPService.ldapEntryManager.findEntries(badge.getDn(), BadgeClass.class, Filter.create("(gluuBadgeRequestInum=" + Inum + ")"));
                    logger.info("LDAP completed retrieving badge class");
                    if (badges.size() > 0)
                        return badges.get(0);
                    else
                        return null;
                } else {
                    return null;
                }
            } else {
                logger.error("Error in connecting database in getBadgeClassByBadgeRequestInum() in BadgeClassesCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in retrieving badge class in LDAP: " + e.getMessage());
            throw new NotFoundException("No such badge class found");
        }
    }

    /**
     * Retrieved a badge class by Id.
     *
     * @param id  GUID of the badge class
     * @param key key of the badge class that is to be retrieved.
     * @return
     */
    public static BadgeClassResponse getBadgeClassResponseById(String id, String key) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeClassResponseById() in BadgeClassCommands");
                BadgeClass badge = new BadgeClass();
                badge.setDn("ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP Started retrieving badge class");
                List<BadgeClass> badges = LDAPService.ldapEntryManager.findEntries(badge.getDn(), BadgeClass.class, Filter.create("(&(gluuBadgeClassId=" + id + ")(gluuBadgeClassKey=" + key + "))"));
                logger.info("LDAP Completed retrieving badge class");
                if (badges.size() > 0)
                    return GetBadgeClassResponse(badges.get(0));
                else
                    return null;
            } else {
                logger.error("Error in connecting LDAP in getBadgeClassResponseById() in BadgeClassCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception in retrieving badge class from LDAP: " + e.getMessage());
            throw new NotFoundException("No such badge instance found");
        }
    }

    /**
     * Reterived a badge class by Inum.
     *
     * @param Inum Inum of the badge class that is to be retrieved.
     * @return
     */
    public static BadgeClassResponse getBadgeClassResponseByInum(String Inum) throws Exception {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeClassResponseByInum() in BadgeClassCommands");
                BadgeClass badge = new BadgeClass();
                badge.setInum(Inum);
                badge.setDn("inum=" + Inum + ",ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieving badge class");
                if (LDAPService.ldapEntryManager.contains(badge.getDn(), BadgeClass.class, Filter.create("(inum=" + badge.getInum() + ")"))) {
                    List<BadgeClass> badges = LDAPService.ldapEntryManager.findEntries(badge.getDn(), BadgeClass.class, Filter.create("(inum=" + badge.getInum() + ")"));
                    logger.info("LDAP completed retrieving badge class");
                    if (badges.size() > 0) {
                        badge = badges.get(0);
                        return GetBadgeClassResponse(badge);
                    } else
                        return null;
                } else {
                    throw new NotFoundException("No such badge class found");
                }
            } else {
                logger.error("Error in connecting LDAP in getBadgeClassResponseByInum() in BadgeClassCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in retrieving badge class in LDAP: " + e.getMessage());
            throw new NotFoundException("No such badge class found");
        }
    }

    /**
     * Get all the badge class of the system
     *
     * @param ldapEntryManager ldapEntryManager
     * @return
     */
    public static List<BadgeClass> getAllBadgeClasses(LdapEntryManager ldapEntryManager) {

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getAllBadgeClasses() in BadgeClassCommands");
                BadgeClass badges = new BadgeClass();
                badges.setDn("ou=badgeClasses,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                return (ldapEntryManager.findEntries(badges.getDn(), BadgeClass.class, null));
            } else {
                logger.error("Error in connecting LDAP in getAllBadgeClasses() in BadgeClassCommands");
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in retrieving badge classes in LDAP: " + ex.getMessage());
            return new ArrayList<>();
        }
    }

    private static BadgeClassResponse GetBadgeClassResponse(BadgeClass badge) {
        try {
            BadgeClassResponse objBadgeClass = new BadgeClassResponse();
            objBadgeClass.setType(badge.getType());
            objBadgeClass.setId(badge.getId());
            objBadgeClass.setName(badge.getName());
            objBadgeClass.setDescription(badge.getDescription());
            objBadgeClass.setImage(badge.getImage());
            Criteria criteria = new Criteria();
            Issuer issuer = new Issuer();
            Verification verification = new Verification();

            final String uri = Global.API_ENDPOINT + Global.getTemplateBadgeById + "/" + badge.getTemplateBadgeId();

            DisableSSLCertificateCheckUtil.disableChecks();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + Global.Request_AccessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            HttpEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

            String result = response.getBody();

            JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();

            if (jObjResponse != null) {
                criteria.setNarrative(GsonService.getValueFromJson("narrative", jObjResponse));
            }

            objBadgeClass.setCriteria(criteria);

//            issuer
//            BadgeRequests objBadgeRequest = BadgeRequestCommands.getBadgeRequestByInum(LDAPService.ldapEntryManager, badge.getBadgeRequestInum());
//            if (objBadgeRequest != null && objBadgeRequest.getParticipant() != null) {
//                final String uriParticipant = Global.API_ENDPOINT + Global.participant + "/" + objBadgeRequest.getParticipant();
//                HttpEntity<String> responseParticipant = restTemplate.exchange(uriParticipant, HttpMethod.GET, request, String.class);
//
//                String resultParticipant = responseParticipant.getBody();
//
//                JsonObject jObjResponseParticipant = new JsonParser().parse(resultParticipant).getAsJsonObject();
//
//                issuer.setId(GsonService.getValueFromJson("id", jObjResponseParticipant));
//                issuer.setType(GsonService.getValueFromJson("type", jObjResponseParticipant));
//                issuer.setName(GsonService.getValueFromJson("name", jObjResponseParticipant));
//                issuer.setUrl("https://example.org");
//
//                String verificationJson = GsonService.getValueFromJson("verification", jObjResponseParticipant);
//                if (verificationJson != null && verificationJson.length() > 0) {
//                    JsonObject jObjVerification = new JsonParser().parse(verificationJson).getAsJsonObject();
//
//                    verification.setAllowedOrigins(GsonService.getValueFromJson("allowedOrigins", jObjVerification));
//                    verification.setType(GsonService.getValueFromJson("type", jObjVerification));
//
//                    issuer.setVerification(verification);
//                }
//
//                objBadgeClass.setIssuer(issuer);
//            }

            issuer.setId("https://erasmusdev.gluu.org");
            issuer.setType("Profile");
            issuer.setName("Erasmus");
            issuer.setUrl("https://erasmusdev.gluu.org");
            issuer.setEmail("erasmussupport@gluu.org");

            verification.setAllowedOrigins("https://erasmusdev.gluu.org");
            verification.setType("hosted");

            issuer.setVerification(verification);
            objBadgeClass.setIssuer(issuer);

            return objBadgeClass;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in retrieving badge class response: " + ex.getMessage());
            return null;
        }
    }
}