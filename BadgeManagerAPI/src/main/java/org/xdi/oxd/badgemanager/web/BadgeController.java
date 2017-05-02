package org.xdi.oxd.badgemanager.web;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.xdi.oxd.badgemanager.qrcode.decorator.ColoredQRCode.colorizeQRCode;
import static org.xdi.oxd.badgemanager.qrcode.decorator.ImageOverlay.addImageOverlay;

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

    @RequestMapping(value = "templates/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTemplateBadgesByParticipant(@RequestParam String accessToken, @RequestParam String type, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

//            retrieve user info
            UserInfo userInfo = userInfoService.getUserInfo(accessToken);
            if (userInfo != null) {
                String issuer = userInfo.getIssuer();
            }

            String issuer = "https://ce-dev2.gluu.org";

            IssuerBadgeRequest issuerBadgeRequest = new IssuerBadgeRequest(issuer, type);

            final String uri = Global.API_ENDPOINT + Global.getTemplateBadgesByParticipant;

            DisableSSLCertificateCheckUtil.disableChecks();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + Global.Request_AccessToken);

            HttpEntity<IssuerBadgeRequest> request = new HttpEntity<IssuerBadgeRequest>(issuerBadgeRequest, headers);

            HttpEntity<String> strResponse = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            String result = strResponse.getBody();

            JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();
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
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "verify/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String verifyBadge(@PathVariable String id, @RequestParam(value = "key", required = false) String key, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        if (LDAPService.isConnected()) {
            try {
                Badges badge = BadgeCommands.getBadgeById(LDAPService.ldapEntryManager, id, utils, request);
                if (badge != null && badge.getInum() != null) {
                    if (badge.getBadgePrivacy().equalsIgnoreCase("public")) {
                        BadgeResponse badgeResponse = BadgeCommands.getBadgeResponseById(LDAPService.ldapEntryManager, id, utils, request);
                        return returnBadgeResponse(badgeResponse, response, "No such badge found");
                    } else if (badge.getBadgePrivacy().equalsIgnoreCase("private")) {
                        if (key != null && key.length() > 0) {
                            if (redisTemplate.opsForValue().get(key) == null) {
                                jsonResponse.addProperty("error", true);
                                jsonResponse.addProperty("errorMsg", "Oops!! Badge expired");
                                return jsonResponse.toString();
                            } else {
                                BadgeResponse badgeResponse = BadgeCommands.getBadgeResponseByIdAndKey(LDAPService.ldapEntryManager, id, key, utils, request);
                                return returnBadgeResponse(badgeResponse, response, "No such badge found");
                            }
                        } else {
                            response.setStatus(HttpServletResponse.SC_CONFLICT);
                            jsonResponse.addProperty("error", true);
                            jsonResponse.addProperty("errorMsg", "Oops! Unauthorized access");
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
                jsonResponse.addProperty("error", e.getMessage());
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadge(@PathVariable String id, @RequestParam(value = "key") String key, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        if (redisTemplate.opsForValue().get(id + key) == null) {
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Oops!! Badge link expired");
            return jsonResponse.toString();
        }

        if (LDAPService.connected) {
            try {
                BadgeResponse badge = BadgeCommands.getBadgeResponseByIdAndKey(LDAPService.ldapEntryManager, id, key, utils, request);
                if (badge != null) {
                    jsonResponse.addProperty("error", false);
                    response.setStatus(HttpServletResponse.SC_OK);
                    return GsonService.getGson().toJson(badge);
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.addProperty("error", e.getMessage());
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "details/{badgeRequestInum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeByBadgeRequestInum(@PathVariable String badgeRequestInum, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        if (LDAPService.isConnected()) {
            try {

                BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestByInum(LDAPService.ldapEntryManager, badgeRequestInum);
                if (badgeRequests != null && badgeRequests.getInum() != null) {
                    BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(LDAPService.ldapEntryManager, badgeRequests.getInum());
                    if (badgeClass != null && badgeClass.getInum() != null) {
                        Badges badges = BadgeCommands.getBadgeByBadgeClassInum(LDAPService.ldapEntryManager, badgeClass.getInum());
                        if (badges != null && badges.getInum() != null) {

                            if (badges.getBadgePrivacy().equalsIgnoreCase("private")) {
                                setRedisData(badges.getKey(), badges.getId(), 95);
                            }

                            String tempURLBase = utils.getBaseURL(request);
                            String tempURL = tempURLBase + "/badges/" + badges.getGuid() + "?key=" + badges.getKey();
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
                                System.out.println("Current time:" + calendar.getTime().toString());
                                calendar.add(Calendar.SECOND, 95);
                                System.out.println("Expire time:" + calendar.getTime().toString());
                                badge.setExpiresAt(calendar.getTime().toString());

                                Recipient recipient = new Recipient();
                                recipient.setType(badges.getRecipientType());
                                recipient.setIdentity(badges.getRecipientIdentity());
                                badge.setRecipient(recipient);

                                response.setStatus(HttpServletResponse.SC_OK);
                                return GsonService.getGson().toJson(badge);
                            } else {
                                logger.error("Failed to generate QR Code");
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
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Please try after some time");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "setPrivacy/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String setBadgePrivacy(@RequestParam String badgeRequestInum, @RequestParam String privacy, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        if (LDAPService.isConnected()) {
            try {
                BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestByInum(LDAPService.ldapEntryManager, badgeRequestInum);
                if (badgeRequests != null && badgeRequests.getInum() != null) {
                    BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByBadgeRequestInum(LDAPService.ldapEntryManager, badgeRequests.getInum());
                    if (badgeClass != null && badgeClass.getInum() != null) {
                        Badges badges = BadgeCommands.getBadgeByBadgeClassInum(LDAPService.ldapEntryManager, badgeClass.getInum());
                        if (badges != null && badges.getInum() != null) {

                            badges.setBadgePrivacy(privacy);

                            if (badges.getBadgePrivacy().equalsIgnoreCase("public")) {
                                badges.setId(utils.getBaseURL(request) + "/badges/verify/" + badges.getGuid());
                            } else if (badges.getBadgePrivacy().equalsIgnoreCase("private")) {
                                badges.setId(utils.getBaseURL(request) + "/badges/verify/" + badges.getGuid() + "?key=" + badges.getKey());
                            }

                            boolean isUpdated = BadgeCommands.updateBadge(LDAPService.ldapEntryManager, badges);
                            if(isUpdated){
                                response.setStatus(HttpServletResponse.SC_OK);
                                jsonResponse.addProperty("error", false);
                                jsonResponse.addProperty("errorMsg", "Badge privacy set successfully");
                                return jsonResponse.toString();
                            } else {
                                response.setStatus(HttpServletResponse.SC_OK);
                                jsonResponse.addProperty("error", true);
                                jsonResponse.addProperty("errorMsg", "Failed to set badge privacy");
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
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
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
//            String imagesPath = utils.getStaticResourcePath(context);
            //Local
            String imagesPath = "src/main/resources/static/images";
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

    private String returnBadgeResponse(BadgeResponse badgeResponse, HttpServletResponse response, String errorMsg) {
        JsonObject jsonResponse = new JsonObject();
        if (badgeResponse != null) {
            jsonResponse.addProperty("error", false);
            response.setStatus(HttpServletResponse.SC_OK);
            return GsonService.getGson().toJson(badgeResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", errorMsg);
            return jsonResponse.toString();
        }
    }
}