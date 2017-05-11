package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.model.Participant;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantController.class);

    @RequestMapping(value = "/{state:.+}/{city:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getParticipants(@PathVariable String state, @PathVariable String city, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

            final String uri = Global.API_ENDPOINT + Global.participant;

            DisableSSLCertificateCheckUtil.disableChecks();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + Global.Request_AccessToken);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                    .queryParam("state", state)
                    .queryParam("city", city);

            HttpEntity<String> request = new HttpEntity<>(headers);

            HttpEntity<String> resp = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, String.class);

            String result = resp.getBody();

            JsonArray jArrayResponse = new JsonParser().parse(result).getAsJsonArray();

            if (jArrayResponse.size() > 0) {
                List<Participant> lstParticipant = new ArrayList<>();
                for (int i = 0; i < jArrayResponse.size(); i++) {
                    JsonObject jObj = jArrayResponse.get(i).getAsJsonObject();
                    if (jObj != null) {
                        Participant obj = new Participant();
                        obj.setId(GsonService.getValueFromJson("_id", jObj));
                        obj.setContext(GsonService.getValueFromJson("@context", jObj));
                        obj.setOpHost(GsonService.getValueFromJson("discoveryUrl", jObj));
                        obj.setName(GsonService.getValueFromJson("name", jObj));
                        obj.setPhoneNo(GsonService.getValueFromJson("phoneNo", jObj));
                        obj.setAddress(GsonService.getValueFromJson("address", jObj));
                        obj.setZipcode(GsonService.getValueFromJson("zipcode", jObj));
                        obj.setCity(GsonService.getValueFromJson("city", jObj));
                        obj.setState(GsonService.getValueFromJson("state", jObj));
                        obj.setType(GsonService.getValueFromJson("type", jObj));

                        lstParticipant.add(obj);
                    }
                }

                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.add("participants", GsonService.getGson().toJsonTree(lstParticipant));
                jsonResponse.addProperty("error", false);
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No participants found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", true);
            jsonResponse.addProperty("errorMsg", "Please try after some time");
            logger.error("Exception in retrieving participant in getParticipants:"+e.getMessage());
            return jsonResponse.toString();
        }
    }
}