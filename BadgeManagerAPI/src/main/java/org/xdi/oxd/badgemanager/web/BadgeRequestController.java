package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeClassesCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeRequestCommands;
import org.xdi.oxd.badgemanager.ldap.models.BadgeClass;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.model.ApproveBadge;
import org.xdi.oxd.badgemanager.model.BadgeRequestResponse;
import org.xdi.oxd.badgemanager.model.CreateBadgeRequest;
import org.xdi.oxd.badgemanager.model.CreateBadgeResponse;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;
import org.xdi.oxd.badgemanager.util.JWTUtil;
import org.xdi.oxd.badgemanager.util.Utils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Arvind Tomar on 14/10/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/badges/request")
public class BadgeRequestController {

    private static final Logger logger = LoggerFactory.getLogger(BadgeRequestController.class);

    @Inject
    private JWTUtil jwtUtil;

    @Inject
    private Utils utils;


    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createBadgeRequest(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody CreateBadgeRequest badgeRequest, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        if (LDAPService.isConnected()) {
            try {

                BadgeRequests objBadgeRequest = new BadgeRequests();
                objBadgeRequest.setGluuBadgeRequester("test@test.com");
                objBadgeRequest.setParticipant(badgeRequest.getParticipant());
                objBadgeRequest.setTemplateBadgeId(badgeRequest.getTemplateBadgeId());
                objBadgeRequest.setTemplateBadgeTitle(badgeRequest.getTemplateBadgeTitle());

                CreateBadgeResponse objBadgeResponse = BadgeRequestCommands.createBadgeRequestNew(LDAPService.ldapEntryManager, objBadgeRequest);
                if (objBadgeResponse != null) {
                    jsonResponse.add("badgeRequest", GsonService.getGson().toJsonTree(objBadgeResponse));
                    jsonResponse.addProperty("error", false);
                } else {
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "Unable to request badge");
                }

                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", e.getMessage());
                logger.error("Exception is adding badge request entry:" + e.getMessage());
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            logger.error("Error in connecting database:");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "listPending/{participant:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPendingBadgeRequestsByParticipant(@RequestHeader(value = "Authorization") String authorization, @PathVariable String participant, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        //Static
//        List<BadgeRequests> lstBadgeRequests=new ArrayList<>();
//        BadgeRequests obj1=new BadgeRequests();
//        obj1.setInum("@!4301.2A50.9A09.7688!1002!BA48.6F40");
//        obj1.setTemplateBadgeId("58dfa009a016c8832d9b7ea9");
//        obj1.setTemplateBadgeTitle("Emergency Medical Technician-Basic");
//        obj1.setParticipant("58e1dfaf159139ee277d7ab7");
//        obj1.setStatus("Pending");
//        obj1.setGluuBadgeRequester("test@test.com");
//        obj1.setDn("inum=@!4301.2A50.9A09.7688!1002!BA48.6F40,ou=badgeRequests,ou=badges,o=@!C460.F7DA.F3E9.4A62!0001!5EE3.2D5C,o=gluu");
//
//        BadgeRequests obj2=new BadgeRequests();
//        obj2.setInum("@!4301.2A50.9A09.7688!1002!D79C.9514");
//        obj2.setTemplateBadgeId("58dfa009a016c8832d9b7ea9");
//        obj2.setTemplateBadgeTitle("Entry-Level Firefighter");
//        obj2.setParticipant("58e1dfaf159139ee277d7ab7");
//        obj2.setStatus("Pending");
//        obj2.setGluuBadgeRequester("test@test.com");
//        obj2.setDn("inum=@!4301.2A50.9A09.7688!1002!D79C.9514,ou=badgeRequests,ou=badges,o=@!C460.F7DA.F3E9.4A62!0001!5EE3.2D5C,o=gluu");
//
//        lstBadgeRequests.add(obj1);
//        lstBadgeRequests.add(obj2);
//
//        try {
//            jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(lstBadgeRequests));
//            jsonResponse.addProperty("error", false);
//            response.setStatus(HttpServletResponse.SC_OK);
//            return jsonResponse.toString();
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            jsonResponse.addProperty("error", true);
//            jsonResponse.addProperty("errorMsg", e.getMessage());
//            return jsonResponse.toString();
//        }

        //Dynamic

        if (!authorization.equalsIgnoreCase(Global.AccessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.addProperty("error", "You're not authorized to perform this request");
            return jsonResponse.toString();
        }

        if (LDAPService.isConnected()) {
            try {
                List<CreateBadgeResponse> lstBadgeRequests = BadgeRequestCommands.getPendingBadgeRequestsNew(LDAPService.ldapEntryManager, participant, "Pending");
                if (lstBadgeRequests.size() > 0) {
                    jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(lstBadgeRequests));
                    jsonResponse.addProperty("error", false);
                } else {
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No pending badge requests found");
                }
                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", e.getMessage());
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "approve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String approveBadgeRequest(@RequestHeader(value = "Authorization") String authorization, @RequestBody ApproveBadge approveBadge, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        if (!authorization.equalsIgnoreCase(Global.AccessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.addProperty("error", "You're not authorized to perform this request");
            return jsonResponse.toString();
        }

        if (LDAPService.isConnected()) {
            boolean isUpdated;
            try {

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setInum(approveBadge.getInum());
                badgeRequest.setStatus("Approved");
                badgeRequest.setValidity(approveBadge.getValidity());

                isUpdated = BadgeRequestCommands.updateBadgeRequest(LDAPService.ldapEntryManager, badgeRequest);
                if (isUpdated) {
                    jsonResponse.addProperty("responseMsg", "Badge request approved successfully");
                    createBadgeClass(request, approveBadge.getInum());
                } else {
                    jsonResponse.addProperty("responseMsg", "Badge request approved failed");
                }
                jsonResponse.addProperty("error", false);
                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("responseMsg", e.getMessage());
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "list/{status:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeRequestsByStatus(@RequestHeader(value = "AccessToken") String accessToken, @PathVariable String status, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        if (LDAPService.isConnected()) {
            try {
                String email = accessToken;
                if (status.equalsIgnoreCase("all")) {
                    BadgeRequestResponse badgeRequests = new BadgeRequestResponse();
                    List<CreateBadgeResponse> lstApprovedBadgeRequests = BadgeRequestCommands.getBadgeRequestsByStatusNew(LDAPService.ldapEntryManager, email, "Approved");
                    List<CreateBadgeResponse> lstPendingBadgeRequests = BadgeRequestCommands.getBadgeRequestsByStatusNew(LDAPService.ldapEntryManager, email, "Pending");
                    if (lstPendingBadgeRequests.size() > 0) {
                        badgeRequests.setPendingBadgeRequests(lstPendingBadgeRequests);
                    }
                    if (lstApprovedBadgeRequests.size() > 0) {
                        badgeRequests.setApprovedBadgerequests(lstApprovedBadgeRequests);
                    }
                    if(badgeRequests.getApprovedBadgerequests().size()==0 && badgeRequests.getPendingBadgeRequests().size()==0){
                        jsonResponse.addProperty("error", true);
                        jsonResponse.addProperty("errorMsg", "No badge requests found");
                    } else {
                        jsonResponse.addProperty("error", false);
                        jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(badgeRequests));
                    }
                } else {
                    List<CreateBadgeResponse> lstBadgeRequests = BadgeRequestCommands.getBadgeRequestsByStatusNew(LDAPService.ldapEntryManager, email, status);
                    if (lstBadgeRequests.size() == 0) {
                        jsonResponse.addProperty("error", true);
                        jsonResponse.addProperty("errorMsg", "No badge requests found");
                    } else {
                        jsonResponse.addProperty("error", false);
                        jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(lstBadgeRequests));
                    }
                }

                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", e.getMessage());
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "delete/{inum:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeBadgeRequest(@PathVariable String inum, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        if (LDAPService.isConnected()) {
            boolean isDeleted = BadgeRequestCommands.deleteBadgeRequestByInum(LDAPService.ldapEntryManager, inum);
            if (isDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("success", "Badge Request deleted successfully");
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.addProperty("error", "No badge request found");
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    private boolean createBadgeClass(HttpServletRequest servletRequest, String badgeRequestInum) {
        try {
            if (LDAPService.isConnected()) {
                BadgeRequests objBadgeRequest = BadgeRequestCommands.getBadgeRequestByInum(LDAPService.ldapEntryManager, badgeRequestInum);
                if (objBadgeRequest != null) {

                    final String uri = Global.API_ENDPOINT + Global.getTemplateBadgeById + "/" + objBadgeRequest.getTemplateBadgeId();

                    DisableSSLCertificateCheckUtil.disableChecks();
                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", "Bearer " + Global.Request_AccessToken);

                    HttpEntity<String> request = new HttpEntity<>(headers);
                    HttpEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

                    String result = response.getBody();

                    JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();
                    if (jObjResponse != null) {
                        if (jObjResponse.has("message") && jObjResponse.get("message").getAsString().equalsIgnoreCase("Badge not found")) {
                            logger.error("Unable to persist badge class entry. reason is:" + jObjResponse.get("message").getAsString());
                            return false;
                        }

                        BadgeClass objBadgeClass = new BadgeClass();
                        objBadgeClass.setTemplateBadgeId(jObjResponse.get("_id").getAsString());
                        objBadgeClass.setName(jObjResponse.get("name").getAsString());
                        objBadgeClass.setType("BadgeClass");
                        objBadgeClass.setDescription(jObjResponse.get("description").getAsString());
                        objBadgeClass.setBadgeRequestInum(objBadgeRequest.getInum());
                        objBadgeClass.setImage(jObjResponse.get("image").getAsString());
                        objBadgeClass.setGuid(utils.generateRandomGUID());
                        objBadgeClass.setKey(utils.generateRandomKey(12));

                        objBadgeClass.setId(utils.getBaseURL(servletRequest) + "/badgeClass/" + objBadgeClass.getGuid() + "?key=" + objBadgeClass.getKey());

                        objBadgeClass = BadgeClassesCommands.createBadgeClass(LDAPService.ldapEntryManager, objBadgeClass);

                        if (objBadgeClass.getInum() != null) {
                            Badges objBadge = createBadge(servletRequest, objBadgeClass);
                            if (objBadge.getInum() != null) {
                                return true;
                            } else {
                                logger.error("Unable to persist badge entry");
                                return false;
                            }
                        } else {
                            logger.error("Unable to persist badge entry. reason is: badge class not persisted");
                            return false;
                        }
                    }
                }
            } else {
                logger.error("Unable to persist badge class entry.Not connected with LDAP");
                return false;
            }
        } catch (Exception ex) {
            logger.error("Exception in insert badge assertion entry:" + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private Badges createBadge(HttpServletRequest servletRequest, BadgeClass objBadgeClass) {
        Badges objBadge = new Badges();

        try {
            //Create actual badge entry(badge assertion)
            objBadge.setContext("https://w3id.org/openbadges/v2");
            objBadge.setType("Assertion");
            objBadge.setRecipientType("text");
            objBadge.setRecipientIdentity(jwtUtil.generateJWT("{\"email\":\"test_user@test.org\",\"email_verified\":\"true\",\"sub\":\"K6sBjQkZQl3RP-XILa1gLa2k211zv4BgoVJCtvfRZjA\",\"zoneinfo\":\"America/Chicago\",\"nickname\":\"user\",\"website\":\"http://www.gluu.org\",\"middle_name\":\"User\",\"locale\":\"en-US\",\"preferred_username\":\"user\",\"given_name\":\"Test\",\"picture\":\"http://www.gluu.org/wp-content/uploads/2012/04/mike3.png\",\"updated_at\":\"20170224125915.538Z\",\"name\":\"oxAuth Test User\",\"birthdate\":\"1983-1-6\",\"family_name\":\"User\",\"gender\":\"Male\",\"profile\":\"http://www.mywebsite.com/profile\"}"));
            objBadge.setVerificationType("hosted");
            objBadge.setBadgeClassInum(objBadgeClass.getInum());
            objBadge.setGuid(utils.generateRandomGUID());
            objBadge.setKey(utils.generateRandomKey(12));

            objBadge.setId(utils.getBaseURL(servletRequest) + "/badges/verify/" + objBadge.getGuid() + "?key=" + objBadge.getKey());

            return BadgeCommands.createBadge(LDAPService.ldapEntryManager, objBadge);
        } catch (Exception ex) {
            logger.error("Exception in persist badge entry." + ex.getMessage());
        }

        return objBadge;
    }


}