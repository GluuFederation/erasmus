package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeClassesCommands;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.model.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/badgeClass/")
public class BadgeClassController {

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeClass(@PathVariable String id, @RequestParam(value="key") String key, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        if (LDAPService.isConnected()) {
            try {
                BadgeClassResponse badge = BadgeClassesCommands.getBadgeClassResponseById(LDAPService.ldapEntryManager, id, key);
                return returnResponse(badge,response,"No such badge found");
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

    private String returnResponse(BadgeClassResponse badge, HttpServletResponse response, String errorMsg){
        JsonObject jsonResponse = new JsonObject();
        if (badge != null) {
            jsonResponse.addProperty("error", false);
            response.setStatus(HttpServletResponse.SC_OK);
            return GsonService.getGson().toJson(badge);
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", errorMsg);
            return jsonResponse.toString();
        }
    }
}