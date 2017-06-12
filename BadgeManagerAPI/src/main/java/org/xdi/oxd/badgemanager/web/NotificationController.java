package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xdi.oxauth.model.fido.u2f.protocol.DeviceData;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeClassesCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeRequestCommands;
import org.xdi.oxd.badgemanager.ldap.commands.PersonCommands;
import org.xdi.oxd.badgemanager.ldap.models.BadgeClass;
import org.xdi.oxd.badgemanager.ldap.models.BadgeRequests;
import org.xdi.oxd.badgemanager.ldap.models.Badges;
import org.xdi.oxd.badgemanager.ldap.models.Person;
import org.xdi.oxd.badgemanager.ldap.models.fido.u2f.DeviceRegistration;
import org.xdi.oxd.badgemanager.model.*;
import org.xdi.oxd.badgemanager.push.AndroidPushNotificationsService;
import org.xdi.oxd.badgemanager.push.FirebaseResponse;
import org.xdi.oxd.badgemanager.service.UserInfoService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Arvind Tomar on 31/5/17.
 */
@CrossOrigin
@RestController
@Api(basePath = "/notification", description = "push notification apis")
@RequestMapping("/notification")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    @Inject
    private UserInfoService userInfoService;

    @Inject
    public BadgeController badgeController;

    @Inject
    public ParticipantController participantController;

    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String sendNotification(@RequestHeader(value = "AccessToken") String accessToken, @RequestBody NotificationRequest notificationRequest, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

            if (accessToken == null || accessToken.length() == 0 || notificationRequest == null
                    || notificationRequest.getBadge() == null || notificationRequest.getBadge().length() == 0
                    || notificationRequest.getOpHost() == null || notificationRequest.getOpHost().length() == 0
                    || notificationRequest.getParticipant() == null || notificationRequest.getParticipant().length() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request.Missing required derails.");
                return jsonResponse.toString();
            }

            UserInfo userInfo = userInfoService.getUserInfo(notificationRequest.getOpHost(), accessToken);
            if (userInfo == null || userInfo.getEmail() == null || userInfo.getEmail().equalsIgnoreCase("")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Invalid user");
                return jsonResponse.toString();
            }

            Badges badges = BadgeCommands.getBadgeById(notificationRequest.getBadge());
            if (badges == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. No such badge");
                return jsonResponse.toString();
            }

            BadgeClass badgeClass = BadgeClassesCommands.getBadgeClassByInum(badges.getBadgeClassInum());
            BadgeRequests badgeRequests = BadgeRequestCommands.getBadgeRequestByInum(badgeClass.getBadgeRequestInum());

            Person person = PersonCommands.getPersonByEmail(badgeRequests.getGluuBadgeRequester());
            if (person == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Asserter entry not found");
                return jsonResponse.toString();
            }
            List<DeviceRegistration> deviceRegistrations = PersonCommands.getDeviceRegistrationByPerson(person.getInum());
            logger.info("Device registration counts: " + deviceRegistrations.size());
            if (deviceRegistrations.size() == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Device entry not found");
                return jsonResponse.toString();
            }
            DeviceRegistration deviceRegistration = deviceRegistrations.get(0);
            logger.info("Device data: " + deviceRegistration.getDeviceData());
            DeviceData deviceData = deviceRegistration.getDeviceData();
            if(!deviceData.getPlatform().equalsIgnoreCase("android")){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "You're not authorized to perform this request. Device entry not found");
                return jsonResponse.toString();
            }

            String fromEmail = userInfo.getEmail();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Validator", fromEmail);
            jsonObject.addProperty("Asserter", badgeRequests.getGluuBadgeRequester());
            jsonObject.addProperty("Badge", badges.getGuid());

            badgeController.setRedisData(badges.getGuid(), jsonObject.toString(), 180);

//            static token for testing
//            String toDeviceToken = "e_ZEo4DHlqE:APA91bFo6BDgrQQzU4xkHpAgiLDhVlMOq1ivpjNh0X8tViqNuxZnVjKHLjgG0uMqZYsup1_MU_VkOzDO-nplW7QNa9wlYaoB_eoj3-Xdt-QCeIXOgCgoDdzeT1mnOEhBZoX00FzCKBkO";

            String toDeviceToken = deviceData.getPushToken();
            String msg = "Someone from " + notificationRequest.getParticipant() + " is requesting to see your badge";

            JsonObject data = new JsonObject();
            data.addProperty("fromEmail", fromEmail);
            data.addProperty("badge", badges.getGuid());
            data.addProperty("badgeTitle", badgeRequests.getTemplateBadgeTitle());
            data.addProperty("notifyType", 1);

            return notificationResponse(send(msg, toDeviceToken, data), jsonResponse, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Failed to send notification");
            logger.error("Exception in sending notification:" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    public ResponseEntity<String> send(String msg, String toDeviceToken, JsonObject data) {

        JsonObject body = new JsonObject();
        // JsonArray registration_ids = new JsonArray();
        // body.put("registration_ids", registration_ids);
//        body.addProperty("to", "Q3kZZChDwk:APA91bFsSaCe16Mmf3uWk4SuS0pv_1Hk0SGwrpZYCs86RADmcz_Mm4Jn-ysqiuuEhcFYfUndhMY-B1tFoV0ro0ttg03GAw_K2iAOIxGGcPA2F8RLszFSRh9GbLwSif01afyAkg191CoR");
//        body.addProperty("to", "fpnEyeODo_k:APA91bEl_CM4_VSkz4Ss4RXUvxZd6KkYnBQs3NHzd6F8zvDr9qrnak9cJcQdEeFqFy32o0-tIoT3N7P8KcRz2PfrbOUO9HyHqLLZVOg7qL59r6IF9sRLolwxvEND45jlWgiMVwNn_SGJ");
        body.addProperty("to", toDeviceToken);
        body.addProperty("priority", "high");
        // body.put("dry_run", true);

        JsonObject notification = new JsonObject();
        notification.addProperty("body", msg);
        notification.addProperty("title", "ERASMUS Notification");
        // notification.put("icon", "myicon");

        body.add("notification", notification);
        body.add("data", data);

        HttpEntity<String> request = new HttpEntity<>(body.toString());

        CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            FirebaseResponse firebaseResponse = pushNotification.get();
            if (firebaseResponse.getSuccess() == 1) {
                logger.info("push notification sent ok! to "+ toDeviceToken);
                return new ResponseEntity<>(firebaseResponse.toString(), HttpStatus.OK);
            } else {
                logger.error("error sending push notifications: " + firebaseResponse.toString()+" to "+ toDeviceToken);
                return new ResponseEntity<>(firebaseResponse.toString(), HttpStatus.CONFLICT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("the push notification cannot be send.", HttpStatus.BAD_REQUEST);
    }

    private String notificationResponse(ResponseEntity<String> responseEntity, JsonObject jsonResponse, HttpServletResponse response) {
        if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.addProperty("error", false);
            jsonResponse.addProperty("message", "Notification sent successfully");
            return jsonResponse.toString();
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Failed to send notification");
            return jsonResponse.toString();
        }
    }
}
