/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package net.gluu.erasmus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.gluu.appauth.AppAuthConfiguration;
import net.gluu.appauth.AuthState;
import net.gluu.appauth.AuthorizationException;
import net.gluu.appauth.AuthorizationResponse;
import net.gluu.appauth.AuthorizationService;
import net.gluu.appauth.AuthorizationServiceDiscovery;
import net.gluu.appauth.ClientAuthentication;
import net.gluu.appauth.TokenRequest;
import net.gluu.erasmus.device.DeviceUuidManager;
import net.gluu.erasmus.fragments.u2f.KeyFragmentListFragment;
import net.gluu.erasmus.fragments.u2f.KeyHandleInfoFragment;
import net.gluu.erasmus.fragments.u2f.ProcessFragment;
import net.gluu.erasmus.listener.OxPush2RequestListener;
import net.gluu.erasmus.listener.PushNotificationRegistrationListener;
import net.gluu.erasmus.model.u2f.OxPush2Request;
import net.gluu.erasmus.net.CommunicationService;
import net.gluu.erasmus.store.AndroidKeyDataStore;
import net.gluu.erasmus.u2f.v2.SoftwareDevice;
import net.gluu.erasmus.u2f.v2.exception.U2FException;
import net.gluu.erasmus.u2f.v2.model.TokenResponse;
import net.gluu.erasmus.u2f.v2.store.DataStore;
import net.gluu.erasmus.utils.Utils;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import okio.Okio;

/**
 * Main activity
 * <p>
 * Created by Yuriy Movchan on 12/28/2015.
 */
public class U2FActivity extends AppCompatActivity implements OxPush2RequestListener, KeyFragmentListFragment.OnListFragmentInteractionListener, PushNotificationRegistrationListener, KeyHandleInfoFragment.OnDeleteKeyHandleListener {

    private static final String TAG = "main-activity";

    public static final String QR_CODE_PUSH_NOTIFICATION_MESSAGE = MainActivity.class.getPackage().getName() + ".QR_CODE_PUSH_NOTIFICATION_MESSAGE";

    private SoftwareDevice u2f;
    private AndroidKeyDataStore dataStore;

    private static Context context;

    private static final String KEY_USER_INFO = "userInfo";

    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();
    private ExecutorService mExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init network layer
        CommunicationService.init();
        // Init device UUID service
        DeviceUuidManager deviceUuidFactory = new DeviceUuidManager();
        deviceUuidFactory.init(this);

        context = getApplicationContext();
        this.dataStore = new AndroidKeyDataStore(context);
        this.u2f = new SoftwareDevice(this, dataStore);

        mStateManager = AuthStateManager.getInstance(this);
        mExecutor = Executors.newSingleThreadExecutor();

        Configuration config = Configuration.getInstance(this);
        if (config.hasConfigurationChanged()) {
            Toast.makeText(
                    this,
                    "Configuration change detected",
                    Toast.LENGTH_SHORT)
                    .show();
            signOut();
            return;
        }

        mAuthService = new AuthorizationService(
                this,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());

        setContentView(R.layout.activity_u2f);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayLoading("Restoring state...");

