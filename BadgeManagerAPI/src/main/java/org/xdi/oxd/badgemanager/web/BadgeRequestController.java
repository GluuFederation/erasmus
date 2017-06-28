package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonArray;
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
import org.xdi.oxauth.model.fido.u2f.protocol.DeviceData;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeClassesCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeRequestCommands;
import org.xdi.oxd.badgemanager.ldap.commands.PersonCommands;
import org.xdi.oxd.badgemanager.ldap.models.BadgeClass;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.models.Person;
import org.xdi.oxd.badgemanager.ldap.models.fido.u2f.DeviceRegistration;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.model.*;
import org.xdi.oxd.badgemanager.service.UserInfoService;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;
import org.xdi.oxd.badgemanager.util.JWTUtil;
import org.xdi.oxd.badgemanager.util.Utils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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

    @Inject
    private UserInfoService userInfoService;

    @Inject
    private NotificationController notificationController;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createBadgeRequest(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody CreateBadgeRequest badgeRequest, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

            if (accessToken == null || accessToken.length() == 0 || badgeRequest == null
                    || badgeRequest.getOpHost() == null || badgeRequest.getOpHost().length() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }

//            retrieve user info
            UserInfo userInfo = userInfoService.getUserInfo(badgeRequest.getOpHost(), accessToken);
            if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                jsonResponse.addProperty("error", true);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            BadgeRequests objBadgeRequest = new BadgeRequests();
            objBadgeRequest.setGluuBadgeRequester(email);
            objBadgeRequest.setParticipant(badgeRequest.getParticipant());
            objBadgeRequest.setTemplateBadgeId(badgeRequest.getTemplateBadgeId());
            objBadgeRequest.setTemplateBadgeTitle(badgeRequest.getTemplateBadgeTitle());
            objBadgeRequest.setFidesAccess(true);

            CreateBadgeResponse objBadgeResponse = BadgeRequestCommands.createBadgeRequestNew(objBadgeRequest);
            if (objBadgeResponse != null) {
                jsonResponse.add("badgeRequest", GsonService.getGson().toJsonTree(objBadgeResponse));
                jsonResponse.addProperty("error", false);
            } else {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You have already requested same badge");
            }

            response.setStatus(HttpServletResponse.SC_OK);
            return jsonResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in adding badge request entry in createBadgeRequest():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "list/{participant:.+}/{status:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeRequestsByParticipant(@RequestHeader(value = "Authorization") String authorization, @PathVariable String participant, @PathVariable String status, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {

            if (!authorization.equalsIgnoreCase(Global.AccessToken)) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            }

            List<CreateBadgeResponse> lstBadgeRequests = BadgeRequestCommands.getBadgeRequestsByParticipant(participant, status);
            if (lstBadgeRequests.size() > 0) {
                jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(lstBadgeRequests));
                jsonResponse.addProperty("error", false);
            } else {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No badge requests found");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            return jsonResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in retrieving pending badge requests in getPendingBadgeRequestsByParticipant():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "approve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String approveBadgeRequest(@RequestHeader(value = "Authorization") String authorization, @RequestBody ApproveBadge approveBadge, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
            if (!authorization.equalsIgnoreCase(Global.AccessToken)) {
                jsonResponse.addProperty("error", true);
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }

            BadgeRequests badgeRequest = new BadgeRequests();
            badgeRequest.setInum(approveBadge.getInum());
            badgeRequest.setStatus("Approved");
            badgeRequest.setValidity(approveBadge.getValidity());
            badgeRequest.setFidesAccess(true);

            BadgeRequests objBadgeRequest = BadgeRequestCommands.getBadgeRequestByInum(approveBadge.getInum());

            if (objBadgeRequest == null || objBadgeRequest.getInum() == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge request found");
            }

            Person person = PersonCommands.getPersonByEmail(objBadgeRequest.getGluuBadgeRequester());

            if (createBadgeClass(request, approveBadge, objBadgeRequest, person)) {
                if (BadgeRequestCommands.updateBadgeRequest(badgeRequest)) {
                    if (person != null) {
                        List<DeviceRegistration> deviceRegistrations = PersonCommands.getDeviceRegistrationByPerson(person.getInum());
                        logger.info("Device registration counts in approveBadgeRequest(): " + deviceRegistrations.size());
                        if (deviceRegistrations.size() > 0) {
                            DeviceRegistration deviceRegistration = deviceRegistrations.get(0);
                            logger.info("Device data in approveBadgeRequest(): " + deviceRegistration.getDeviceData());
                            DeviceData deviceData = deviceRegistration.getDeviceData();
                            if (deviceData != null && deviceData.getPlatform().equalsIgnoreCase("android") && deviceData.getPushToken() != null && deviceData.getPushToken().length() > 0) {
                                JsonObject data = new JsonObject();
                                data.addProperty("notifyType", 4);

                                String msg = "Your badge " + badgeRequest.getTemplateBadgeTitle() + " approved successfully";

                                notificationController.send(msg, deviceData.getPushToken(), data);
                            } else {
                                logger.info("No android device found in approveBadgeRequest()");
                            }
                        } else {
                            logger.info("Device not found in approveBadgeRequest()");
                        }
                    } else {
                        logger.info("Person not found in approveBadgeRequest() with email:" + badgeRequest.getGluuBadgeRequester());
                    }

                    jsonResponse.addProperty("message", "Badge request approved successfully");
                } else {
                    jsonResponse.addProperty("message", "Badge request approved failed");
                }
            } else {
                jsonResponse.addProperty("message", "Badge request approved failed");
            }

            jsonResponse.addProperty("error", false);
            response.setStatus(HttpServletResponse.SC_OK);
            return jsonResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in approving badge request in approveBadgeRequest():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeRequestsByStatus(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody BadgeRequest badgeRequest, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {

            if (accessToken == null || accessToken.length() == 0 || badgeRequest == null
                    || badgeRequest.getOpHost() == null || badgeRequest.getStatus() == null
                    || badgeRequest.getOpHost().length() == 0 || badgeRequest.getStatus().length() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }

            if (!badgeRequest.getStatus().equalsIgnoreCase("all")
                    && !badgeRequest.getStatus().equalsIgnoreCase("approved")
                    && !badgeRequest.getStatus().equalsIgnoreCase("pending")) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Invalid status");
                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            }

            UserInfo userInfo = userInfoService.getUserInfo(badgeRequest.getOpHost(), accessToken);
            if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            BadgeRequestResponse badgeRequests = new BadgeRequestResponse();
            badgeRequests.setApprovedBadgeRequests(new ArrayList<>());
            badgeRequests.setPendingBadgeRequests(new ArrayList<>());
            if (badgeRequest.getStatus().equalsIgnoreCase("all")) {
                List<CreateBadgeResponse> lstApprovedBadgeRequests = BadgeRequestCommands.getBadgeRequestsByStatusNew(email, "Approved");
                List<CreateBadgeResponse> lstPendingBadgeRequests = BadgeRequestCommands.getBadgeRequestsByStatusNew(email, "Pending");
                if (lstPendingBadgeRequests.size() > 0) {
                    badgeRequests.setPendingBadgeRequests(lstPendingBadgeRequests);
                }
                if (lstApprovedBadgeRequests.size() > 0) {
                    badgeRequests.setApprovedBadgeRequests(lstApprovedBadgeRequests);
                }
                if ((badgeRequests.getApprovedBadgeRequests() == null || badgeRequests.getApprovedBadgeRequests().size() == 0) && (badgeRequests.getPendingBadgeRequests() == null || badgeRequests.getPendingBadgeRequests().size() == 0)) {
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No badge requests found");
                } else {
                    jsonResponse.addProperty("error", false);
                    jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(badgeRequests));
                }
            } else {
                List<CreateBadgeResponse> lstBadgeRequests = BadgeRequestCommands.getBadgeRequestsByStatusNew(email, badgeRequest.getStatus());
                if (lstBadgeRequests.size() == 0) {
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No " + badgeRequest.getStatus() + " badge requests found");
                } else {
                    if (badgeRequest.getStatus().equalsIgnoreCase("Approved")) {
                        badgeRequests.setApprovedBadgeRequests(lstBadgeRequests);
                    } else if (badgeRequest.getStatus().equalsIgnoreCase("Pending")) {
                        badgeRequests.setPendingBadgeRequests(lstBadgeRequests);
                    }
                    jsonResponse.addProperty("error", false);
                    jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(badgeRequests));
                }
            }

            response.setStatus(HttpServletResponse.SC_OK);
            return jsonResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in retrieving badge requests getBadgeRequestsByStatus():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeBadgeRequest(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody BadgeRequestDetail badgeRequest, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        try {

            if (accessToken == null || accessToken.length() == 0 || badgeRequest == null
                    || badgeRequest.getOpHost() == null || badgeRequest.getBadgeRequestInum() == null
                    || badgeRequest.getOpHost().isEmpty() || badgeRequest.getBadgeRequestInum().length() == 0) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            }

            UserInfo userInfo = userInfoService.getUserInfo(badgeRequest.getOpHost(), accessToken);
            if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            boolean isDeleted = BadgeRequestCommands.deleteUserBadgeRequestByInum(badgeRequest.getBadgeRequestInum(), email);
            if (isDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", false);
                jsonResponse.addProperty("message", "Badge Request deleted successfully");
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No badge request found");
                return jsonResponse.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", ex.getMessage());
            logger.error("Exception in removing badge request in removeBadgeRequest():" + ex.getMessage());
            return jsonResponse.toString();
        }
    }

    private boolean createBadgeClass(HttpServletRequest servletRequest, ApproveBadge approveBadgeRequest, BadgeRequests objBadgeRequest, Person person) {
        try {

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

                objBadgeClass = BadgeClassesCommands.createBadgeClass(objBadgeClass);

                if (objBadgeClass != null && objBadgeClass.getInum() != null) {
                    Badges objBadge = createBadge(servletRequest, objBadgeClass, approveBadgeRequest, person);
                    if (objBadge != null && objBadge.getInum() != null) {
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

        } catch (Exception ex) {
            logger.error("Exception in insert badge assertion entry in createBadgeClass():" + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private Badges createBadge(HttpServletRequest servletRequest, BadgeClass objBadgeClass, ApproveBadge approveBadge, Person person) {
        Badges objBadge = new Badges();

        try {

            String userinfo = "{\"email\":\"test_user@test.org\",\"email_verified\":\"true\",\"sub\":\"K6sBjQkZQl3RP-XILa1gLa2k211zv4BgoVJCtvfRZjA\",\"zoneinfo\":\"America/Chicago\",\"nickname\":\"user\",\"website\":\"http://www.gluu.org\",\"middle_name\":\"User\",\"locale\":\"en-US\",\"preferred_username\":\"user\",\"given_name\":\"Test\",\"picture\":\"http://www.gluu.org/wp-content/uploads/2012/04/mike3.png\",\"updated_at\":\"20170224125915.538Z\",\"name\":\"oxAuth Test User\",\"birthdate\":\"1983-1-6\",\"family_name\":\"User\",\"gender\":\"Male\",\"profile\":\"http://www.mywebsite.com/profile\"}";
            if (person != null) {

                JsonArray jArraySub = new JsonArray();
                jArraySub.add(person.getSub() == null ? "" : person.getSub());
                JsonArray jArrayEmail = new JsonArray();
                jArrayEmail.add(person.getEmail() == null ? "" : person.getEmail());
                JsonArray jArrayWebsite = new JsonArray();
                jArrayWebsite.add(person.getWebsite() == null ? "" : person.getWebsite());
                JsonArray jArrayZoneInfo = new JsonArray();
                jArrayZoneInfo.add(person.getZoneinfo() == null ? "" : person.getZoneinfo());
                JsonArray jArrayEmailVerified = new JsonArray();
                jArrayEmailVerified.add(person.getEmail_verified() == null ? "" : person.getEmail_verified());
                JsonArray jArrayGender = new JsonArray();
                jArrayGender.add(person.getGender() == null ? "" : person.getGender());
                JsonArray jArrayProfile = new JsonArray();
                jArrayProfile.add(person.getProfile() == null ? "" : person.getProfile());
                JsonArray jArrayPreferredUserName = new JsonArray();
                jArrayPreferredUserName.add(person.getPreferredUsername() == null ? "" : person.getPreferredUsername());
                JsonArray jArrayGivenName = new JsonArray();
                jArrayGivenName.add(person.getGivenName() == null ? "" : person.getGivenName());
                JsonArray jArrayMiddleName = new JsonArray();
                jArrayMiddleName.add(person.getMiddle_name() == null ? "" : person.getMiddle_name());
                JsonArray jArrayLocale = new JsonArray();
                jArrayLocale.add(person.getLocale() == null ? "" : person.getLocale());
                JsonArray jArrayPicture = new JsonArray();
                jArrayPicture.add(person.getPicture() == null ? "" : person.getPicture());
                JsonArray jArrayUpdatedAt = new JsonArray();
                jArrayUpdatedAt.add(person.getUpdated_at() == null ? "" : person.getUpdated_at());
                JsonArray jArrayName = new JsonArray();
                jArrayName.add(person.getDisplayName() == null ? "" : person.getDisplayName());
                JsonArray jArrayNickName = new JsonArray();
                jArrayNickName.add(person.getNickname() == null ? "" : person.getNickname());
                JsonArray jArrayBirthDate = new JsonArray();
                jArrayBirthDate.add(person.getBirthdate() == null ? "" : person.getBirthdate());

                JsonObject jsonObject = new JsonObject();
                jsonObject.add("sub", jArraySub);
                jsonObject.add("website", jArrayWebsite);
                jsonObject.add("zoneinfo", jArrayZoneInfo);
                jsonObject.add("email_verified", jArrayEmailVerified);
                jsonObject.add("gender", jArrayGender);
                jsonObject.add("profile", jArrayProfile);
                jsonObject.add("preferred_username", jArrayPreferredUserName);
                jsonObject.add("given_name", jArrayGivenName);
                jsonObject.add("middle_name", jArrayMiddleName);
                jsonObject.add("locale", jArrayLocale);
                jsonObject.add("picture", jArrayPicture);
                jsonObject.add("updated_at", jArrayUpdatedAt);
                jsonObject.add("name", jArrayName);
                jsonObject.add("nickname", jArrayNickName);
                jsonObject.add("email", jArrayEmail);

                userinfo = jsonObject.toString();
            }

            //Create actual badge entry(badge assertion)
            objBadge.setContext("https://w3id.org/openbadges/v2");
            objBadge.setType("Assertion");
            objBadge.setRecipientType("text");
            objBadge.setRecipientIdentity(jwtUtil.generateJWT(userinfo));
            objBadge.setVerificationType("hosted");
            objBadge.setBadgeClassInum(objBadgeClass.getInum());
            objBadge.setGuid(utils.generateRandomGUID());
            objBadge.setKey(utils.generateRandomKey(12));
            objBadge.setBadgePrivacy(approveBadge.getPrivacy());

            if (objBadge.getBadgePrivacy().equalsIgnoreCase("public")) {
                objBadge.setId(utils.getBaseURL(servletRequest) + "/badges/verify/" + objBadge.getGuid());
            } else if (objBadge.getBadgePrivacy().equalsIgnoreCase("private")) {
                objBadge.setId(utils.getBaseURL(servletRequest) + "/badges/verify/" + objBadge.getGuid() + "?key=" + objBadge.getKey());
            }

            return BadgeCommands.createBadge(objBadge);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in persist badge entry in createBadge()" + ex.getMessage());
        }
        return objBadge;
    }
}