package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeCommands;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.model.BadgeResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Arvind Tomar on 1/5/17.
 */
@CrossOrigin
@RestController
@Api(basePath = "/tmp", description = "temp url apis")
@RequestMapping("/tmp")
public class TempURLController {

    @Autowired
    public RedisTemplate<Object, Object> redisTemplate;

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String redirect(@PathVariable String id, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {

            if (redisTemplate.opsForValue().get(id) == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Oops!! Badge link expired");
                return jsonResponse.toString();
            }

            final String url = String.valueOf(redisTemplate.opsForValue().get(id));
            if (url != null)
                response.sendRedirect(url);
            else
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
