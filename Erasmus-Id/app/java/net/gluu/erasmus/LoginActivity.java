/*
 * Copyright 2015 The AppAuth for Android Authors. All Rights Reserved.
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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnyThread;
import android.support.annotation.ColorRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import net.gluu.appauth.AppAuthConfiguration;
import net.gluu.appauth.AuthState;
import net.gluu.appauth.AuthorizationException;
import net.gluu.appauth.AuthorizationRequest;
import net.gluu.appauth.AuthorizationService;
import net.gluu.appauth.AuthorizationServiceConfiguration;
import net.gluu.appauth.ClientSecretBasic;
import net.gluu.appauth.RegistrationRequest;
import net.gluu.appauth.RegistrationResponse;
import net.gluu.appauth.ResponseTypeValues;
import net.gluu.appauth.browser.AnyBrowserMatcher;
import net.gluu.appauth.browser.BrowserMatcher;
import net.gluu.erasmus.model.Participant;
import net.gluu.erasmus.model.u2f.U2fMetaData;
import net.gluu.erasmus.utils.PrefrenceUtils;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Demonstrates the usage of the AppAuth to authorize a user with an OAuth2 / OpenID Connect
 * provider. Based on the configuration provided in `res/raw/auth_config.json`, the code
 * contained here will:
 * <p>
 * - Retrieve an OpenID Connect discovery document for the provider, or use a local static
 * configuration.
 * - Utilize dynamic client registration, if no static client id is specified.
 * - Initiate the authorization request using the built-in heuristics or a user-selected browser.
 * <p>
 * _NOTE_: From a clean checkout of this project, the authorization service is not configured.
 * Edit `res/values/auth_config.xml` to provide the required configuration properties. See the
 * README.md in the app/ directory for configuration instructions, and the adjacent IDP-specific
 * instructions.
 */
public final class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String EXTRA_FAILED = "failed";

    private AuthorizationService mAuthService;
    private AuthStateManager mAuthStateManager;
    private Configuration mConfiguration;

    private final AtomicReference<String> mClientId = new AtomicReference<>();
    private ExecutorService mExecutor;

    ProgressDialog mProgress;

    @NonNull
    private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthStateManager = AuthStateManager.getInstance(this);
        mConfiguration = Configuration.getInstance(this);
        restoreData();

        mExecutor = Executors.newSingleThreadExecutor();

        mConfiguration.setDiscoveryUri(Application.participant.getOpHost() + Application.wellknownurl);
        Application.U2F_Issuer = Application.participant.getOpHost();
