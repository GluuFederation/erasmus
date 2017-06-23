package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.bind.annotation.*;
import org.xdi.oxd.badgemanager.service.RedisService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Arvind Tomar on 1/5/17.
 */
@CrossOrigin
@RestController
@Api(basePath = "/tmp", description = "temp url apis")
@RequestMapping("/tmp")
@ContextConfiguration(classes = RedisService.class, loader = AnnotationConfigContextLoader.class)
@ComponentScan({"org.xdi.oxd.badgemanager.service","org.xdi.oxd.badgemanager.service","org.xdi.oxd.badgemanager.web"})
public class TempURLController {

    private static final Logger logger = LoggerFactory.getLogger(TempURLController.class);
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    @Inject
    private BadgeController badgeController;

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String redirect(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

            System.out.println("id---------------------------------------"+id);
            if (redisTemplate.opsForValue().get(id) == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "Oops!! Badge expired");
                return jsonResponse.toString();
            }

            final String url = String.valueOf(redisTemplate.opsForValue().get(id));
            if (url != null) {
                String strId, strKey;
                String[] arURL = url.split("/");
                if (arURL.length > 0) {
                    String strURL = arURL[arURL.length - 1];
                    if (strURL.indexOf("key") > 0) {
                        //private
                        String[] arURL1 = strURL.split("\\?key=");
                        if (arURL1.length > 0) {
                            strId = arURL1[0];
                            strKey = arURL1[1];
                            return badgeController.verifyPrivateBadge(strId, strKey, request, response);
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            jsonResponse.addProperty("error", true);
                            jsonResponse.addProperty("errorMsg", "No such badge found");
                            return jsonResponse.toString();
                        }
                    } else {
                        strId = strURL;
                        //public
                        return badgeController.verifyBadge(strId, response);
                    }
                } else {
                    //no badge found
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    jsonResponse.addProperty("error", true);
                    jsonResponse.addProperty("errorMsg", "No such badge found");
                    return jsonResponse.toString();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No such badge found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "No such badge found");
            logger.error("Exception in retrieving badge using temp url: " + e.getMessage());
            return jsonResponse.toString();
        }
    }
}
