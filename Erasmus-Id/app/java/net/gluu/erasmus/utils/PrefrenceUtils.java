package net.gluu.erasmus.utils;


import android.content.Context;
import android.content.SharedPreferences;

import net.gluu.erasmus.Application;

/**
 * Created by Arvind Tomar on 12/7/17.
 */

public class PrefrenceUtils {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public static final String CITY = "CITY";
    public static final String STATE = "STATE";
    public static final String OPDETAILS = "OPDETAILS";


    public PrefrenceUtils() {

        sharedPref = Application.mApplicationContext.getSharedPreferences(Application.mApplicationContext.getPackageName(), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

    }

    public String getCity() {

        return sharedPref.getString(CITY, "");
    }

    public void setCity(String city) {
        editor.putString(CITY, city).commit();
    }

    public String getState() {
        return sharedPref.getString(CITY, "");
    }

    public void setState(String state) {
        editor.putString(STATE, state).commit();
    }

    public String getGetOpDetails() {
        return sharedPref.getString(OPDETAILS, "");
    }

    public void setGetOpDetails(String getOpDetails) {
        editor.putString(OPDETAILS, getOpDetails).commit();
    }
}
