package org.xdi.oxd.badgemanager.web;

import com.google.common.hash.Hashing;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
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
import org.xdi.oxd.badgemanager.qrcode.QRCBuilder;
import org.xdi.oxd.badgemanager.qrcode.ZXingQRCodeBuilder;
import org.xdi.oxd.badgemanager.service.RedisService;
import org.xdi.oxd.badgemanager.service.UserInfoService;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;
import org.xdi.oxd.badgemanager.util.JWTUtil;
import org.xdi.oxd.badgemanager.util.Utils;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.xdi.oxd.badgemanager.qrcode.decorator.ColoredQRCode.colorizeQRCode;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@Api(basePath = "/badges", description = "badges apis")
@RequestMapping("/badges")

@ContextConfiguration(classes = RedisService.class, loader = AnnotationConfigContextLoader.class)
@ComponentScan({"org.xdi.oxd.badgemanager.service"})
public class BadgeController {

    private static final Logger logger = LoggerFactory.getLogger(BadgeController.class);

    @Autowired
    public RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private UserInfoService userInfoService;

    @Inject
    private Utils utils;

    @Inject
    private JWTUtil jwtUtil;

    private final ServletContext context;

    @Inject
    public NotificationController notificationController;

    @Autowired
    public BadgeController(ServletContext context) {
        this.context = context;
    }

    public void setRedisData(String key, String value, int timeout) throws IOException {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public void setRedisData(String key, String value) throws IOException {
        redisTemplate.opsForValue().set(key, value);
    }

    @RequestMapping(value = "templates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTemplateBadgesByParticipant(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody TemplateBadgeRequest templateBadgeRequest, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

            if (accessToken == null || templateBadgeRequest.getOpHost() == null) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            }

//            retrieve user info
            UserInfo userInfo = userInfoService.getUserInfo(templateBadgeRequest.getOpHost(), accessToken);
            if (userInfo != null) {
                String issuer = userInfo.getIssuer();
            }

            String issuer = templateBadgeRequest.getOpHost();

            IssuerBadgeRequest issuerBadgeRequest = new IssuerBadgeRequest(issuer, templateBadgeRequest.getType());

            final String uri = Global.API_ENDPOINT + Global.getTemplateBadgesByParticipant;

            DisableSSLCertificateCheckUtil.disableChecks();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + Global.Request_AccessToken);

            HttpEntity<IssuerBadgeRequest> request = new HttpEntity<IssuerBadgeRequest>(issuerBadgeRequest, headers);

            HttpEntity<String> strResponse = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            String result = strResponse.getBody();

