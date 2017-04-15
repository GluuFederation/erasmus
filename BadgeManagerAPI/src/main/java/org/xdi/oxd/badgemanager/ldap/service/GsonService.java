package org.xdi.oxd.badgemanager.ldap.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static void setGson(Gson gson) {

        GsonService.gson = gson;
    }
}
