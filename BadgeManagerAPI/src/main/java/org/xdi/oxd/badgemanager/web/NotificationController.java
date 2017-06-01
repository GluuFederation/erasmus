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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xdi.oxd.badgemanager.push.AndroidPushNotificationsService;
import org.xdi.oxd.badgemanager.push.FirebaseResponse;

import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value = "/send", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String sendNotification(HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {
            ResponseEntity<String> responseEntity = send();
            if (responseEntity != null && responseEntity.getStatusCode()==HttpStatus.OK) {
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
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Failed to send notification");
            logger.error("Exception in sending notification:" + e.getMessage());
            return jsonResponse.toString();
        }
    }

    private ResponseEntity<String> send() {

        JsonObject body = new JsonObject();
        // JsonArray registration_ids = new JsonArray();
        // body.put("registration_ids", registration_ids);
        body.addProperty("to", "e_ZEo4DHlqE:APA91bFo6BDgrQQzU4xkHpAgiLDhVlMOq1ivpjNh0X8tViqNuxZnVjKHLjgG0uMqZYsup1_MU_VkOzDO-nplW7QNa9wlYaoB_eoj3-Xdt-QCeIXOgCgoDdzeT1mnOEhBZoX00FzCKBkO");
        body.addProperty("priority", "high");
        // body.put("dry_run", true);

        JsonObject notification = new JsonObject();
        notification.addProperty("body", "Someone from ___ organization is requesting to see your badge");
        notification.addProperty("title", "ERASMUS Notification");
        // notification.put("icon", "myicon");

        JsonObject data = new JsonObject();
        data.addProperty("key1", "value1");
        data.addProperty("key2", "value2");

        body.add("notification", notification);
        body.add("data", data);

        HttpEntity<String> request = new HttpEntity<>(body.toString());

        CompletableFuture<FirebaseResponse> pushNotification = androidPushNotificationsService.send(request);
        CompletableFuture.allOf(pushNotification).join();

        try {
            FirebaseResponse firebaseResponse = pushNotification.get();
            if (firebaseResponse.getSuccess() == 1) {
                logger.info("push notification sent ok!");
            } else {
                logger.error("error sending push notifications: " + firebaseResponse.toString());
            }
            return new ResponseEntity<>(firebaseResponse.toString(), HttpStatus.OK);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("the push notification cannot be send.", HttpStatus.BAD_REQUEST);
    }
}
