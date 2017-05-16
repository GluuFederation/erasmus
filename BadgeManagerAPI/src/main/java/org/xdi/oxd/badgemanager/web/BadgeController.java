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
import org.springframework.data.redis.core.RedisTemplate;
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
import org.xdi.oxd.badgemanager.model.*;
import org.xdi.oxd.badgemanager.qrcode.QRCBuilder;
import org.xdi.oxd.badgemanager.qrcode.ZXingQRCodeBuilder;
import org.xdi.oxd.badgemanager.service.UserInfoService;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;
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
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.xdi.oxd.badgemanager.qrcode.decorator.ColoredQRCode.colorizeQRCode;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@Api(basePath = "/badges", description = "badges apis")
@RequestMapping("/badges")
public class BadgeController {

    private static final Logger logger = LoggerFactory.getLogger(BadgeController.class);

    @Autowired
    public RedisTemplate<Object, Object> redisTemplate;

    @Inject
    private UserInfoService userInfoService;

    @Inject
    private Utils utils;

    private final ServletContext context;

    @Autowired
    public BadgeController(ServletContext context) {
        this.context = context;
    }

    public void setRedisData(String key, String value, int timeout) throws IOException {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
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
            if (jObjResponse != null) {
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
            logger.error("Exception in retrieving data:" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "verify/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String verifyBadge(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in verifyBadge()");
                BadgeResponse badgeResponse = BadgeCommands.getBadgeResponseById(LDAPService.ldapEntryManager, id, utils, request);
                if (badgeResponse != null) {
                    return returnBadgeResponse(badgeResponse, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Please try after some time");
                logger.error("Error in connecting database in verifyBadge():");
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
            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in verifyPrivateBadge()");
                BadgeResponse badgeResponse = BadgeCommands.getBadgeResponseByIdAndKey(LDAPService.ldapEntryManager, id, key, utils, request);
                if (badgeResponse != null) {
                    return returnBadgeResponse(badgeResponse, response);
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Please try after some time");
                logger.error("Error in connecting database in verifyPrivateBadge():");
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

//    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadge(@PathVariable String id, @RequestParam(value = "key") String key, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
            if (redisTemplate.opsForValue().get(id + key) == null) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Oops!! Badge link expired");
                return jsonResponse.toString();
            }

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadge()");
                BadgeResponse badge = BadgeCommands.getBadgeResponseByIdAndKey(LDAPService.ldapEntryManager, id, key, utils, request);
                if (badge != null) {
                    jsonResponse.addProperty("error", false);
                    response.setStatus(HttpServletResponse.SC_OK);
                    return GsonService.getGson().toJson(badge);
                } else {
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Please try after some time");
                logger.error("Error in connecting database in getBadge():");
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
            if (userInfo == null) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            } else if (userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in getBadgeByBadgeRequestInum()");
                BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestForUserByInum(LDAPService.ldapEntryManager, badgeRequest.getBadgeRequestInum(), email);
                if (badgeRequests != null && badgeRequests.getInum() != null) {
                    BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(LDAPService.ldapEntryManager, badgeRequests.getInum());
                    if (badgeClass != null && badgeClass.getInum() != null) {
                        Badges badges = BadgeCommands.getBadgeByBadgeClassInum(LDAPService.ldapEntryManager, badgeClass.getInum());
                        if (badges != null && badges.getInum() != null) {

                            String tempURLBase = utils.getBaseURL(request);
                            String tempURL = tempURLBase + "/badges/" + badges.getGuid();

                            if (badges.getBadgePrivacy().equalsIgnoreCase("private")) {
                                setRedisData(badges.getKey(), badges.getId(), 95);
                                Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
                                calendar.add(Calendar.SECOND, 95);
                                badges.setExpires(calendar.getTime());
                                boolean isUpdated = BadgeCommands.updateBadge(LDAPService.ldapEntryManager, badges);
                                logger.info("Badge updated after expires set:" + isUpdated);
                                tempURL = tempURLBase + "/badges/" + badges.getGuid() + "?key=" + badges.getKey();
                            }

                            String redisKey = badges.getGuid() + badges.getKey();
                            setRedisData(redisKey, tempURL, 95);
                            final String id = Hashing.murmur3_32().hashString(tempURL, StandardCharsets.UTF_8).toString();
                            System.out.println("Shortern url id:" + id);
                            String shortenURL = tempURLBase + "/tmp/" + id;
                            System.out.println("Shortern url:" + shortenURL);
                            setRedisData(id, tempURL, 95);

                            if (generateQrCode(badges, badgeClass.getImage(), shortenURL, 250, "png")) {
                                logger.info("QR Code generated successfully");
                                DisplayBadge badge = new DisplayBadge();
                                badge.setQrCode(utils.getBaseURL(request) + File.separator + "images" + File.separator + badges.getImage());
                                badge.setBadgePublicURL(badges.getId());
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
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Please try after some time");
                logger.error("Error in connecting database in getBadgeByBadgeRequestInum():");
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
            if (userInfo == null) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            } else if (userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return jsonResponse.toString();
            }
            String email = userInfo.getEmail();

            if (LDAPService.isConnected()) {
                logger.info("LDAP connected in setBadgePrivacy()");
                BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestForUserByInum(LDAPService.ldapEntryManager, privacy.getBadgeRequestInum(), email);
                if (badgeRequests != null && badgeRequests.getInum() != null) {
                    BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(LDAPService.ldapEntryManager, badgeRequests.getInum());
                    if (badgeClass != null && badgeClass.getInum() != null) {
                        Badges badges = BadgeCommands.getBadgeByBadgeClassInum(LDAPService.ldapEntryManager, badgeClass.getInum());
                        if (badges != null && badges.getInum() != null) {

                            badges.setBadgePrivacy(privacy.getPrivacy());

                            if (badges.getBadgePrivacy().equalsIgnoreCase("public")) {
                                badges.setId(utils.getBaseURL(request) + "/badges/verify/" + badges.getGuid());
                            } else if (badges.getBadgePrivacy().equalsIgnoreCase("private")) {
                                badges.setId(utils.getBaseURL(request) + "/badges/verify/" + badges.getGuid() + "?key=" + badges.getKey());
                            } else {
                                response.setStatus(HttpServletResponse.SC_OK);
                                jsonResponse.addProperty("error", true);
                                jsonResponse.addProperty("errorMsg", "Failed to set badge privacy");
                                return jsonResponse.toString();
                            }

                            boolean isUpdated = BadgeCommands.updateBadge(LDAPService.ldapEntryManager, badges);
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
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Please try after some time");
                logger.error("Error in connecting database in setBadgePrivacy():");
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
            logger.info("Logo file url:" + logoFileURL);

            //logo file
//            String logoFileName = + System.currentTimeMillis() + ".png";
////            logoFileURL = logoFileURL.replace("127.0.0.1", "192.168.200.79");
//            //Server
//            String logoPath = utils.getStaticResourceLogoPath(context);
//            //Local
////            String logoPath = "src/main/resources/static/logo";
//            System.out.println("logo file path :" + logoPath);
//            if (new File(logoPath).exists()) {
//                System.out.println("Directory exists:" + logoPath);
//                FileUtils.cleanDirectory(new File(logoPath));
//            }
//            String logoFilePath = logoPath + File.separator + logoFileName;
//            System.out.println("logo file path:" + logoFilePath);
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
            System.out.println("path :" + imagesPath);
            if (new File(imagesPath).exists()) {
                System.out.println("Directory exists:" + imagesPath);
                FileUtils.cleanDirectory(new File(imagesPath));
            }
            String filePath = imagesPath + File.separator + fileName;
            System.out.println("file path:" + filePath);

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