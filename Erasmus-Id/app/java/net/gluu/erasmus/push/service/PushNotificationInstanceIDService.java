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

public class PushNotificationInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = PushNotificationInstanceIDService.class.getSimpleName();
 
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
}