package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/organizations")
public class OrganizationController  {

    //@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getOrganizations(HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

            final String uri = Global.API_ENDPOINT + Global.getAllOrganizations;

            DisableSSLCertificateCheckUtil.disableChecks();
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            JsonArray jArrayResponse = new JsonParser().parse(result).getAsJsonArray();
            if (jArrayResponse.size() > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.add("organizations", GsonService.getGson().toJsonTree(jArrayResponse));
                jsonResponse.addProperty("error", false);
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No organizations found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }
}