        if (savedInstanceState != null) {
            try {
                mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
            }
        }
    }

    private String getRequestData() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", Application.U2F_Username);
        jsonObject.addProperty("issuer", Application.U2F_Issuer);
        jsonObject.addProperty("state", Application.U2F_State);
        jsonObject.addProperty("method", Application.U2F_Method);
        jsonObject.addProperty("app", Application.U2F_Issuer + Application.U2F_App);

        return jsonObject.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_manage_keys) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            KeyFragmentListFragment fragment = new KeyFragmentListFragment();

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQrRequest(String requestJson) {
        if (!validateOxPush2Request(requestJson)) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = ProcessFragment.newInstance(requestJson);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public TokenResponse onSign(String jsonRequest, String origin, Boolean isDeny) throws JSONException, IOException, U2FException {
        return u2f.sign(jsonRequest, origin, isDeny);
    }

    @Override
    public TokenResponse onEnroll(String jsonRequest, OxPush2Request oxPush2Request, Boolean isDeny) throws JSONException, IOException, U2FException {
        return u2f.enroll(jsonRequest, oxPush2Request, isDeny);
    }

    @Override
    public DataStore onGetDataStore() {
        return dataStore;
    }

    private boolean validateOxPush2Request(String requestJson) {
        boolean result = true;
        try {
            // Try to parse JSON
            OxPush2Request oxPush2Request = new Gson().fromJson(requestJson, OxPush2Request.class);

            boolean isOneStep = Utils.isEmpty(oxPush2Request.getUserName());
            boolean isTwoStep = Utils.areAllNotEmpty(oxPush2Request.getUserName(), oxPush2Request.getIssuer(), oxPush2Request.getApp(),
                    oxPush2Request.getState(), oxPush2Request.getMethod());

            if (BuildConfig.DEBUG)
                Log.d(TAG, "isOneStep: " + isOneStep + " isTwoStep: " + isTwoStep);

            if (isOneStep || isTwoStep) {
                // Valid authentication method should be used
                if (isTwoStep && !(Utils.equals(oxPush2Request.getMethod(), "authenticate") || Utils.equals(oxPush2Request.getMethod(), "enroll"))) {
                    result = false;
                }
            } else {
                // All fields must be not empty
                result = false;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to parse QR code");
            result = false;
        }

        if (!result) {
            Toast.makeText(getApplicationContext(), R.string.invalid_qr_code, Toast.LENGTH_LONG).show();
        }

        return result;
    }

    @Override
    public void onListFragmentInteraction(String tokenString) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        KeyHandleInfoFragment fragment = new KeyHandleInfoFragment().newInstance(tokenString);

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onPushRegistrationSuccess(String registrationId, boolean isNewRegistration) {
        Log.v("TAG", "Registration successful. registration id is: " + registrationId);
    }

    @Override
    public void onPushRegistrationFailure(Exception ex) {
        Toast.makeText(getApplicationContext(), R.string.failed_subscribe_push_notification, Toast.LENGTH_LONG).show();
        signOut();
    }

    @Override
    public void onDeleteKeyHandle(byte[] keyHandle) {
        dataStore.deleteTokenEntry(keyHandle);
    }

    public static String getResourceString(int resourceID) {
        return context.getString(resourceID);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }

        if (mStateManager.getCurrent().isAuthorized()) {
            displayAuthorized();
            return;
        }

        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            mStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            mStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
        } else {
            displayNotAuthorized("No authorization state retained - reauthorization required");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        // user info is retained to survive activity restarts, such as when rotating the
        // device or switching apps. This isn't essential, but it helps provide a less
        // jarring UX when these events occur - data does not just disappear from the view.
        if (mUserInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString());
        }
    }

    @Override
    protected void onDestroy() {
        if(mAuthService!=null)
        mAuthService.dispose();
        if(mExecutor!=null)
        mExecutor.shutdownNow();
        super.onDestroy();

    }

    @MainThread
    private void displayNotAuthorized(String explanation) {
        findViewById(R.id.not_authorized).setVisibility(View.VISIBLE);
        findViewById(R.id.authorized).setVisibility(View.GONE);
        findViewById(R.id.loading_container).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.explanation)).setText(explanation);
        findViewById(R.id.reauth).setOnClickListener((View view) -> signOut());
    }

    @MainThread
    private void displayLoading(String message) {
        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
        findViewById(R.id.authorized).setVisibility(View.GONE);
        findViewById(R.id.not_authorized).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.loading_description)).setText(message);
    }

    @MainThread
    private void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);

        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    @MainThread
    private void displayAuthorized() {
        findViewById(R.id.authorized).setVisibility(View.VISIBLE);
        findViewById(R.id.not_authorized).setVisibility(View.GONE);
        findViewById(R.id.loading_container).setVisibility(View.GONE);

        AuthState state = mStateManager.getCurrent();

        TextView refreshTokenInfoView = (TextView) findViewById(R.id.refresh_token_info);
        refreshTokenInfoView.setText((state.getRefreshToken() == null)
                ? R.string.no_refresh_token_returned
                : R.string.refresh_token_returned);

        TextView idTokenInfoView = (TextView) findViewById(R.id.id_token_info);
        idTokenInfoView.setText((state.getIdToken()) == null
                ? R.string.no_id_token_returned
                : R.string.id_token_returned);

        TextView accessTokenInfoView = (TextView) findViewById(R.id.access_token_info);
        if (state.getAccessToken() == null) {
            accessTokenInfoView.setText(R.string.no_access_token_returned);
        } else {
            Long expiresAt = state.getAccessTokenExpirationTime();
            if (expiresAt == null) {
                accessTokenInfoView.setText(R.string.no_access_token_expiry);
            } else if (expiresAt < System.currentTimeMillis()) {
                accessTokenInfoView.setText(R.string.access_token_expired);
            } else {
                String template = getResources().getString(R.string.access_token_expires_at);
                accessTokenInfoView.setText(String.format(template,
                        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss ZZ").print(expiresAt)));
            }
        }

        Button refreshTokenButton = (Button) findViewById(R.id.refresh_token);
        refreshTokenButton.setVisibility(state.getRefreshToken() != null
                ? View.VISIBLE
                : View.GONE);
        refreshTokenButton.setOnClickListener((View view) -> refreshAccessToken());

        Button viewProfileButton = (Button) findViewById(R.id.view_profile);

        AuthorizationServiceDiscovery discoveryDoc =
                state.getAuthorizationServiceConfiguration().discoveryDoc;
        if (discoveryDoc == null || discoveryDoc.getUserinfoEndpoint() == null) {
            viewProfileButton.setVisibility(View.GONE);
        } else {
            fetchUserInfo();
            viewProfileButton.setVisibility(View.GONE);
            viewProfileButton.setOnClickListener((View view) -> fetchUserInfo());
        }

        ((Button) findViewById(R.id.sign_out)).setOnClickListener((View view) -> signOut());

        View userInfoCard = findViewById(R.id.userinfo_card);
        JSONObject userInfo = mUserInfoJson.get();
        if (userInfo == null) {
            userInfoCard.setVisibility(View.INVISIBLE);
        } else {
            try {
                String name = "???";
                if (userInfo.has("name")) {
                    name = userInfo.getString("name");
                }
                ((TextView) findViewById(R.id.userinfo_name)).setText(name);

                if (userInfo.has("picture")) {
                    Glide.with(U2FActivity.this)
                            .load(Uri.parse(userInfo.getString("picture")))
                            .fitCenter()
                            .into((ImageView) findViewById(R.id.userinfo_profile));
                }

                ((TextView) findViewById(R.id.userinfo_json)).setText(mUserInfoJson.toString());
                userInfoCard.setVisibility(View.GONE);
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to read userinfo JSON", ex);
            }
        }
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        Application.U2F_State = authorizationResponse.additionalParameters.get("session_id");
        displayLoading("Exchanging authorization code");
        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @MainThread
    private void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            displayNotAuthorized("Client authentication method is unsupported");
            return;
        }

        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }

    @MainThread
    private void performTokenValidation(
            net.gluu.appauth.TokenResponse response,
            AuthorizationService.TokenValidationResponseCallback callback) {

        mAuthService.performTokenValidation(
                response,
                callback);
    }

    @WorkerThread
    private void handleAccessTokenResponse(
            @Nullable net.gluu.appauth.TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        runOnUiThread(this::displayAuthorized);
    }

    @MainThread
    private void handleCodeExchangeResponse(
            @Nullable net.gluu.appauth.TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {

        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        if (!mStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed"
                    + ((authException != null) ? authException.error : "");

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            runOnUiThread(() -> displayNotAuthorized(message));
        } else {
            if (tokenResponse != null
                    && tokenResponse.idToken != null
                    && tokenResponse.request.configuration.discoveryDoc != null) {
                performTokenValidation(
                        tokenResponse,
                        this::handleTokenValidationResponse);
            } else {
                final String message = "Failed to perform id_token validation";

                // WrongThread inference is incorrect for lambdas
                //noinspection WrongThread
                runOnUiThread(() -> displayNotAuthorized(message));
            }
        }
    }

    @WorkerThread
    private void handleTokenValidationResponse(
            boolean isTokenValid,
            @Nullable AuthorizationException authException) {
        if (isTokenValid) {
            runOnUiThread(this::displayAuthorized);
        } else {
            final String message = "Invalid id_token";

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            runOnUiThread(() -> displayNotAuthorized(message));
        }
    }

    /**
     * Demonstrates the use of {@link AuthState#performActionWithFreshTokens} to retrieve
     * user info from the IDP's user info endpoint. This callback will negotiate a new access
     * token / id token for use in a follow-up action, or provide an error if this fails.
     */
    @MainThread
    private void fetchUserInfo() {
        displayLoading("Fetching user info");
        mStateManager.getCurrent().performActionWithFreshTokens(mAuthService, this::fetchUserInfo);
    }

    @MainThread
    private void fetchUserInfo(String accessToken, String idToken, AuthorizationException ex) {
        if (ex != null) {
            Log.e(TAG, "Token refresh failed when fetching user info");
            mUserInfoJson.set(null);
            runOnUiThread(this::displayAuthorized);
            return;
        }

        AuthorizationServiceDiscovery discovery =
                mStateManager.getCurrent()
                        .getAuthorizationServiceConfiguration()
                        .discoveryDoc;

        URL userInfoEndpoint;
        try {
            userInfoEndpoint = new URL(discovery.getUserinfoEndpoint().toString());
        } catch (MalformedURLException urlEx) {
            Log.e(TAG, "Failed to construct user info endpoint URL", urlEx);
            mUserInfoJson.set(null);
            runOnUiThread(this::displayAuthorized);
            return;
        }

        mExecutor.submit(() -> {
            try {
                HttpURLConnection conn =
                        (HttpURLConnection) userInfoEndpoint.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setInstanceFollowRedirects(false);
                String response = Okio.buffer(Okio.source(conn.getInputStream()))
                        .readString(Charset.forName("UTF-8"));
                mUserInfoJson.set(new JSONObject(response));
                Application.U2F_Username = mUserInfoJson.get().getString("user_name");
                Log.v("TAG", "userinfo:" + response);
            } catch (IOException ioEx) {
                Log.e(TAG, "Network error when querying userinfo endpoint", ioEx);
                showSnackbar("Fetching user info failed");
            } catch (JSONException jsonEx) {
                Log.e(TAG, "Failed to parse userinfo response");
                showSnackbar("Failed to parse user info");
            }

            runOnUiThread(this::displayU2FDetails);
        });
    }

    @MainThread
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.coordinator),
                message,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @MainThread
    private void refreshAccessToken() {
        displayLoading("Refreshing access token");
        performTokenRequest(
                mStateManager.getCurrent().createTokenRefreshRequest(),
                this::handleAccessTokenResponse);
    }

    @MainThread
    private void displayU2FDetails() {
        findViewById(R.id.authorized).setVisibility(View.VISIBLE);
        findViewById(R.id.not_authorized).setVisibility(View.GONE);
        findViewById(R.id.loading_container).setVisibility(View.GONE);

        onQrRequest(getRequestData());
    }

    @Override
    public void onBackPressed() {
    }
}
