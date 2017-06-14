package net.gluu.erasmus.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Arvind Tomar on 14/6/17.
 */

public class JsonUtils {

    public static String getValueFromJson(String key, JsonObject jObj) {
        if (jObj == null || !jObj.has(key) ) {
            return  "";
        } else if(jObj.get(key) instanceof JsonArray){
            return jObj.get(key).getAsJsonArray().get(0).getAsString();
        }

        return jObj.get(key).getAsString();
    }
}
