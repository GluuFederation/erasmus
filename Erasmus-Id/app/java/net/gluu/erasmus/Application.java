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
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import net.gluu.appauth.AppAuthConfiguration;
import net.gluu.appauth.AuthState;
import net.gluu.appauth.AuthorizationException;
import net.gluu.appauth.AuthorizationService;
import net.gluu.appauth.ClientAuthentication;
import net.gluu.appauth.TokenRequest;
import net.gluu.appauth.TokenResponse;
import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.Participant;
import net.gluu.erasmus.model.Recipient;
import net.gluu.erasmus.model.ScanResponseSuccess;
import net.gluu.erasmus.model.UserInfo;
import net.gluu.erasmus.utils.JWTUtils;

import org.json.JSONObject;

/**
 * Application object; ensures that the support library is correctly configured for use of
 * vector drawables.
 */
public final class Application extends android.app.Application {
    public static Context mApplicationContext;
    public static String State, City;
    public static String AccessToken;
    public static Participant participant;
    public static String wellknownurl = "/.well-known/openid-configuration";
    public static DisplayBadge displayBadge;
    public static ScanResponseSuccess scanResponseSuccess;

    //    U2F
    public static String U2F_Username = "test";
    public static String U2F_Issuer = "https://ce-release.gluu.org";
    public static String U2F_State = "900bea6a-6b8e-4d04-ad40-2475882389b9";
    public static String U2F_Method = "authenticate";
    public static String U2F_App = "https://ce-release.gluu.org/identity/authentication/authcode";
    private static AuthStateManager mStateManager;
    private static AuthorizationService mAuthService;

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
    }

    public static void showAutoDismissAlertDialog(Context ctx, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Badge Manager");
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
                        JSONObject jsonObject = new JSONObject(jObj.getString("userinfo"));
                        userInfo1 = new UserInfo();
                        userInfo1.setEmail(jsonObject.getString("email") == null ? "" : jsonObject.getString("email"));
                        userInfo1.setZoneInfo(jsonObject.getString("zoneinfo") == null ? "" : jsonObject.getString("zoneinfo"));
                        userInfo1.setNickName(jsonObject.getString("nickname") == null ? "" : jsonObject.getString("nickname"));
                        userInfo1.setWebsite(jsonObject.getString("website") == null ? "" : jsonObject.getString("website"));
                        userInfo1.setMiddleName(jsonObject.getString("middle_name") == null ? "" : jsonObject.getString("middle_name"));
                        userInfo1.setLocale(jsonObject.getString("locale") == null ? "" : jsonObject.getString("locale"));
                        userInfo1.setPreferredUsername(jsonObject.getString("preferred_username") == null ? "" : jsonObject.getString("preferred_username"));
                        userInfo1.setGivenName(jsonObject.getString("given_name") == null ? "" : jsonObject.getString("given_name"));
                        userInfo1.setPicture(jsonObject.getString("picture") == null ? "" : jsonObject.getString("picture"));
                        userInfo1.setName(jsonObject.getString("name") == null ? "" : jsonObject.getString("name"));
                        userInfo1.setBirthdate(jsonObject.getString("birthdate") == null ? "" : jsonObject.getString("birthdate"));
                        userInfo1.setFamilyName(jsonObject.getString("family_name") == null ? "" : jsonObject.getString("family_name"));
                        userInfo1.setGender(jsonObject.getString("gender") == null ? "" : jsonObject.getString("gender"));
                        userInfo1.setProfile(jsonObject.getString("profile") == null ? "" : jsonObject.getString("profile"));
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

    public static String getAccessToken() {
        AuthState state = mStateManager.getCurrent();
        Long expiresAt = state.getAccessTokenExpirationTime();
        Application.AccessToken = state.getAccessToken();
        if (expiresAt != null && expiresAt < System.currentTimeMillis()) {
            //refresh token
            Log.v("TAG", "Access token expired");
            refreshAccessToken();
        }
        return Application.AccessToken;
    }

    public static void refreshAccessToken() {
        performTokenRequest(
                mStateManager.getCurrent().createTokenRefreshRequest(),
                new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
                        mStateManager.updateAfterTokenResponse(response, ex);
                        AuthState state = mStateManager.getCurrent();
                        Application.AccessToken = state.getAccessToken();
                        Log.v("TAG", "Access token after refresh: " + Application.AccessToken);
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
}
