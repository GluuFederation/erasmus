package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.LDAPInitializer;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeInstancesCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeRequestCommands;
import org.xdi.oxd.badgemanager.ldap.models.BadgeInstances;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.model.ApproveBadge;
import org.xdi.oxd.badgemanager.qrcode.QRCBuilder;
import org.xdi.oxd.badgemanager.qrcode.ZXingQRCodeBuilder;
import org.xdi.oxd.badgemanager.storage.StorageProperties;
import org.xdi.oxd.badgemanager.storage.StorageService;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

import static org.xdi.oxd.badgemanager.qrcode.decorator.ColoredQRCode.colorizeQRCode;
import static org.xdi.oxd.badgemanager.qrcode.decorator.ImageOverlay.addImageOverlay;

/**
 * Created by Arvind Tomar on 14/10/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/badges/request")
public class BadgeRequestController {

    //    private final StorageService storageService;
    private final ServletContext context;
//
//    @Autowired
//    public BadgeRequestController(StorageService storageService) {
//        this.storageService = storageService;
//    }

    @Autowired
    public BadgeRequestController(ServletContext context) {
        this.context = context;
    }

    boolean isConnected = false;
    LdapEntryManager ldapEntryManager;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createBadgeRequest(@RequestBody BadgeRequests badgeRequest, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
//        Static
//        try {
//            badgeRequest = BadgeRequestCommands.createBadgeRequest(ldapEntryManager, badgeRequest);
//            jsonResponse.add("badgeRequest", GsonService.getGson().toJsonTree(badgeRequest));
//            jsonResponse.addProperty("error", false);
//            response.setStatus(HttpServletResponse.SC_OK);
//            return jsonResponse.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            jsonResponse.addProperty("error", true);
//            jsonResponse.addProperty("errorMsg", e.getMessage());
//            return jsonResponse.toString();
//        }

        //Dynamic
        LDAPInitializer ldapInitializer = new LDAPInitializer((isConnected, ldapEntryManager) -> {
            this.isConnected = isConnected;
            this.ldapEntryManager = ldapEntryManager;
        });

        if (isConnected) {
            try {
                badgeRequest = BadgeRequestCommands.createBadgeRequest(ldapEntryManager, badgeRequest);
                jsonResponse.add("badgeRequest", GsonService.getGson().toJsonTree(badgeRequest));
                jsonResponse.addProperty("error", false);
                response.setStatus(HttpServletResponse.SC_OK);
                return jsonResponse.toString();
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", e.getMessage());
                System.out.print("Exception is adding badge request entry:" + e.getMessage());
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            System.out.print("Problem in connecting database:");
            return jsonResponse.toString();
        }
    }

    @RequestMapping(value = "listPending/{id:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPendingBadgeRequestsByParticipant(@PathVariable String id, HttpServletResponse response) {

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
        LDAPInitializer ldapInitializer = new LDAPInitializer((isConnected, ldapEntryManager) -> {
            this.isConnected = isConnected;
            this.ldapEntryManager = ldapEntryManager;
        });

        if (isConnected) {
            try {
                List<BadgeRequests> lstBadgeRequests = BadgeRequestCommands.getPendingBadgeRequests(ldapEntryManager, id, "Pending");
                jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(lstBadgeRequests));
                jsonResponse.addProperty("error", false);
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
    public String approveBadgeRequest(@RequestBody ApproveBadge approveBadge, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        //Static
//        try {
//            jsonResponse.addProperty("responseMsg", "Badge request approved successfully");
//            jsonResponse.addProperty("error", false);
//            response.setStatus(HttpServletResponse.SC_OK);
//            return jsonResponse.toString();
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            jsonResponse.addProperty("error", true);
//            jsonResponse.addProperty("responseMsg", e.getMessage());
//            return jsonResponse.toString();
//        }

        //Dynamic
        LDAPInitializer ldapInitializer = new LDAPInitializer((isConnected, ldapEntryManager) -> {
            this.isConnected = isConnected;
            this.ldapEntryManager = ldapEntryManager;
        });

        if (isConnected) {
            boolean isUpdated;
            try {

                BadgeRequests badgeRequest = new BadgeRequests();
                badgeRequest.setInum(approveBadge.getInum());
                badgeRequest.setStatus("Approved");
                badgeRequest.setValidity(approveBadge.getValidity());

                isUpdated = BadgeRequestCommands.updateBadgeRequest(ldapEntryManager, badgeRequest);
                if (isUpdated) {
                    jsonResponse.addProperty("responseMsg", "Badge request approved successfully");
                    createBadgeInstance(approveBadge.getInum());
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

    @RequestMapping(value = "listApproved/{accessToken:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getApprovedBadgeRequests(@PathVariable String accessToken, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        //Static
//        List<BadgeRequests> lstBadgeRequests=new ArrayList<>();
//        BadgeRequests obj1=new BadgeRequests();
//        obj1.setInum("@!4301.2A50.9A09.7688!1002!BA48.6F40");
//        obj1.setTemplateBadgeId("58dfa009a016c8832d9b7ea9");
//        obj1.setTemplateBadgeTitle("Emergency Medical Technician-Basic");
//        obj1.setParticipant("58e1dfaf159139ee277d7ab7");
//        obj1.setStatus("Approved");
//        obj1.setGluuBadgeRequester("test@test.com");
//        obj1.setDn("inum=@!4301.2A50.9A09.7688!1002!BA48.6F40,ou=badgeRequests,ou=badges,o=@!C460.F7DA.F3E9.4A62!0001!5EE3.2D5C,o=gluu");
//
//        BadgeRequests obj2=new BadgeRequests();
//        obj2.setInum("@!4301.2A50.9A09.7688!1002!D79C.9514");
//        obj2.setTemplateBadgeId("58dfa009a016c8832d9b7ea9");
//        obj2.setTemplateBadgeTitle("Entry-Level Firefighter");
//        obj2.setParticipant("58e1dfaf159139ee277d7ab7");
//        obj2.setStatus("Approved");
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
        LDAPInitializer ldapInitializer = new LDAPInitializer((isConnected, ldapEntryManager) -> {
            this.isConnected = isConnected;
            this.ldapEntryManager = ldapEntryManager;
        });

        if (isConnected) {
            try {
                String email = accessToken;
                List<BadgeRequests> lstBadgeRequests = BadgeRequestCommands.getApprovedBadgeRequests(ldapEntryManager, email, "Approved");
                jsonResponse.add("badgeRequests", GsonService.getGson().toJsonTree(lstBadgeRequests));
                jsonResponse.addProperty("error", false);
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

    private boolean createBadgeInstance(String inum) {
        try {
            BadgeRequests objBadgeRequest = BadgeRequestCommands.getBadgeRequestByInum(ldapEntryManager, inum);
            if (objBadgeRequest != null) {

                final String uri = Global.API_ENDPOINT + Global.getTemplateBadgeById + "/" + objBadgeRequest.getTemplateBadgeId();

                DisableSSLCertificateCheckUtil.disableChecks();
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.getForObject(uri, String.class);

                JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();
                if (jObjResponse != null) {
                    if (jObjResponse.has("message") && jObjResponse.get("message").getAsString().equalsIgnoreCase("Badge not found")) {
                        System.out.print("Unable to insert badge instance entry. reason is:" + jObjResponse.get("message").getAsString());
                        return false;
                    }

                    BadgeInstances objBadgeInstance = new BadgeInstances();
                    objBadgeInstance.setTemplateBadgeId(jObjResponse.get("_id").getAsString());
                    objBadgeInstance.setName(jObjResponse.get("name").getAsString());
                    objBadgeInstance.setType("BadgeClass");
                    objBadgeInstance.setDescription(jObjResponse.get("description").getAsString());
                    objBadgeInstance.setBadgeRequestInum(objBadgeRequest.getInum());

//                    if (createQrCode(objBadgeInstance, jObjResponse.get("image").getAsString(), 250, "png")) {
//                        System.out.print("QR Code generated successfully");
//                    } else {
//                        System.out.print("Failed to generate QR Code");
//                    }

                    return BadgeInstancesCommands.createNewBadgeInstances(ldapEntryManager, objBadgeInstance);
                }
            }
        } catch (Exception ex) {
            System.out.print("Exception in insert badge instance entry:" + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Call this method to create a QR-code image. You must provide the
     * OutputStream where the image data can be written.
     *
     * @param badge       The  badge object from which content should be encoded with the QR-code.
     * @param qrCodeSize  The QR-code must be quadratic. So this is the number of pixel
     *                    in width and height.
     * @param imageFormat The image format in which the image should be rendered. As
     *                    Example 'png' or 'jpg'. See @javax.imageio.ImageIO for more
     *                    information which image formats are supported.
     * @throws Exception If an Exception occur during the create of the QR-code or
     *                   while writing the data into the OutputStream.
     */
    private boolean createQrCode(BadgeInstances badge, String logoFileName, int qrCodeSize, String imageFormat) {
        try {

            float TRANSPARENCY = 0.75f;
            float OVERLAY_RATIO = 0.25f;

            System.out.print("Context real path: " + context.getRealPath(""));
            System.out.print("WEB-INF img path: " + context.getRealPath("/WEB-INF/classes/img/"));

//            String location = new StorageProperties().getLocation();
            String location = context.getRealPath("/WEB-INF/classes/img/");

            String fileName = location + File.separator + "badges" +
                    File.separator + "qrcodes" + File.separator + System.currentTimeMillis() + ".png";

//            String logoFileName = "src/main/resources" + badge.getPicture();
//            File logoFile = new File(logoFileName);
//            FileInputStream in = new FileInputStream(logoFile);
//            byte[] content = new byte[(int) logoFile.length()];
//            in.read(content);

//            if (!logoFile.exists()) {
//                return false;
//            }

            QRCBuilder<BufferedImage> qrCodeBuilder = new ZXingQRCodeBuilder();

            qrCodeBuilder.newQRCode()
                    .withSize(qrCodeSize, qrCodeSize)
                    .and()
                    .withData(badge.getDescription())
                    .and()
                    .decorate(colorizeQRCode(new Color(51, 102, 153)))
                    .and()
                    .decorate(addImageOverlay(ImageIO.read(new URL(logoFileName)), TRANSPARENCY, OVERLAY_RATIO))
                    .and()
                    .doVerify(true)
                    .toFile(fileName, imageFormat);

            String[] arFilePath = fileName.split("/resources");
            if (arFilePath.length > 0) {
                String filePath = arFilePath[arFilePath.length - 1];
                badge.setImage(filePath);
            }

            return true;
        } catch (Exception ex) {
            System.out.println("Exception in generating qr code: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
}