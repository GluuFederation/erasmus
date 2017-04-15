package org.xdi.oxd.badgemanager.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.xdi.oxd.badgemanager.ldap.LDAPInitializer;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeCommands;
import org.xdi.oxd.badgemanager.ldap.commands.BadgeInstancesCommands;
import org.xdi.oxd.badgemanager.ldap.commands.OrganizationCommands;
import org.xdi.oxd.badgemanager.ldap.commands.PersonCommands;
import org.xdi.oxd.badgemanager.ldap.models.BadgeInstances;
import org.xdi.oxd.badgemanager.ldap.service.GsonService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
@CrossOrigin
@RestController
@RequestMapping("/badge/instance")
public class BadgeInsatanceController  {

    boolean isConnected = false;
    LdapEntryManager ldapEntryManager;

    //@RequestMapping(value = "/{id:.+}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public String updateBadgeInstance(@PathVariable String id, @RequestBody BadgeInstances badges, HttpServletResponse response) {
//
//        JsonObject jsonResponse = new JsonObject();
//        badges.setInum(id);
//        if (isConnected) {
//            try {
//                BadgeInstancesCommands.updateBadge(ldapEntryManager, badges);
//                jsonResponse.addProperty("success", "Badge instance updated successfully");
//                return jsonResponse.toString();
//            } catch (Exception e) {
//                e.printStackTrace();
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                jsonResponse.addProperty("error", e.getMessage());
//                return jsonResponse.toString();
//            }
//
//        } else {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            jsonResponse.addProperty("error", "Please try after some time");
//            return jsonResponse.toString();
//        }
//    }

//    @RequestMapping(value = "/{inum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeInstanceByInum(@PathVariable String inum, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        //Static

        try {

            String result=" {\n" +
                    "    \"type\": \"BadgeClass\",\n" +
                    "    \"id\": \"https://example.org/badges/5\",\n" +
                    "    \"name\": \"Emergency Medical Technician-Basic\",\n" +
                    "    \"description\": \"EMT-Basic training requires about 100 hours of instruction, including practice in a hospital or ambulance\",\n" +
                    "    \"image\": \"https://example.org/badges/5/image\",\n" +
                    "    \"criteria\": {\n" +
                    "      \"narrative\": \"EMT-Basic students must pass an exam testing the ability to assess patient condition, handle trauma or cardiac emergencies and clear blocked airways. They also learn to immobilize injured patients and give oxygen. \"\n" +
                    "    },\n" +
                    "    \"issuer\": {\n" +
                    "      \"id\": \"https://example.org/issuer\",\n" +
                    "      \"type\": \"Profile\",\n" +
                    "      \"name\": \"National Registry of Emergency Medical Technicians-state specific\",\n" +
                    "      \"url\": \"https://example.org\",\n" +
                    "      \"email\": \"contact@example.org\",\n" +
                    "      \"verification\": {\n" +
                    "         \"allowedOrigins\": \"example.org\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }";

            JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();

            if (jObjResponse != null){
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.add("badge", GsonService.getGson().toJsonTree(jObjResponse));
                jsonResponse.addProperty("error", false);
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No badges found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", e.getMessage());
            return jsonResponse.toString();
        }

        //Dynamic
//        if (isConnected) {
//            try {
//                BadgeInstances badges = BadgeInstancesCommands.getBadgeInstanceByInum(ldapEntryManager, id);
//                if (badges != null) {
//                    response.setStatus(HttpServletResponse.SC_OK);
//                    return GsonService.getGson().toJson(badges);
//                } else {
//                    response.setStatus(HttpServletResponse.SC_CONFLICT);
//                    jsonResponse.addProperty("error", "No such badge found");
//                    return jsonResponse.toString();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                jsonResponse.addProperty("error", e.getMessage());
//                return jsonResponse.toString();
//            }
//        } else {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            jsonResponse.addProperty("error", "Please try after some time");
//            return jsonResponse.toString();
//        }
    }

