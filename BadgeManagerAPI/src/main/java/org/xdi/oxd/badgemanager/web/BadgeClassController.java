package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeClassesCommands;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.model.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/badgeClass/")
public class BadgeClassController {

    private static final Logger logger = LoggerFactory.getLogger(BadgeClassController.class);

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeClass(@PathVariable String id, @RequestParam(value = "key") String key, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
            BadgeClassResponse badge = BadgeClassesCommands.getBadgeClassResponseById(id, key);
            return returnResponse(badge, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", ex.getMessage());
            logger.error("Exception in retrieving badge class in getBadgeClass(): " + ex.getMessage());
            return jsonResponse.toString();
        }
    }

    private String returnResponse(BadgeClassResponse badge, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();
        if (badge != null) {
            jsonResponse.addProperty("error", false);
            response.setStatus(HttpServletResponse.SC_OK);
            return GsonService.getGson().toJson(badge);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "No such badge found");
            return jsonResponse.toString();
        }
    }
}