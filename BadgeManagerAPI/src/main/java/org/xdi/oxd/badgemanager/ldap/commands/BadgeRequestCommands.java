package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;
import org.xdi.oxd.badgemanager.model.CreateBadgeResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arvind Tomar on 14/10/16.
 */
public class BadgeRequestCommands {
    //Crete new badge request according to organization and badge

    /**
     * Crete new badge request according to participant and template badge
     *
     * @param ldapEntryManager ldapEntryManager
     * @param badgeRequest     pass participant and template badge
     * @return
     * @throws Exception
     */
    public static BadgeRequests createBadgeRequest(LdapEntryManager ldapEntryManager, BadgeRequests badgeRequest) throws Exception {

        String inum = InumService.getInum(InumService.badgeRequestPrefix);
        badgeRequest.setDn("inum=" + inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
        badgeRequest.setInum(inum);
        badgeRequest.setStatus("Pending");
        badgeRequest.setCreationDate(new Date());

        //static(need to remove once implemented)
//        return badgeRequest;

        //Dynamic
        if (!(ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")")))) {
            if (!ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + badgeRequest.getGluuBadgeRequester() + ")(masterBadgeId=" + badgeRequest.getTemplateBadgeId() + "))"))) {
                ldapEntryManager.persist(badgeRequest);
                System.out.println("new badge request created");
                return badgeRequest;
            } else {
                throw new Exception("You have already requested for same badge");
            }
        } else {
            createBadgeRequest(ldapEntryManager, badgeRequest);
        }

        throw new Exception("There was problem creating a badge request");
    }

    /**
     * Crete new badge request according to participant and template badge
     *
     * @param ldapEntryManager ldapEntryManager
     * @param badgeRequest     pass participant and template badge
     * @return
     * @throws Exception
     */
    public static CreateBadgeResponse createBadgeRequestNew(LdapEntryManager ldapEntryManager, BadgeRequests badgeRequest) throws Exception {

        String inum = InumService.getInum(InumService.badgeRequestPrefix);
        badgeRequest.setDn("inum=" + inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
        badgeRequest.setInum(inum);
        badgeRequest.setStatus("Pending");
        badgeRequest.setCreationDate(new Date());

        //static(need to remove once implemented)
//        return badgeRequest;

        //Dynamic
        if (!(ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")")))) {
            if (!ldapEntryManager.contains("ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu", BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + badgeRequest.getGluuBadgeRequester() + ")(masterBadgeId=" + badgeRequest.getTemplateBadgeId() + "))"))) {
                ldapEntryManager.persist(badgeRequest);
                System.out.println("new badge request created");
                CreateBadgeResponse objResponse = new CreateBadgeResponse();
                objResponse.setInum(badgeRequest.getInum());
                objResponse.setParticipant(badgeRequest.getParticipant());
                objResponse.setTemplateBadgeId(badgeRequest.getTemplateBadgeId());
                objResponse.setTemplateBadgeTitle(badgeRequest.getTemplateBadgeTitle());
                objResponse.setStatus(badgeRequest.getStatus());
                return objResponse;
            } else {
                throw new Exception("You have already requested for same badge");
            }
        } else {
            createBadgeRequestNew(ldapEntryManager, badgeRequest);
        }

        throw new Exception("There was problem creating a badge request");
    }

    /**
     * Get pending badge request according to participant
     *
     * @param ldapEntryManager ldapEntryManager
     * @param id               pass participant id
     * @param status           pass status
     * @return
     */
    public static List<BadgeRequests> getPendingBadgeRequests(LdapEntryManager ldapEntryManager, String id, String status) throws Exception {

        BadgeRequests badgeRequest = new BadgeRequests();
        badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

        return (ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuParticipant=" + id + ")(gluuStatus=" + status + "))")));
    }

    /**
     * Get pending badge request according to participant
     *
     * @param ldapEntryManager ldapEntryManager
     * @param id               pass participant id
     * @param status           pass status
     * @return
     */
    public static List<CreateBadgeResponse> getPendingBadgeRequestsNew(LdapEntryManager ldapEntryManager, String id, String status) throws Exception {

        BadgeRequests badgeRequest = new BadgeRequests();
        badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

        return GetResponseList(ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuParticipant=" + id + ")(gluuStatus=" + status + "))")));
    }

    /**
     * Deletes badge request by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param inum             inum of the badge request to be deleted
     * @return
     */
    public static boolean deleteBadgeRequestByInum(LdapEntryManager ldapEntryManager, String inum) {
        try {
            BadgeRequests badgeRequest = new BadgeRequests();
            badgeRequest.setDn("inum=" + inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            badgeRequest.setInum(inum);
            if (ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")"))) {
                ldapEntryManager.remove(badgeRequest);
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
     * Update badge request by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param badgeRequest     badge request to be updated
     * @return
     */
    public static boolean updateBadgeRequest(LdapEntryManager ldapEntryManager, BadgeRequests badgeRequest) {
        try {
            badgeRequest.setUpdatedAt(new Date());
            badgeRequest.setDn("inum=" + badgeRequest.getInum() + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            if (ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")"))) {
                MergeService.merge(badgeRequest, ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")")).get(0));
                ldapEntryManager.merge(badgeRequest);
                System.out.println("Badge request updated successfully");
                return true;
            } else {
                System.out.println("Badge request not found");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get approved badge request for user
     *
     * @param ldapEntryManager ldapEntryManager
     * @param email            user email
     * @param status           status
     * @return
     */
    public static List<BadgeRequests> getBadgeRequestsByStatus(LdapEntryManager ldapEntryManager, String email, String status) throws Exception {

        BadgeRequests badgeRequest = new BadgeRequests();
        badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

        return (ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(gluuStatus=" + status + "))")));
    }

    /**
     * Get approved badge request for user
     *
     * @param ldapEntryManager ldapEntryManager
     * @param email            user email
     * @param status           status
     * @return
     */
    public static List<CreateBadgeResponse> getBadgeRequestsByStatusNew(LdapEntryManager ldapEntryManager, String email, String status) throws Exception {

        BadgeRequests badgeRequest = new BadgeRequests();
        badgeRequest.setDn("ou=badgeRequests, ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

        return GetResponseList((ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(&(gluuBadgeRequester=" + email + ")(gluuStatus=" + status + "))"))));
    }

    /**
     * Retrieves a badge request by Inum
     *
     * @param ldapEntryManager ldapEntryManager
     * @param Inum             Inum of the badge request that is to be retrieved.
     * @return
     */
    public static BadgeRequests getBadgeRequestByInum(LdapEntryManager ldapEntryManager, String Inum) {
        try {

            BadgeRequests badgeRequest = new BadgeRequests();
            badgeRequest.setInum(Inum);
            badgeRequest.setDn("inum=" + Inum + ",ou=badgeRequests,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");

            if (ldapEntryManager.contains(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")"))) {
                List<BadgeRequests> badgeRequests = ldapEntryManager.findEntries(badgeRequest.getDn(), BadgeRequests.class, Filter.create("(inum=" + badgeRequest.getInum() + ")"));
                if (badgeRequests.size() > 0)
                    return badgeRequests.get(0);
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

    private static List<CreateBadgeResponse> GetResponseList(List<BadgeRequests> lstBadgeRequests){
        List<CreateBadgeResponse> lstBadgeResponse = new ArrayList<>();
        for (BadgeRequests obj : lstBadgeRequests ) {
            CreateBadgeResponse objResponse = new CreateBadgeResponse();
            objResponse.setInum(obj.getInum());
            objResponse.setParticipant(obj.getParticipant());
            objResponse.setTemplateBadgeId(obj.getTemplateBadgeId());
            objResponse.setTemplateBadgeTitle(obj.getTemplateBadgeTitle());
            objResponse.setStatus(obj.getStatus());

            lstBadgeResponse.add(objResponse);
        }
        return lstBadgeResponse;
    }
}