    @RequestMapping(value = "getURL/{accessToken:.+}/{inum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPermanentUrl(@PathVariable String accessToken, @PathVariable String inum, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        //Static

        try {

            String result="https://example.com/badgemgrapi?id=28e780f4-eedf-4fee-8e80-99dae5a92558&key=348159fd-8441-4ebd-9f9c-9480b486e27f";

            if (result != null){
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("url", result);
                jsonResponse.addProperty("error", false);
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No badges found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", e.getMessage());
            return jsonResponse.toString();
        }

        //Dynamic
//        if (isConnected) {
//            try {
//                BadgeInstances badges = BadgeInstancesCommands.getBadgeInstanceByInum(ldapEntryManager, id);
//                if (badges != null) {
//                    response.setStatus(HttpServletResponse.SC_OK);
//                    return GsonService.getGson().toJson(badges);
//                } else {
//                    response.setStatus(HttpServletResponse.SC_CONFLICT);
//                    jsonResponse.addProperty("error", "No such badge found");
//                    return jsonResponse.toString();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                jsonResponse.addProperty("error", e.getMessage());
//                return jsonResponse.toString();
//            }
//        } else {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            jsonResponse.addProperty("error", "Please try after some time");
//            return jsonResponse.toString();
//        }
    }

    @RequestMapping(value = "getBadgeURL/{accessToken:.+}/{inum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeUrl(@PathVariable String accessToken, @PathVariable String inum, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        //Static

        try {

            String result="https://example.com/badgemgrapi?id=28e780f4-eedf-4fee-8e80-99dae5a92558&key=348159fd-8441-4ebd-9f9c-9480b486e27f";

            if (result != null){
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("url", result);
                jsonResponse.addProperty("error", false);
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("errorMsg", "No badges found");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", e.getMessage());
            return jsonResponse.toString();
        }

        //Dynamic
//        if (isConnected) {
//            try {
//                BadgeInstances badges = BadgeInstancesCommands.getBadgeInstanceByInum(ldapEntryManager, id);
//                if (badges != null) {
//                    response.setStatus(HttpServletResponse.SC_OK);
//                    return GsonService.getGson().toJson(badges);
//                } else {
//                    response.setStatus(HttpServletResponse.SC_CONFLICT);
//                    jsonResponse.addProperty("error", "No such badge found");
//                    return jsonResponse.toString();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                jsonResponse.addProperty("error", e.getMessage());
//                return jsonResponse.toString();
//            }
//        } else {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            jsonResponse.addProperty("error", "Please try after some time");
//            return jsonResponse.toString();
//        }
    }

    @RequestMapping(value = "verify/{accessToken:.+}/{inum:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String verifyBadge(@PathVariable String accessToken, @PathVariable String inum, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();

        try {
//            Static
            String result=" {\n" +
                    "    \"type\": \"BadgeClass\",\n" +
                    "    \"id\": \"https://example.org/badges/5\",\n" +
                    "    \"name\": \"Emergency Medical Technician-Basic\",\n" +
                    "    \"description\": \"EMT-Basic training requires about 100 hours of instruction, including practice in a hospital or ambulance\",\n" +
                    "    \"image\": \"https://example.org/badges/5/image\",\n" +
                    "    \"criteria\": {\n" +
                    "      \"narrative\": \"EMT-Basic students must pass an exam testing the ability to assess patient condition, handle trauma or cardiac emergencies and clear blocked airways. They also learn to immobilize injured patients and give oxygen. \"\n" +
                    "    },\n" +
                    "    \"issuer\": {\n" +
                    "      \"id\": \"https://example.org/issuer\",\n" +
                    "      \"type\": \"Profile\",\n" +
                    "      \"name\": \"National Registry of Emergency Medical Technicians-state specific\",\n" +
                    "      \"url\": \"https://example.org\",\n" +
                    "      \"email\": \"contact@example.org\",\n" +
                    "      \"verification\": {\n" +
                    "         \"allowedOrigins\": \"example.org\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }";

            JsonObject jObjResponse = new JsonParser().parse(result).getAsJsonObject();

            if (jObjResponse != null){
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.add("badge", GsonService.getGson().toJsonTree(jObjResponse));
                jsonResponse.addProperty("error", false);
                jsonResponse.addProperty("responseMsg", "Badge verified successfully");
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("error", true);
                jsonResponse.addProperty("responseMsg", "Badge verification failed");
                return jsonResponse.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.addProperty("error", e.getMessage());
            return jsonResponse.toString();
        }

        //Dynamic
//        if (isConnected) {
//            try {
//                BadgeInstances badges = BadgeInstancesCommands.getBadgeInstanceByInum(ldapEntryManager, id);
//                if (badges != null) {
//                    response.setStatus(HttpServletResponse.SC_OK);
//                    return GsonService.getGson().toJson(badges);
//                } else {
//                    response.setStatus(HttpServletResponse.SC_CONFLICT);
//                    jsonResponse.addProperty("error", "No such badge found");
//                    return jsonResponse.toString();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                jsonResponse.addProperty("error", e.getMessage());
//                return jsonResponse.toString();
//            }
//        } else {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            jsonResponse.addProperty("error", "Please try after some time");
//            return jsonResponse.toString();
//        }
    }

//    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBadgeInstances(HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();
        if (isConnected) {
            List<BadgeInstances> badges = BadgeInstancesCommands.getAllBadgesInstance(ldapEntryManager);
            if (badges != null) {

                for (BadgeInstances badge : badges) {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                response.setStatus(HttpServletResponse.SC_OK);

                return GsonService.getGson().toJson(badges);
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.addProperty("error", "No Badge instances found");
                return jsonResponse.toString();

            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();

        }
    }

//    @RequestMapping(value = "/{id:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String removeBadgeInstance(@PathVariable String id, HttpServletResponse response) {

        JsonObject jsonResponse = new JsonObject();
        if (isConnected) {
            boolean isDeleted = BadgeInstancesCommands.deleteBadgeInstanceByInum(ldapEntryManager, id);
            if (isDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.addProperty("success", "Badge instance was deleted successfully");
                return jsonResponse.toString();
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonResponse.addProperty("error", "No such badge instance found");
                return jsonResponse.toString();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("error", "Please try after some time");
            return jsonResponse.toString();
        }
    }
}