//        mConfiguration.setDiscoveryUri("https://erasmusdev.gluu.org" + Application.wellknownurl);

        if (mAuthStateManager.getCurrent().isAuthorized()
                && !mConfiguration.hasConfigurationChanged()) {
            startActivity(new Intent(this, BadgeStatusActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        mProgress = new ProgressDialog(LoginActivity.this);
        mProgress.setMessage("Loading..");

        ((TextView) findViewById(R.id.tv_organization)).setText(Application.participant.getName());
        findViewById(R.id.tv_change_organization).setOnClickListener((View view) -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        findViewById(R.id.start_auth).setOnClickListener((View view) -> startAuth());

        if (!mConfiguration.isValid()) {
            displayError(mConfiguration.getConfigurationError(), false);
            return;
        }

        configureBrowserSelector();
//        if (mConfiguration.hasConfigurationChanged()) {
//            // discard any existing authorization state due to the change of configuration
//            Log.i(TAG, "Configuration change detected, discarding old state");
//            mAuthStateManager.replace(new AuthState());
//            mConfiguration.acceptConfiguration();
//        }

        if (getIntent().getBooleanExtra(EXTRA_FAILED, false)) {
            Snackbar.make(findViewById(R.id.coordinator),
                    "Authorization canceled",
                    Snackbar.LENGTH_SHORT)
                    .show();
        }

        displayLoading("Initializing");

        if (mConfiguration.hasConfigurationChanged()) {
            // discard any existing authorization state due to the change of configuration
            Log.i(TAG, "Configuration change detected, discarding old state");
            mAuthStateManager.replace(new AuthState());
            mConfiguration.acceptConfiguration();
        }
        mExecutor.submit(this::initializeAppAuth);
    }

    private void restoreData() {
        PrefrenceUtils prefrenceUtils = new PrefrenceUtils();
        Participant participant = new Gson().fromJson(prefrenceUtils.getGetOpDetails(), Participant.class);
        if (Application.participant == null || !Application.participant.getOpHost().equals(participant.getOpHost())) {
//            mAuthStateManager.replace(new AuthState());
//            mConfiguration.acceptConfiguration();
        }
        Application.participant = participant;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mExecutor.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
        if (mAuthService != null) {
            mAuthService.dispose();
        }
        super.onDestroy();

    }

    @MainThread
    void startAuth() {
        final String loginHint = getString(R.string.login_hint_value);

        displayLoading("Making authorization request");

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        mExecutor.submit(() -> doAuth(loginHint));
    }

    /**
     * Initializes the authorization service configuration if necessary, either from the local
     * static values or by retrieving an OpenID discovery document.
     */
    @WorkerThread
    private void initializeAppAuth() {
        recreateAuthorizationService();

        if (mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {
            // configuration is already created, skip to client initialization
            initializeClient();
            return;
        }

        // if we are not using discovery, build the authorization service configuration directly
        // from the static configuration values.
        if (mConfiguration.getDiscoveryUri() == null) {
            AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
                    mConfiguration.getAuthEndpointUri(),
                    mConfiguration.getTokenEndpointUri(),
                    mConfiguration.getRegistrationEndpointUri());

            mAuthStateManager.replace(new AuthState(config));
            initializeClient();
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        runOnUiThread(() -> displayLoading("Retrieving discovery document"));
        AuthorizationServiceConfiguration.fetchFromUrl(
                mConfiguration.getDiscoveryUri(),
                this::handleConfigurationRetrievalResult,
                mConfiguration.getConnectionBuilder());
    }

    @MainThread
    private void handleConfigurationRetrievalResult(
            AuthorizationServiceConfiguration config, AuthorizationException ex) {
        if (config == null) {
            displayError("Failed to retrieve discovery document: " + ex.getMessage(), true);
            return;
        }

        mAuthStateManager.replace(new AuthState(config));
        mExecutor.submit(this::initializeClient);
    }

    /**
     * Initiates a dynamic registration request if a client ID is not provided by the static
     * configuration.
     */
    @WorkerThread
    private void initializeClient() {

        if (mConfiguration.getClientId() != null) {
            // use a statically configured client ID
            mClientId.set(mConfiguration.getClientId());
            runOnUiThread(this::displayAuthOptions);
            return;
        }

        RegistrationResponse lastResponse =
                mAuthStateManager.getCurrent().getLastRegistrationResponse();
        if (lastResponse != null) {
            // already dynamically registered a client ID
            mClientId.set(lastResponse.clientId);
            runOnUiThread(this::displayAuthOptions);
            return;
        }

        // WrongThread inference is incorrect for lambdas
        // noinspection WrongThread
        runOnUiThread(() -> displayLoading("Dynamically registering client"));

        RegistrationRequest registrationRequest = new RegistrationRequest.Builder(
                mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                Collections.singletonList(mConfiguration.getRedirectUri()))
                .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
                .build();

        mAuthService.performRegistrationRequest(
                registrationRequest,
                this::handleRegistrationResponse);
    }

    @MainThread
    private void handleRegistrationResponse(
            RegistrationResponse response,
            AuthorizationException ex) {
        mAuthStateManager.updateAfterRegistration(response, ex);
        if (response == null) {
            displayErrorLater("Failed to register client: " + ex.getMessage(), true);
            return;
        }

        mClientId.set(response.clientId);
        displayAuthOptions();
    }

    /**
     * Enumerates the browsers installed on the device and populates a spinner, allowing the
     * demo user to easily test the authorization flow against different browser and custom
     * tab configurations.
     */
    @MainThread
    private void configureBrowserSelector() {
//        Spinner spinner = (Spinner) findViewById(R.id.browser_selector);
//        final BrowserSelectionAdapter adapter = new BrowserSelectionAdapter(this);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                BrowserInfo info = adapter.getItem(position);
//                if (info == null) {
//                    mBrowserMatcher = AnyBrowserMatcher.INSTANCE;
//                    return;
//                } else {
//                    mBrowserMatcher = new ExactBrowserMatcher(info.mDescriptor);
//                }
//
//                recreateAuthorizationService();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mBrowserMatcher = AnyBrowserMatcher.INSTANCE;
//            }
//        });
    }

    /**
     * Performs the authorization request, using the browser selected in the spinner,
     * and a user-provided `login_hint` if available.
     */
    @WorkerThread
    private void doAuth(@NonNull String loginHint) {
        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                mClientId.get(),
                ResponseTypeValues.CODE,
                mConfiguration.getRedirectUri())
                .setScope(mConfiguration.getScope());

        if (!TextUtils.isEmpty(loginHint)) {
            authRequestBuilder.setLoginHint(loginHint);
        }

        Intent completionIntent = new Intent(this, U2FActivity.class);
        Intent cancelIntent = new Intent(this, LoginActivity.class);
        cancelIntent.putExtra(EXTRA_FAILED, true);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mAuthService.performAuthorizationRequest(
                authRequestBuilder.build(),
                PendingIntent.getActivity(this, 0, completionIntent, 0),
                PendingIntent.getActivity(this, 0, cancelIntent, 0),
                mAuthService.createCustomTabsIntentBuilder()
                        .setToolbarColor(getColorCompat(R.color.colorPrimary))
                        .build());
    }

    private void recreateAuthorizationService() {
        if (mAuthService != null) {
            mAuthService.dispose();
        }
        mAuthService = createAuthorizationService();
    }

    private AuthorizationService createAuthorizationService() {
        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
        builder.setBrowserMatcher(mBrowserMatcher);
        builder.setConnectionBuilder(mConfiguration.getConnectionBuilder());

        return new AuthorizationService(this, builder.build());
    }

    @MainThread
    private void displayLoading(String loadingMessage) {
        showProgressBar();
    }

    @MainThread
    private void displayError(String error, boolean recoverable) {
//        findViewById(R.id.error_container).setVisibility(View.VISIBLE);
//        findViewById(R.id.loading_container).setVisibility(View.GONE);
//        findViewById(R.id.auth_container).setVisibility(View.GONE);
//
//        ((TextView) findViewById(R.id.error_description)).setText(error);
//        findViewById(R.id.retry).setVisibility(recoverable ? View.VISIBLE : View.GONE);
    }

    // WrongThread inference is incorrect in this case
    @SuppressWarnings("WrongThread")
    @AnyThread
    private void displayErrorLater(final String error, final boolean recoverable) {
        runOnUiThread(() -> displayError(error, recoverable));
    }

    @MainThread
    private void displayAuthOptions() {
        hideProgressBar();
        findViewById(R.id.start_auth).setVisibility(View.VISIBLE);

        AuthState state = mAuthStateManager.getCurrent();
        AuthorizationServiceConfiguration config = state.getAuthorizationServiceConfiguration();

        String authEndpointStr;
        if (config.discoveryDoc != null) {
            authEndpointStr = "Discovered auth endpoint: \n";
        } else {
            authEndpointStr = "Static auth endpoint: \n";
        }
        authEndpointStr += config.authorizationEndpoint;

        String clientIdStr;
        if (state.getLastRegistrationResponse() != null) {
            clientIdStr = "Dynamic client ID: \n";
        } else {
            clientIdStr = "Static client ID: \n";
        }
        clientIdStr += mClientId;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    private int getColorCompat(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(color);
        } else {
            return getResources().getColor(color);
        }
    }

    private void showProgressBar() {
        if (mProgress == null)
            return;

        mProgress.show();
    }

    private void hideProgressBar() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }

    @Override
    public void onBackPressed() {
    }
}