            JsonArray jObjResponse = new JsonParser().parse(result).getAsJsonArray();
            if (jObjResponse != null && jObjResponse.size() > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.add("badges", GsonService.getGson().toJsonTree(jObjResponse));
                jsonResponse.addProperty("error", false);
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No badges found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Please try after some time");
            logger.error("Exception in retrieving template badges:" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "verify/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String verifyBadge(@PathVariable String id, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
            Badges badges = BadgeCommands.getBadgeById(id);
            if (badges != null && badges.getGuid() != null) {

                if (badges.getGluuValidatorAccess() == null || !badges.getGluuValidatorAccess().equalsIgnoreCase("true")) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "You're not authorized to see this badge");
                    return jsonResponse.toString();
                }

                BadgeResponse badgeResponse = BadgeCommands.getBadgeResponseById(id);
                if (badgeResponse != null) {
                    notifyAsserter(badges.getGuid());
                    return returnBadgeResponse(badgeResponse, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in verifying badge in verifyBadge():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "verify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String verifyPrivateBadge(@RequestParam String id, @RequestParam String key, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {

            Badges badges = BadgeCommands.getBadgeByIdAndKey(id, key);
            if (badges != null && badges.getGuid() != null) {

                if (badges.getGluuValidatorAccess() == null || !badges.getGluuValidatorAccess().equalsIgnoreCase("true")) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "You're not authorized to see this badge");
                    return jsonResponse.toString();
                }

                BadgeResponse badgeResponse = BadgeCommands.getBadgeResponseByIdAndKey(id, key);
                if (badgeResponse != null) {
                    notifyAsserter(badges.getGuid());
                    return returnBadgeResponse(badgeResponse, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in verifying badge in verifyPrivateBadge():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "{badgeRequestInum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadge(@RequestHeader(value = "Authorization") String authorization, @PathVariable String badgeRequestInum, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
            if (!authorization.equalsIgnoreCase(Global.AccessToken)) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }

            BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestByInum(badgeRequestInum);
            if (badgeRequests != null && badgeRequests.getInum() != null) {
                BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(badgeRequests.getInum());
                if (badgeClass != null && badgeClass.getInum() != null) {
                    Badges badges = BadgeCommands.getBadgeByBadgeClassInum(badgeClass.getInum());
                    if (badges != null && badges.getInum() != null) {
                        BadgeResponse badgeResponse = BadgeCommands.getBadgeResponseByIdNew(badges.getGuid());
                        if (badgeResponse != null) {
                            return returnBadgeResponse(badgeResponse, response);
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        jsonResponse.addProperty("error", true);
                        jsonResponse.addProperty("errorMsg", "No such badge found");
                        return jsonResponse.toString();
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                        jsonResponse.addProperty("error", true);
                        jsonResponse.addProperty("errorMsg", "No such badge found");
                        return jsonResponse.toString();
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            logger.error("Exception in retrieving badge in getBadge():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeByBadgeRequestInum(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody BadgeRequestDetail badgeRequest, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
            if (accessToken == null || accessToken.length() == 0 || badgeRequest == null
                    || badgeRequest.getOpHost() == null || badgeRequest.getBadgeRequestInum() == null
                    || badgeRequest.getOpHost().length() == 0 || badgeRequest.getBadgeRequestInum().length() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }

            UserInfo userInfo = userInfoService.getUserInfo(badgeRequest.getOpHost(), accessToken);
            if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().length() == 0
                    || userInfo.getUserInfoJSON() == null || userInfo.getUserInfoJSON().length() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestForUserByInum(badgeRequest.getBadgeRequestInum(), email);
            if (badgeRequests != null && badgeRequests.getInum() != null) {
                BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(badgeRequests.getInum());
                if (badgeClass != null && badgeClass.getInum() != null) {
                    Badges badges = BadgeCommands.getBadgeByBadgeClassInum(badgeClass.getInum());
                    if (badges != null && badges.getInum() != null) {

                        badges.setRecipientIdentity(jwtUtil.generateJWT(userInfo.getUserInfoJSON()));
                        if (!BadgeCommands.updateBadge(badges)) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            jsonResponse.addProperty("error", true);
                            jsonResponse.addProperty("errorMsg", "No such badge found");
                            return jsonResponse.toString();
                        }

                        if (generateQrCode(badges, badgeClass.getImage(), badges.getGuid(), 250, "png")) {
                            logger.info("QR Code generated successfully");

                            DisplayBadge badge = new DisplayBadge();
                            badge.setQrCode(utils.getBaseURL(request) + File.separator + "images" + File.separator + badges.getImage());
                            badge.setBadgePublicURL(badges.getId());
                            if (badges.getBadgePrivacy().equalsIgnoreCase("private")) {
                                badge.setBadgePublicURL("");
                            }
                            badge.setBadgeTitle(badgeRequests.getTemplateBadgeTitle());

                            Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
                            calendar.add(Calendar.SECOND, 95);
                            badge.setExpiresAt(calendar.getTime().toString());

                            Recipient recipient = new Recipient();
                            recipient.setType(badges.getRecipientType());
                            recipient.setIdentity(badges.getRecipientIdentity());
                            badge.setRecipient(recipient);

                            response.setStatus(HttpServletResponse.SC_OK);
                            return GsonService.getGson().toJson(badge);
                        } else {
                            logger.error("Failed to generate QR Code");
                            response.setStatus(HttpServletResponse.SC_OK);
                            jsonResponse.addProperty("error", true);
                            jsonResponse.addProperty("errorMsg", "No such badge found");
                            return jsonResponse.toString();
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                        jsonResponse.addProperty("error", true);
                        jsonResponse.addProperty("errorMsg", "No such badge found");
                        return jsonResponse.toString();
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge found");
                return jsonResponse.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", ex.getMessage());
            logger.error("Error in retrieving badge details in getBadgeByBadgeRequestInum(): " + ex.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "setPrivacy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String setBadgePrivacy(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody PrivacyRequest privacy, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {

            if (accessToken == null || accessToken.length() == 0 || privacy == null
                    || privacy.getOpHost() == null || privacy.getBadgeRequestInum() == null
                    || privacy.getOpHost().length() == 0 || privacy.getBadgeRequestInum().length() == 0) {
                jsonResponse.addProperty("error", true);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                return jsonResponse.toString();
            }

            UserInfo userInfo = userInfoService.getUserInfo(privacy.getOpHost(), accessToken);
            if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestForUserByInum(privacy.getBadgeRequestInum(), email);
            if (badgeRequests != null && badgeRequests.getInum() != null) {
                BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(badgeRequests.getInum());
                if (badgeClass != null && badgeClass.getInum() != null) {
                    Badges badges = BadgeCommands.getBadgeByBadgeClassInum(badgeClass.getInum());
                    if (badges != null && badges.getInum() != null) {

                        badges.setBadgePrivacy(privacy.getPrivacy());
                        badges.setId(utils.getBaseURL(request) + "/badges/verify/" + badges.getGuid());
                        if (badges.getBadgePrivacy().equalsIgnoreCase("public")) {
                            badges.setId(utils.getBaseURL(request) + "/badges/verify/" + badges.getGuid());
                        } else if (badges.getBadgePrivacy().equalsIgnoreCase("private")) {
                            badges.setId("");
                        } else {
                            response.setStatus(HttpServletResponse.SC_OK);
                            jsonResponse.addProperty("error", true);
                            jsonResponse.addProperty("errorMsg", "Failed to set badge privacy");
                            return jsonResponse.toString();
                        }

                        boolean isUpdated = BadgeCommands.updateBadge(badges);
                        if (isUpdated) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            jsonResponse.addProperty("error", false);
                            jsonResponse.addProperty("message", "Badge privacy set to " + badges.getBadgePrivacy() + " successfully");
                            return jsonResponse.toString();
                        } else {
                            response.setStatus(HttpServletResponse.SC_OK);
                            jsonResponse.addProperty("error", true);
                            jsonResponse.addProperty("errorMsg", "Failed to set badge privacy");
                            return jsonResponse.toString();
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                        jsonResponse.addProperty("error", true);
                        jsonResponse.addProperty("errorMsg", "No such badge found");
                        return jsonResponse.toString();
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in set badge privacy in setBadgePrivacy():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "setPermission", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String setBadgePermission(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody AccessRequest accessRequest, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        String strTempURL = "", msg;
        try {

            if (accessToken == null || accessToken.length() == 0 || accessRequest == null
                    || accessRequest.getOpHost() == null || accessRequest.getOpHost().length() == 0
                    || accessRequest.getAccess() == null || accessRequest.getAccess().length() == 0
                    || accessRequest.getBadge() == null || accessRequest.getBadge().length() == 0) {
                jsonResponse.addProperty("error", true);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Missing required details");
                return jsonResponse.toString();
            }

            UserInfo userInfo = userInfoService.getUserInfo(accessRequest.getOpHost(), accessToken);
            if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Invalid user");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            if (redisTemplate.opsForValue().get(accessRequest.getBadge()) == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Unable to set permission");
                return jsonResponse.toString();
            }

            String linkedData = (String) redisTemplate.opsForValue().get(accessRequest.getBadge());
            JsonObject jObjLinkedData = new JsonParser().parse(linkedData).getAsJsonObject();
            if (jObjLinkedData == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Unable to set permission");
                return jsonResponse.toString();
            }
            String toEmail = GsonService.getValueFromJson("Validator", jObjLinkedData);
            logger.info("Redis data: " + jObjLinkedData);
            logger.info("Validator: " + toEmail);

            Person person = PersonCommands.getPersonByEmail(toEmail);
            if (person == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Validator entry not found");
                return jsonResponse.toString();
            }
            java.util.List<DeviceRegistration> deviceRegistrations = PersonCommands.getDeviceRegistrationByPerson(person.getInum());
            logger.info("Device registration counts in setBadgePermission: " + deviceRegistrations.size());
            if (deviceRegistrations.size() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Device entry not found");
                return jsonResponse.toString();
            }
            DeviceRegistration deviceRegistration = deviceRegistrations.get(0);
            logger.info("Device data in setBadgePermission: " + deviceRegistration.getDeviceData());
            DeviceData deviceData = deviceRegistration.getDeviceData();
            if (!deviceData.getPlatform().equalsIgnoreCase("android")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Device entry not found");
                return jsonResponse.toString();
            }

            Badges badges = BadgeCommands.getBadgeById(accessRequest.getBadge());
            if (badges == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge found");
                return jsonResponse.toString();
            }

            badges.setGluuValidatorAccess(accessRequest.getAccess());

            boolean isUpdated = BadgeCommands.updateBadge(badges);
            if (isUpdated) {

                response.setStatus(HttpServletResponse.SC_OK);
                String access = accessRequest.getAccess().equalsIgnoreCase("true") ? "granted" : "revoked";
                jsonResponse.addProperty("error", false);

                if (access.equalsIgnoreCase("granted")) {
                    String tempURLBase = utils.getBaseURL(request);
                    String tempURL = tempURLBase + "/badges/" + badges.getGuid();

                    if (badges.getBadgePrivacy().equalsIgnoreCase("private")) {
                        setRedisData(badges.getKey(), badges.getId(), 95);
                        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
                        calendar.add(Calendar.SECOND, 95);
                        badges.setExpires(calendar.getTime());
                        boolean isBadgeUpdated = BadgeCommands.updateBadge(badges);
                        logger.info("Badge updated after expires set:" + isBadgeUpdated);
                        tempURL = tempURLBase + "/badges/" + badges.getGuid() + "?key=" + badges.getKey();
                    }

                    String redisKey = badges.getGuid() + badges.getKey();
                    setRedisData(redisKey, tempURL, 95);
                    final String id = Hashing.murmur3_32().hashString(tempURL, StandardCharsets.UTF_8).toString();
                    logger.info("Shortern url id:" + id);
                    String shortenURL = tempURLBase + "/tmp/" + id;
                    logger.info("Shortern url:" + shortenURL);
                    strTempURL = shortenURL;
                    setRedisData(id, tempURL, 95);
                }

                msg = "Permission to see this badge " + access + " successfully";

                sendNotification(msg, email, deviceData.getPushToken(), strTempURL, 2);

                jsonResponse.addProperty("message", msg);
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Failed to set badge access");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", e.getMessage());
            logger.error("Exception in set badge permission in setBadgePermission():" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    private void sendNotification(String msg, String fromEmail, String toDeviceToken, String tempURL, int type) {

//        String toDeviceToken = "e_ZEo4DHlqE:APA91bFo6BDgrQQzU4xkHpAgiLDhVlMOq1ivpjNh0X8tViqNuxZnVjKHLjgG0uMqZYsup1_MU_VkOzDO-nplW7QNa9wlYaoB_eoj3-Xdt-QCeIXOgCgoDdzeT1mnOEhBZoX00FzCKBkO";

        JsonObject data = new JsonObject();
        data.addProperty("fromAsserter", fromEmail);
        data.addProperty("tempUrl", tempURL);
        data.addProperty("notifyType", type);

        notificationController.send(msg, toDeviceToken, data);
    }

    private void notifyAsserter(String id) {
        try {
            if (redisTemplate.opsForValue().get(id) != null) {
                String linkedData = (String) redisTemplate.opsForValue().get(id);
                JsonObject jObjLinkedData = new JsonParser().parse(linkedData).getAsJsonObject();
                if (jObjLinkedData != null && jObjLinkedData.get("Asserter") != null) {

                    Person person = PersonCommands.getPersonByEmail(GsonService.getValueFromJson("Asserter", jObjLinkedData));
                    if (person != null) {
                        List<DeviceRegistration> deviceRegistrations = PersonCommands.getDeviceRegistrationByPerson(person.getInum());
                        logger.info("Device registration counts: " + deviceRegistrations.size());
                        if (deviceRegistrations.size() > 0) {
                            DeviceRegistration deviceRegistration = deviceRegistrations.get(0);
                            logger.info("Device data: " + deviceRegistration.getDeviceData());
                            DeviceData deviceData = deviceRegistration.getDeviceData();
                            if (deviceData.getPlatform().equalsIgnoreCase("android") && deviceData.getPushToken() != null) {
                                sendNotification("Badge verified successfully", "", deviceData.getPushToken(), "", 3);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in notify asserter " + ex.getMessage());
        }
    }

    /**
     * Call this method to create a QR-code image. You must provide the
     * OutputStream where the image data can be written.
     *
     * @param badge       The  badge object from which content should be encoded with the QR-code.
     * @param tempURL
     * @param qrCodeSize  The QR-code must be quadratic. So this is the number of pixel
     *                    in width and height.
     * @param imageFormat The image format in which the image should be rendered. As
     *                    Example 'png' or 'jpg'. See @javax.imageio.ImageIO for more
     *                    information which image formats are supported.   @throws Exception If an Exception occur during the create of the QR-code or
     */
    private boolean generateQrCode(Badges badge, String logoFileURL, String tempURL, int qrCodeSize, String imageFormat) {
        try {

            float TRANSPARENCY = 0.75f;
            float OVERLAY_RATIO = 0.25f;
//            logger.info("Logo file url:" + logoFileURL);

            //logo file
//            String logoFileName = + System.currentTimeMillis() + ".png";
////            logoFileURL = logoFileURL.replace("127.0.0.1", "192.168.200.79");
//            //Server
//            String logoPath = utils.getStaticResourceLogoPath(context);
//            //Local
////            String logoPath = "src/main/resources/static/logo";
//            logger.info("logo file path :" + logoPath);
//            if (new File(logoPath).exists()) {
//                logger.info("Directory exists:" + logoPath);
//                FileUtils.cleanDirectory(new File(logoPath));
//            }
//            String logoFilePath = logoPath + File.separator + logoFileName;
//            logger.info("logo file path:" + logoFilePath);
//            if(!downloadUsingStream(logoFileURL, logoFilePath)){
//                return false;
//            }
//            File logoFile = new File(logoFilePath);
//
//            if (!logoFile.exists()) {
//                return false;
//            }

            //location of barcode
            String fileName = System.currentTimeMillis() + ".png";
            //Server
            String imagesPath = utils.getStaticResourcePath(context);
            //Local
//            String imagesPath = "src/main/resources/static/images";
            logger.info("path :" + imagesPath);
            if (new File(imagesPath).exists()) {
                logger.info("Directory exists:" + imagesPath);
                FileUtils.cleanDirectory(new File(imagesPath));
            }
            String filePath = imagesPath + File.separator + fileName;
            logger.info("file path:" + filePath);

            QRCBuilder<BufferedImage> qrCodeBuilder = new ZXingQRCodeBuilder();
            qrCodeBuilder.newQRCode()
                    .withSize(qrCodeSize, qrCodeSize)
                    .and()
                    .withData(tempURL)
                    .and()
                    .decorate(colorizeQRCode(new Color(51, 102, 153)))
//                    .and()
//                    .decorate(addImageOverlay(ImageIO.read(logoFile), TRANSPARENCY, OVERLAY_RATIO))
                    .and()
                    .doVerify(false)
                    .toFile(filePath, imageFormat);

            String[] arFilePath = fileName.split("/resources");
            if (arFilePath.length > 0) {
                String filepath = arFilePath[arFilePath.length - 1];
                badge.setImage(filepath);
            }

            return true;
        } catch (Exception ex) {
            logger.error("Exception in generating qr code: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private boolean downloadUsingStream(String urlStr, String file) throws IOException {
        try {

            final String userAgent = "Mozilla/5.0 (X11; U; Linux i586; en-US; rv:1.7.3) Gecko/20040924 Epiphany/1.4.4 (Ubuntu)";

            Connection.Response resultImageResponse = Jsoup.connect(urlStr)
                    .userAgent(userAgent)
                    .ignoreContentType(true).execute();

            FileOutputStream out = (new FileOutputStream(file));
            out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
            out.close();

//            URL url = new URL(urlStr);
//            BufferedInputStream bis = new BufferedInputStream(url.openStream());
//            FileOutputStream fis = new FileOutputStream(file);
//            byte[] buffer = new byte[1024];
//            int count = 0;
//            while ((count = bis.read(buffer, 0, 1024)) != -1) {
//                fis.write(buffer, 0, count);
//            }
//            fis.close();
//            bis.close();
            logger.info("Logo image stored successfully");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception in storing logo image:" + ex.getMessage());
            return false;
        }
    }

    private String returnBadgeResponse(BadgeResponse badgeResponse, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();
        if (badgeResponse != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.addProperty("error", false);
            return GsonService.getGson().toJson(badgeResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "No such badge found");
            return jsonResponse.toString();
        }
    }
}