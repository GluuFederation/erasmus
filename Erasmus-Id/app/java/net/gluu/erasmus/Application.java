/*
 * Copyright 2016 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gluu.erasmus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.gluu.appauth.AppAuthConfiguration;
import net.gluu.appauth.AuthState;
import net.gluu.appauth.AuthorizationException;
import net.gluu.appauth.AuthorizationService;
import net.gluu.appauth.ClientAuthentication;
import net.gluu.appauth.TokenRequest;
import net.gluu.appauth.TokenResponse;
import net.gluu.erasmus.model.ApprovedBadgeRequest;
import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.Participant;
import net.gluu.erasmus.model.PendingBadgeRequest;
import net.gluu.erasmus.model.Recipient;
import net.gluu.erasmus.model.ScanResponseSuccess;
import net.gluu.erasmus.model.UserInfo;
import net.gluu.erasmus.utils.JWTUtils;
import net.gluu.erasmus.utils.JsonUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Application object; ensures that the support library is correctly configured for use of
 * vector drawables.
 */

public final class Application extends android.app.Application {
    public static Context mApplicationContext;
    public static String State, City;
    public static Participant participant;
    public static String wellknownurl = "/.well-known/openid-configuration";
    public static DisplayBadge displayBadge;
    public static ScanResponseSuccess scanResponseSuccess;

    //    U2F
    public static String U2F_Username = "test";
    public static String U2F_Issuer = "https://ce-release.gluu.org";
    public static String U2F_State = "27218b33-20bd-40f9-b881-beda2f73a92f";
    public static String U2F_Method = "enroll";
    public static String U2F_App = "/identity/authentication/authcode";
    private static AuthStateManager mStateManager;
    private static AuthorizationService mAuthService;
    public static Handler applicationHandler;
    public static List<ApprovedBadgeRequest> approvedBadgeRequests = new ArrayList<>();
    public static List<PendingBadgeRequest> pendingBadgeRequests = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mApplicationContext = getApplicationContext();
        mStateManager = AuthStateManager.getInstance(this);
        Configuration config = Configuration.getInstance(this);
        mAuthService = new AuthorizationService(
                this,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());
        applicationHandler = new Handler(this.getMainLooper());
    }

    public static void showAutoDismissAlertDialog(Context ctx, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.Theme_AppCompat_Dialog);
        builder.setTitle("ERASMUS");
        builder.setMessage(msg);

        AlertDialog alert = builder.create();
        alert.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.dismiss();
            }
        }, 2000);
    }

    public static UserInfo getUserInfo(Recipient recipient) {
        UserInfo userInfo1 = null;

        try {
            if (recipient != null) {
                try {
                    String userInfo = JWTUtils.decoded(recipient.getIdentity());
                    Log.v("TAG", "User info is:" + userInfo);
                    if (userInfo != null && userInfo.length() > 0) {
                        JSONObject jObj = new JSONObject(userInfo);
                        JsonObject jsonObject = new JsonParser().parse(jObj.getString("userinfo")).getAsJsonObject();
                        userInfo1 = new UserInfo();
                        userInfo1.setEmail(JsonUtils.getValueFromJson("email", jsonObject));
                        userInfo1.setZoneInfo(JsonUtils.getValueFromJson("zoneinfo", jsonObject));
                        userInfo1.setNickName(JsonUtils.getValueFromJson("nickname", jsonObject));
                        userInfo1.setWebsite(JsonUtils.getValueFromJson("website", jsonObject));
                        userInfo1.setMiddleName(JsonUtils.getValueFromJson("middle_name", jsonObject));
                        userInfo1.setLocale(JsonUtils.getValueFromJson("locale", jsonObject));
                        userInfo1.setPreferredUsername(JsonUtils.getValueFromJson("preferred_username", jsonObject));
                        userInfo1.setGivenName(JsonUtils.getValueFromJson("given_name", jsonObject));
                        userInfo1.setPicture(JsonUtils.getValueFromJson("picture", jsonObject));
                        userInfo1.setName(JsonUtils.getValueFromJson("name", jsonObject));
                        userInfo1.setBirthdate(JsonUtils.getValueFromJson("birthdate", jsonObject));
                        userInfo1.setFamilyName(JsonUtils.getValueFromJson("family_name", jsonObject));
                        userInfo1.setGender(JsonUtils.getValueFromJson("gender", jsonObject));
                        userInfo1.setProfile(JsonUtils.getValueFromJson("profile", jsonObject));
                        return userInfo1;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return userInfo1;
    }

    public static void getAccessToken(net.gluu.erasmus.api.AccessToken accessToken) {
        AuthState state = mStateManager.getCurrent();
        if ((state.getAccessTokenExpirationTime() != null && state.getAccessTokenExpirationTime() < System.currentTimeMillis()) || state.getAccessToken() == null || state.getAccessToken().length() == 0) {
            //refresh token
            Log.v("TAG", "Access token expired");
            refreshAccessToken(accessToken);
        } else if (state.getAccessToken() != null && state.getAccessToken().length() > 0) {
            accessToken.onAccessTokenSuccess(state.getAccessToken());
        } else {
            accessToken.onAccessTokenFailure();
        }
    }

    public static void refreshAccessToken(net.gluu.erasmus.api.AccessToken accessToken) {
        performTokenRequest(
                mStateManager.getCurrent().createTokenRefreshRequest(),
                new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
                        mStateManager.updateAfterTokenResponse(response, ex);
                        AuthState state = mStateManager.getCurrent();
                        Log.v("TAG", "Access token after refresh: " + state.getAccessToken());
                        if (state.getAccessToken() != null && state.getAccessToken().length() > 0) {
                            accessToken.onAccessTokenSuccess(state.getAccessToken());
                        } else {
                            accessToken.onAccessTokenFailure();
                        }
                    }
                });
    }

    public static void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.v("TAG", "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            return;
        }

        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return ((netInfo != null) && netInfo.isAvailable() && netInfo
                .isConnected());
    }
}
