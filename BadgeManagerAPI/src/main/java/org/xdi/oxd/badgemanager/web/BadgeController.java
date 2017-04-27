package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeCommands;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.ldap.service.LDAPService;
import org.xdi.oxd.badgemanager.model.*;
import org.xdi.oxd.badgemanager.service.UserInfoService;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;
import org.xdi.oxd.badgemanager.util.Utils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@Api(basePath = "/badges", description = "badges apis")
@RequestMapping("/badges")
public class BadgeController {

    @Autowired
    public RedisTemplate<Object, Object> redisTemplate;

    @Inject
    private UserInfoService userInfoService;

    @Inject
    private Utils utils;

    @RequestMapping(value = "listTemplateBadges/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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

    //    @RequestMapping(value = "/{inum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeByInum(@PathVariable String inum, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        //Static

        //Dynamic
        if (LDAPService.isConnected()) {
            try {
                BadgeResponse objBadge = BadgeCommands.getBadgeResponseByInum(LDAPService.ldapEntryManager, inum, utils, request);
                if (objBadge != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return GsonService.getGson().toJson(objBadge);
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    jsonResponse.addProperty("error", "No such badge found");
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

    @RequestMapping(value = "verify/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String verifyBadge(@PathVariable String id, @RequestParam(value = "key") String key, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        //Dynamic
        if (LDAPService.isConnected()) {
            try {
                BadgeResponse badge = BadgeCommands.getBadgeResponseById(LDAPService.ldapEntryManager, id, key, utils, request);
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

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadge(@PathVariable String id, @RequestParam(value = "key") String key, HttpServletRequest request, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        if (redisTemplate.opsForValue().get(id + key) == null) {
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Oops!! Badge link expired");
            return jsonResponse.toString();
        }

        //Dynamic
        if (LDAPService.connected) {
            try {
                BadgeResponse badge = BadgeCommands.getBadgeResponseById(LDAPService.ldapEntryManager, id, key, utils, request);
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
}