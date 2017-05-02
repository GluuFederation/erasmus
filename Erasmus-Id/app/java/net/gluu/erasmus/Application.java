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

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.gluu.erasmus.model.Participant;

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
    public static ProgressBar mProgress;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mApplicationContext = getApplicationContext();

        mProgress = new ProgressBar(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mProgress.setLayoutParams(params);
        mProgress.setVisibility(View.GONE);
    }

    public static void showProgressBar() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar() {
        if (mProgress == null)
            return;

        mProgress.setVisibility(View.GONE);
    }
}
