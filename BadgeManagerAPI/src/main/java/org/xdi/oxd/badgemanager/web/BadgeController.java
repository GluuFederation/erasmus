package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;
import org.xdi.oxd.badgemanager.model.IssuerBadgeRequest;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;
import org.xdi.oxd.badgemanager.util.Utils;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@Api(basePath = "/badges", description = "badges apis")
@RequestMapping("/badges")
public class BadgeController  {

    @RequestMapping(value = "listTemplateBadges/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTemplateBadgesByParticipant(@RequestParam String accessToken, @RequestParam String type, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {

            //static
//            String result="{\"Firefighter\":[{\"_id\":\"58dfa009a016c8832d9b7ea9\",\"updatedAt\":\"2017-04-03T10:44:17.542Z\",\"createdAt\":\"2017-04-01T12:41:45.972Z\",\"name\":\"Emergency Medical Technician-Basic\",\"description\":\"EMT-Basic training requires about 100 hours of instruction, including practice in a hospital or ambulance\",\"image\":\"http://127.0.0.1:8000/public/images/badges/0dd6ac7a-9030-487a-8c93-bec2266ca1f7.png\",\"narrative\":\"EMT-Basic students must pass an exam testing the ability to assess patient condition, handle trauma or cardiac emergencies and clear blocked airways. They also learn to immobilize injured patients and give oxygen.\",\"category\":{\"_id\":\"58e3a560716617e27241fd07\",\"updatedAt\":\"2017-04-11T07:28:01.846Z\",\"createdAt\":\"2017-04-04T13:53:36.978Z\",\"description\":\"fire dept\",\"name\":\"Firefighter\",\"__v\":0},\"__v\":1,\"organizations\":[\"58e1dfaf159139ee277d7ab7\"],\"isActive\":true,\"type\":\"BadgeClasses\"},{\"_id\":\"58dfa0d7a016c8832d9b7eab\",\"updatedAt\":\"2017-04-06T06:35:06.234Z\",\"createdAt\":\"2017-04-01T12:45:11.682Z\",\"name\":\"Entry-Level Firefighter\",\"description\":\"Instruction lasts several weeks and teaches building codes, emergency medical procedures and prevention techniques. Plus, programs train students to fight fires with standard equipment, such as fire extinguishers, ladders, axes and chainsaws\",\"image\":\"http://127.0.0.1:8000/public/images/badges/e44ce336-987a-4b93-aa46-d79ec006f4d0.png\",\"narrative\":\"After academy training, firefighters need to complete an apprenticeship of up to four years. Some fire departments send students for additional education with the National Fire Academy, where they learn disaster preparedness, public education and how to handle hazardous materials\",\"category\":{\"_id\":\"58e3a560716617e27241fd07\",\"updatedAt\":\"2017-04-11T07:28:01.846Z\",\"createdAt\":\"2017-04-04T13:53:36.978Z\",\"description\":\"fire dept\",\"name\":\"Firefighter\",\"__v\":0},\"__v\":0,\"isActive\":true,\"type\":\"BadgeClasses\"}],\"Medical\":[{\"_id\":\"58dfa060a016c8832d9b7eaa\",\"updatedAt\":\"2017-04-06T06:48:56.281Z\",\"createdAt\":\"2017-04-01T12:43:12.698Z\",\"name\":\"EMT-Paramedic\",\"description\":\"Certification programs typically require at least 1,300 hours of training in advanced medical skills, such as stitching wounds or giving intravenous medications\",\"image\":\"http://127.0.0.1:8000/public/images/badges/21df3b9d-7a53-4558-9bb8-9e533e85a1b8.png\",\"narrative\":\"Firefighters learning paramedic practices usually perform hospital rotations that emphasize emergency-department skills and train them to provide advanced life support to patients. Aspiring firefighters learn to defibrillate patients, insert breathing tubes and catheterize major veins and arteries to monitor blood pressure and take blood sample\",\"category\":{\"_id\":\"58df9ef8a016c8832d9b7ea7\",\"updatedAt\":\"2017-04-01T12:37:12.746Z\",\"createdAt\":\"2017-04-01T12:37:12.746Z\",\"description\":\"Medical Department\",\"name\":\"Medical\",\"__v\":0},\"__v\":0,\"isActive\":true,\"type\":\"BadgeClasses\"}]}";
//
//            JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();
//            if (jObjResponse != null){
//                response.setStatus(HttpServletResponse.SC_OK);
//                jsonResponse.add("badges", GsonService.getGson().toJsonTree(jObjResponse));
//                jsonResponse.addProperty("error", false);
//                return jsonResponse.toString();
//            } else {
//                response.setStatus(HttpServletResponse.SC_OK);
//                jsonResponse.addProperty("error", true);
//                jsonResponse.addProperty("errorMsg", "No badges found");
//                return jsonResponse.toString();
//            }

            //Dynamic
            String[] split = accessToken.split("\\.");
            String decodeTokenBody = Utils.decodeBase64url(split[1]);

            JsonObject jsonObjectBody = new JsonParser().parse(decodeTokenBody).getAsJsonObject();
            String issuer= jsonObjectBody.get("iss").getAsString();

            IssuerBadgeRequest issuerBadgeRequest = new IssuerBadgeRequest(issuer,type);

            final String uri = Global.API_ENDPOINT + Global.getTemplateBadgesByParticipant;

            DisableSSLCertificateCheckUtil.disableChecks();
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.postForObject(uri, issuerBadgeRequest, String.class);

            JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();
            if (jObjResponse != null){
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
}