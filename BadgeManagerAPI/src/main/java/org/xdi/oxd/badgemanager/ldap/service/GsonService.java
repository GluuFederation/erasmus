package org.xdi.oxd.badgemanager.ldap.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Arvind Tomar on 26/11/16.
 */
public class GsonService {
    private static Gson gson;

    public GsonService() {
        gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().serializeNulls().create();
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().serializeNulls().create();

        }
        return gson;
    }

    public static String getValueFromJson(String key, JsonObject jObj) {
        if (jObj == null || !jObj.has(key) ) {
            return  "";
        } else if(jObj.get(key) instanceof JsonArray){
            return jObj.get(key).getAsJsonArray().get(0).getAsString();
        }

        return jObj.get(key).getAsString();
    }

    public static void setGson(Gson gson) {

        GsonService.gson = gson;
    }
}
