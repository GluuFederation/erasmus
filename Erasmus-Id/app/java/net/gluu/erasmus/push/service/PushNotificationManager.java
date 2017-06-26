package net.gluu.erasmus.push.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import net.gluu.erasmus.BuildConfig;
import net.gluu.erasmus.push.Config;
import net.gluu.erasmus.utils.Utils;

public class PushNotificationManager extends FirebaseInstanceIdService {
    private static final String TAG = "TAG";
 
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
 
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);
 
        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);
 
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
            throw new RuntimeException("Could not get package name: " + ex);
        }
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }
 
    private void storeRegIdInPref(String token) {
        Log.v("TAG","token: "+token);

        final SharedPreferences prefs = getFCMPreferences(getApplicationContext());

        int appVersion = getAppVersion(getApplicationContext());
        if (BuildConfig.DEBUG) Log.d(TAG, "Saving regId on app version " + appVersion);

        prefs.edit().putString(Config.PROPERTY_REG_ID, token).putInt(Config.PROPERTY_APP_VERSION, appVersion).apply();
    }

    private static SharedPreferences getFCMPreferences(final Context context) {
        return context.getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     *
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    public static String getRegistrationId(final Context context) {
        final SharedPreferences prefs = getFCMPreferences(context);
        String registrationId = prefs.getString(Config.PROPERTY_REG_ID, null);
        if (Utils.isEmpty(registrationId)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Registration not found");
            return null;
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Config.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            if (BuildConfig.DEBUG) Log.d(TAG, "App version changed");
            return null;
        }

        return registrationId;
    }
}