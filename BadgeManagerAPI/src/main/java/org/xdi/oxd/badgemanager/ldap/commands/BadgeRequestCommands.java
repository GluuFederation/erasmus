package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.BadgeClass;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;
import org.xdi.oxd.badgemanager.model.CreateBadgeResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arvind Tomar on 14/10/16.
 */
public class BadgeRequestCommands {

    private static final Logger logger = LoggerFactory.getLogger(BadgeRequestCommands.class);

    /**
     * Crete new badge request according to participant and template badge
     *
     * @param badgeRequest pass participant and template badge
     * @return
     * @throws Exception
     */
    public static BadgeRequests createBadgeRequest(BadgeRequests badgeRequest) throws Exception {

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in createBadgeRequest() in BadgeRequestCommands");

                String inum = InumService.getInum(InumService.badgeRequestPrefix);
                badgeRequest.setDn("inum=" + inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                badgeRequest.setInum(inum);
                badgeRequest.setStatus("Pending");
                badgeRequest.setCreationDate(new Date());

                if (!(LDAPService.ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")")))) {
                    if (!LDAPService.ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + badgeRequest.getGluuBadgeRequester() + ")(masterBadgeId=" + badgeRequest.getTemplateBadgeId() + "))"))) {
                        LDAPService.ldapEntryManager.persist(badgeRequest);
                        logger.info("new badge request created");
                        return badgeRequest;
                    } else {
                        throw new Exception("You have already requested for same badge");
                    }
                } else {
                    createBadgeRequest(badgeRequest);
                }

                throw new Exception("There was problem creating a badge request");

            } else {
                logger.error("Error in connecting database in createBadgeRequest() in BadgeRequestCommands");
                return null;
            }
        } catch (Exception ex) {
            logger.error("Exception in creating badge request in createBadgeRequest() in BadgeRequestCommands:" + ex.getMessage());
            return null;
        }
    }

    /**
     * Crete new badge request according to participant and template badge
     *
     * @param badgeRequest pass participant and template badge
     * @return
     * @throws Exception
     */
    public static CreateBadgeResponse createBadgeRequestNew(BadgeRequests badgeRequest) throws Exception {

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in createBadgeRequestNew() in BadgeRequestCommands");
                String inum = InumService.getInum(InumService.badgeRequestPrefix);
                badgeRequest.setDn("inum=" + inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                badgeRequest.setInum(inum);
                badgeRequest.setStatus("Pending");
                badgeRequest.setCreationDate(new Date());
                logger.info("Started Badge request entry in LDAP");
                if (!(LDAPService.ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")")))) {
                    if (!LDAPService.ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + badgeRequest.getGluuBadgeRequester() + ")(gluuTemplateBadgeId=" + badgeRequest.getTemplateBadgeId() + ")(gluuParticipant=" + badgeRequest.getParticipant() + "))"))) {
                        LDAPService.ldapEntryManager.persist(badgeRequest);
                        logger.info("Completed Badge request entry in LDAP");
                        logger.info("new badge request created");
                        CreateBadgeResponse objResponse = new CreateBadgeResponse();
                        objResponse.setInum(badgeRequest.getInum());
                        objResponse.setParticipant(badgeRequest.getParticipant());
                        objResponse.setTemplateBadgeId(badgeRequest.getTemplateBadgeId());
                        objResponse.setTemplateBadgeTitle(badgeRequest.getTemplateBadgeTitle());
                        objResponse.setStatus(badgeRequest.getStatus());
                        objResponse.setRequesterEmail(badgeRequest.getGluuBadgeRequester());
                        return objResponse;
                    } else {
                        return null;
                    }
                } else {
                    createBadgeRequestNew(badgeRequest);
                }
            } else {
                logger.error("Error in connecting database in createBadgeRequestNew() in BadgeRequestCommands");
                return null;
            }
        } catch (Exception ex) {
            logger.error("Exception in creating badge request in createBadgeRequestNew() in BadgeRequestCommands:" + ex.getMessage());
            return null;
        }
        throw new Exception("There was problem creating a badge request");
    }

    /**
     * Get pending badge request according to participant
     *
     * @param id     pass participant id
     * @param status pass status
     * @return
     */
    public static List<BadgeRequests> getPendingBadgeRequests(String id, String status) throws Exception {

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getPendingBadgeRequests() in BadgeRequestCommands");

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                return (LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuParticipant=" + id + ")(gluuStatus=" + status + "))")));
            } else {
                logger.error("Error in connecting database in getPendingBadgeRequests() in BadgeRequestCommands");
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            logger.error("Exception in creating badge request in getPendingBadgeRequests() in BadgeRequestCommands:" + ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get pending badge request according to participant
     *
     * @param id     pass participant id
     * @param status pass status
     * @return
     */
    public static List<CreateBadgeResponse> getBadgeRequestsByParticipant(String id, String status) throws Exception {

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getPendingBadgeRequestsNew() in BadgeRequestCommands");
                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP Started retrieving " + status + " badge requests for participant");
                List<BadgeRequests> lstBadgeRequests = LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuParticipant=" + id + ")(gluuStatus=" + status + ")(gluuFIDESAccess=" + true + "))"));
                logger.info("LDAP Completed retrieving " + status + " badge requests for participant and found badge requests: " + lstBadgeRequests.size());
                return GetResponseList(lstBadgeRequests);
            } else {
                logger.error("Error in connecting database in getPendingBadgeRequestsNew() in BadgeRequestCommands");
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            logger.error("Exception in connecting database in getPendingBadgeRequestsNew() in BadgeRequestCommands:" + ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Deletes badge request by Inum
     *
     * @param inum inum of the badge request to be deleted
     * @return
     */
    public static boolean deleteBadgeRequestByInum(String inum) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in deleteBadgeRequestByInum() in BadgeRequestCommands");

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("inum=" + inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                badgeRequest.setInum(inum);
                if (LDAPService.ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")"))) {
                    if (BadgeClassesCommands.deleteBadgeClassByBadgeRequestInum(inum)) {
                        LDAPService.ldapEntryManager.remove(badgeRequest);
                        logger.info("Badge request entry deleted successfully");
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            } else {
                logger.error("Error in connecting database in deleteBadgeRequestByInum() in BadgeRequestCommands");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in deleting badge request in deleteBadgeRequestByInum() in BadgeRequestCommands:" + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes badge request by Inum
     *
     * @param inum  inum of the badge request to be deleted
     * @param email email of the badge requester
     * @return
     */
    public static boolean deleteUserBadgeRequestByInum(String inum, String email) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in deleteUserBadgeRequestByInum() in BadgeRequestCommands");
                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("inum=" + inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("Started remove badge request in LDAP");
                if (LDAPService.ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(inum=" + inum + "))"))) {
                    if (BadgeClassesCommands.deleteBadgeClassByBadgeRequestInum(inum)) {
                        LDAPService.ldapEntryManager.remove(badgeRequest);
                        logger.info("Completed remove badge request in LDAP");
                        logger.info("Badge request entry deleted successfully");
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            } else {
                logger.error("Error in connecting database in deleteUserBadgeRequestByInum() in BadgeRequestCommands");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in remove badge request in LDAP: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update badge request by Inum
     *
     * @param badgeRequest badge request to be updated
     * @return
     */
    public static boolean updateBadgeRequest(BadgeRequests badgeRequest) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in updateBadgeRequest() in BadgeRequestCommands");
                badgeRequest.setUpdatedAt(new Date());
                badgeRequest.setDn("inum=" + badgeRequest.getInum() + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("Started Update badge request in LDAP");
                if (LDAPService.ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")"))) {
                    MergeService.merge(badgeRequest, LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")")).get(0));
                    LDAPService.ldapEntryManager.merge(badgeRequest);
                    logger.info("Completed Update badge request in LDAP");
                    logger.info("Badge request updated successfully");
                    return true;
                } else {
                    logger.info("Badge request not found");
                    return false;
                }
            } else {
                logger.error("Error in connecting database in updateBadgeRequest() in BadgeRequestCommands");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in connecting database in updateBadgeRequest() in BadgeRequestCommands:" + e.getMessage());
            return false;
        }
    }

    /**
     * Get approved badge request for user
     *
     * @param email  user email
     * @param status status
     * @return
     */
    public static List<BadgeRequests> getBadgeRequestsByStatus(String email, String status) throws Exception {
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeRequestsByStatus() in BadgeRequestCommands");

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                return (LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(gluuStatus=" + status + "))")));
            } else {
                logger.error("Error in connecting database in getBadgeRequestsByStatus() in BadgeRequestCommands");
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in retrieving badge requests in getBadgeRequestsByStatus() in BadgeRequestCommands:" + ex.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get approved badge request for user
     *
     * @param email  user email
     * @param status status
     * @return
     */
    public static List<CreateBadgeResponse> getBadgeRequestsByStatusNew(String email, String status) throws Exception {
        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeRequestsByStatusNew() in BadgeRequestCommands");
                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP Search for " + status + " badges for user " + email);
                List<BadgeRequests> lstBadgeRequests = (LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(gluuStatus=" + status + "))")));
                logger.info("Search complete: " + status + " badges found for user " + email + " from LDAP are: " + lstBadgeRequests.size());
                return GetResponseList(lstBadgeRequests);
            } else {
                logger.error("Error in connecting database in getBadgeRequestsByStatusNew() in BadgeRequestCommands");
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in retrieving badge requests in getBadgeRequestsByStatusNew() in BadgeRequestCommands");
            return new ArrayList<>();
        }
    }

    /**
     * Get all badge requests for user
     *
     * @param email user email
     * @return
     */
    public static List<CreateBadgeResponse> getAllBadgeRequestsByRequester(String email) throws Exception {

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getAllBadgeRequestsByRequester() in BadgeRequestCommands");
                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                return GetResponseList((LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + "))"))));
            } else {
                logger.error("Error in connecting database in getAllBadgeRequestsByRequester() in BadgeRequestCommands");
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in retrieving badge requests in getAllBadgeRequestsByRequester() in BadgeRequestCommands");
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a badge request by Inum
     *
     * @param Inum Inum of the badge request that is to be retrieved.
     * @return
     */
    public static BadgeRequests getBadgeRequestByInum(String Inum) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeRequestByInum() in BadgeRequestCommands");

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
                logger.info("LDAP started retrieved badge request");
                if (LDAPService.ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + Inum + ")"))) {
                    List<BadgeRequests> badgeRequests = LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + Inum + ")"));
                    logger.info("LDAP completed retrieving badge request");
                    if (badgeRequests.size() > 0)
                        return badgeRequests.get(0);
                    else
                        return null;
                } else {
                    return null;
                }
            } else {
                logger.error("Error in connecting database in getBadgeRequestByInum() in BadgeRequestCommands");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in retrieving badge request in LDAP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves a badge request by Inum
     *
     * @param Inum  Inum of the badge request that is to be retrieved.
     * @param email Email of the badge requester.
     * @return
     */
    public static BadgeRequests getBadgeRequestForUserByInum(String Inum, String email) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeRequestForUserByInum() in BadgeRequestCommands");

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setDn("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                if (LDAPService.ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(inum=" + Inum + "))"))) {
                    List<BadgeRequests> badgeRequests = LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(inum=" + Inum + "))"));
                    if (badgeRequests.size() > 0)
                        return badgeRequests.get(0);
                    else
                        return null;
                } else {
                    return null;
                }
            } else {
                logger.error("Error in connecting database in getBadgeRequestForUserByInum() in BadgeRequestCommands");
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception in getBadgeRequestForUserByInum() in BadgeRequestCommands:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a badge request by Inum
     *
     * @param email Email of the badge requester.
     * @param title Title of the badge.
     * @return
     */
    public static BadgeRequests getUserBadgeRequestByTitle(String email, String title) {
        try {

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getUserBadgeRequestByTitle() in BadgeRequestCommands");

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setGluuBadgeRequester(email);
                badgeRequest.setDn("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

                List<BadgeRequests> badgeRequests = LDAPService.ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(gluuTemplateBadgeTitle=" + title + "))"));
                if (badgeRequests.size() > 0)
                    return badgeRequests.get(0);
                else
                    return null;
            } else {
                logger.error("Error in connecting database in getUserBadgeRequestByTitle() in BadgeRequestCommands");
                return null;
            }
        } catch (Exception e) {
            logger.error("Exception in getUserBadgeRequestByTitle() in BadgeRequestCommands:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static List<CreateBadgeResponse> GetResponseList(List<BadgeRequests> lstBadgeRequests) {
        List<CreateBadgeResponse> lstBadgeResponse = new ArrayList<>();
        for (BadgeRequests obj : lstBadgeRequests) {
            CreateBadgeResponse objResponse = new CreateBadgeResponse();
            objResponse.setInum(obj.getInum());
            objResponse.setParticipant(obj.getParticipant());
            objResponse.setTemplateBadgeId(obj.getTemplateBadgeId());
            objResponse.setTemplateBadgeTitle(obj.getTemplateBadgeTitle());
            objResponse.setStatus(obj.getStatus());
            objResponse.setRequesterEmail(obj.getGluuBadgeRequester());

            objResponse.setPrivacy("");
            try {
                BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestByInum(obj.getInum());
                if (badgeRequests != null && badgeRequests.getInum() != null && badgeRequests.getStatus().equalsIgnoreCase("Approved")) {
                    BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(badgeRequests.getInum());
                    if (badgeClass != null && badgeClass.getInum() != null) {
                        Badges badges = BadgeCommands.getBadgeByBadgeClassInum(badgeClass.getInum());
                        if (badges != null && badges.getInum() != null) {
                            objResponse.setPrivacy(badges.getBadgePrivacy());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            lstBadgeResponse.add(objResponse);
        }
        return lstBadgeResponse;
    }
}