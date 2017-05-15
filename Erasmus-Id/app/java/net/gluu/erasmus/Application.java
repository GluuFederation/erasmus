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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;

import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.Participant;
import net.gluu.erasmus.model.ScanResponseSuccess;

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

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mApplicationContext = getApplicationContext();
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
}
