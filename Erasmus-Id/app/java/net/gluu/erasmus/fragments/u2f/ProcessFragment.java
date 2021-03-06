/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package net.gluu.erasmus.fragments.u2f;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import net.gluu.appauth.AuthState;
import net.gluu.erasmus.Application;
import net.gluu.erasmus.AuthStateManager;
import net.gluu.erasmus.BadgeStatusActivity;
import net.gluu.erasmus.BuildConfig;
import net.gluu.erasmus.LoginActivity;
import net.gluu.erasmus.R;
import net.gluu.erasmus.listener.OxPush2RequestListener;
import net.gluu.erasmus.model.u2f.OxPush2Request;
import net.gluu.erasmus.model.u2f.U2fMetaData;
import net.gluu.erasmus.model.u2f.U2fOperationResult;
import net.gluu.erasmus.net.CommunicationService;
import net.gluu.erasmus.u2f.v2.exception.U2FException;
import net.gluu.erasmus.u2f.v2.model.TokenResponse;
import net.gluu.erasmus.u2f.v2.store.DataStore;
import net.gluu.erasmus.utils.Utils;

import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Process Fido U2F request fragment
 * <p>
 * Created by Yuriy Movchan on 01/07/2016.
 */
public class ProcessFragment extends Fragment implements View.OnClickListener {

    SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    SimpleDateFormat userDateTimeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private static final String TAG = "TAG";

    private static final String ARG_PARAM1 = "oxPush2Request";

    private OxPush2Request oxPush2Request;

    private OxPush2RequestListener oxPush2RequestListener;

    private DataStore dataStore;

    public ProcessFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProcessFragment.
     */
    public static ProcessFragment newInstance(String oxPush2RequestJson) {
        ProcessFragment fragment = new ProcessFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, oxPush2RequestJson);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String oxPush2RequestJson = getArguments().getString(ARG_PARAM1);

            oxPush2Request = new Gson().fromJson(oxPush2RequestJson, OxPush2Request.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_process, container, false);

        view.findViewById(R.id.button_approve).setOnClickListener(this);
        view.findViewById(R.id.button_decline).setOnClickListener(this);

        updateRequestDetails(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OxPush2RequestListener) {
            oxPush2RequestListener = (OxPush2RequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must teaimplement OxPush2RequestListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        oxPush2RequestListener = null;
    }

    @Override
    public void onClick(View v) {
        if (oxPush2RequestListener == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.button_approve:
                onOxPushApproveRequest(false);
                //// TODO: 19/6/17 only for testing
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent i=new Intent(getActivity(), BadgeStatusActivity.class);
//                        startActivity(i);
//                    }
//                }, 2000);
//                    @Override
                break;
            case R.id.button_decline:
                onOxPushApproveRequest(true);
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        Intent i=new Intent(getActivity(), BadgeStatusActivity.class);
//                        startActivity(i);
//                    }
//                }, 2000);
                break;
        }
    }

    private void runOnUiThread(Runnable runnable) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(runnable);
        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "Activity is null!");
        }
    }

    private void updateRequestDetails(View view) {
        Date createdDate = null;
        String oxPushCreated = oxPush2Request.getCreated();
        if (Utils.isNotEmpty(oxPushCreated)) {
            try {
                createdDate = isoDateTimeFormat.parse(oxPushCreated);
            } catch (ParseException ex) {
                Log.e(TAG, "Failed to parse ISO date/time: " + oxPushCreated, ex);
            }
        }

        String createdString = "";
        if (createdDate != null) {
            createdString = userDateTimeFormat.format(createdDate);
        }

        final boolean oneStep = Utils.isEmpty(oxPush2Request.getUserName());
        final int authenticationType = oneStep ? R.string.one_step : R.string.two_step;

        ((TextView) view.findViewById(R.id.text_application_value)).setText(oxPush2Request.getApp());
        ((TextView) view.findViewById(R.id.text_issuer_value)).setText(oxPush2Request.getIssuer());
        ((TextView) view.findViewById(R.id.text_created_value)).setText(createdString);
        ((TextView) view.findViewById(R.id.text_authentication_type_value)).setText(authenticationType);
        ((TextView) view.findViewById(R.id.text_authentication_method_value)).setText(oxPush2Request.getMethod());
        ((TextView) view.findViewById(R.id.text_user_name_label_value)).setText(oxPush2Request.getUserName());
    }

    private void setFinalStatus(int statusId) {
        ((TextView) getView().findViewById(R.id.status_text)).setText(statusId);
        getView().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    private void setErrorStatus(Exception ex) {
        ((TextView) getView().findViewById(R.id.status_text)).setText(ex.getMessage());
        getView().findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }

    private void onOxPushApproveRequest(final Boolean isDeny) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().findViewById(R.id.action_button_group).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.status_text).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                ((TextView) getView().findViewById(R.id.status_text)).setText(R.string.process_u2f_start);
            }
        });



        if (oxPush2Request != null) {
            final boolean oneStep = Utils.isEmpty(oxPush2Request.getUserName());

            final Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("application", oxPush2Request.getApp());
            if (!oneStep) {
                parameters.put("username", oxPush2Request.getUserName());
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final U2fMetaData u2fMetaData = getU2fMetaData();

                    dataStore = oxPush2RequestListener.onGetDataStore();
                        final List<byte[]> keyHandles = dataStore.getKeyHandlesByIssuerAndAppId(oxPush2Request.getIssuer(), oxPush2Request.getApp());

                        final boolean isEnroll = StringUtils.equals(oxPush2Request.getMethod(), "enroll");//(keyHandles.size() == 0) ||
                        final String u2fEndpoint;
                        if (isEnroll) {
                            u2fEndpoint = u2fMetaData.getRegistrationEndpoint();
                            if (BuildConfig.DEBUG) Log.i(TAG, "Authentication method: enroll");
                        } else {
                            u2fEndpoint = u2fMetaData.getAuthenticationEndpoint();
                            if (BuildConfig.DEBUG)
                                Log.i(TAG, "Authentication method: authenticate");
                        }

                        //Check if we're using cred manager - in that case "state"== null and we should use "enrollment" parameter
                        if (oxPush2Request.getState() == null){//using cred-manager
                            parameters.put("enrollment_code", oxPush2Request.getEnrollment());
                        } else {//using standard workflow
                            //Check is old or new version of server
                            String state_key = u2fEndpoint.contains("seam") ? "session_state" : "session_id";
                            parameters.put(state_key, oxPush2Request.getState());
                        }

                        final String challengeJsonResponse;
                        if (oneStep && (keyHandles.size() > 0)) {
                            // Try to get challenge using all keyHandles associated with issuer and application

                            String validChallengeJsonResponse = null;
                            for (byte[] keyHandle : keyHandles) {
                                parameters.put("keyhandle", Utils.base64UrlEncode(keyHandle));
                                try {
                                    validChallengeJsonResponse = CommunicationService.get(u2fEndpoint, parameters);
                                    break;
                                } catch (FileNotFoundException ex) {
                                    Log.i(TAG, "Found invalid keyHandle: " + Utils.base64UrlEncode(keyHandle));
                                }
                            }

                            challengeJsonResponse = validChallengeJsonResponse;
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "Get U2F JSON response: " + challengeJsonResponse);

                        } else {
                            challengeJsonResponse = CommunicationService.get(u2fEndpoint, parameters);
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "Get U2F JSON response: " + challengeJsonResponse);
                        }

                        if (Utils.isEmpty(challengeJsonResponse)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setFinalStatus(R.string.no_valid_key_handles);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        onChallengeReceived(isEnroll, u2fMetaData, u2fEndpoint, challengeJsonResponse, isDeny);
                                    } catch (Exception ex) {
                                        Log.e(TAG, "Failed to process challengeJsonResponse: " + challengeJsonResponse, ex);
                                        setFinalStatus(R.string.failed_process_challenge);
                                        if (BuildConfig.DEBUG) setErrorStatus(ex);
                                        signOut();
                                    }
                                }
                            });
                        }
                    } catch (final Exception ex) {
                        Log.e(TAG, ex.getLocalizedMessage(), ex);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setFinalStatus(R.string.wrong_u2f_metadata);
                                if (BuildConfig.DEBUG) setErrorStatus(ex);
//                                signOut();
                            }
                        });
                    }
                }
            }).start();
        }    }

    @MainThread
    private void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthStateManager mStateManager = AuthStateManager.getInstance(ProcessFragment.this.getActivity());

        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);

        Intent mainIntent = new Intent(getActivity(), LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        getActivity().finish();
    }

    private U2fMetaData getU2fMetaData() throws IOException {
        // Request U2f meta data
        String discoveryUrl = oxPush2Request.getIssuer();
        if (BuildConfig.DEBUG && discoveryUrl.contains(":8443")) {
            discoveryUrl += "/oxauth/seam/resource/restv1/oxauth/fido-u2f-configuration";
        } else {
            discoveryUrl += "/.well-known/fido-u2f-configuration";
        }

        if (BuildConfig.DEBUG) Log.i(TAG, "Attempting to load U2F metadata from: " + discoveryUrl);

        final String discoveryJson = CommunicationService.get(discoveryUrl, null);
        final U2fMetaData u2fMetaData = new Gson().fromJson(discoveryJson, U2fMetaData.class);

        if (BuildConfig.DEBUG) Log.i(TAG, "Loaded U2f metadata: " + u2fMetaData);

        return u2fMetaData;
    }

    private void onChallengeReceived(boolean isEnroll, final U2fMetaData u2fMetaData, final String u2fEndpoint, final String challengeJson, final Boolean isDeny) throws IOException, JSONException, U2FException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) getView().findViewById(R.id.status_text)).setText(R.string.process_u2f_request);
            }
        });

        final TokenResponse tokenResponse;
        if (isEnroll) {
            tokenResponse = oxPush2RequestListener.onEnroll(challengeJson, oxPush2Request, isDeny);
        } else {
            tokenResponse = oxPush2RequestListener.onSign(challengeJson, u2fMetaData.getIssuer(), isDeny);
        }

        if (tokenResponse == null) {
            if (BuildConfig.DEBUG) Log.e(TAG, "Token response is empty");
            setFinalStatus(R.string.wrong_token_response);
            return;
        }

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", oxPush2Request.getUserName());
        parameters.put("tokenResponse", tokenResponse.getResponse());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String resultJsonResponse = CommunicationService.post(u2fEndpoint, parameters);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "Get U2F JSON result response: " + resultJsonResponse);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final U2fOperationResult u2fOperationResult = new Gson().fromJson(resultJsonResponse, U2fOperationResult.class);
                                if (BuildConfig.DEBUG)
                                    Log.i(TAG, "Get U2f operation result: " + u2fOperationResult);

                                handleResult(u2fMetaData, tokenResponse, u2fOperationResult, isDeny);
                            } catch (Exception ex) {
                                Log.e(TAG, "Failed to process resultJsonResponse: " + resultJsonResponse, ex);
                                setFinalStatus(R.string.failed_process_status);
                            }
                        }
                    });
                } catch (final Exception ex) {
                    Log.e(TAG, "Failed to send Fido U2F response", ex);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setFinalStatus(R.string.failed_process_response);
                            if (BuildConfig.DEBUG) setErrorStatus(ex);
                        }
                    });
                }
            }
        }).start();
    }

    private void handleResult(U2fMetaData u2fMetaData, TokenResponse tokenResponse, U2fOperationResult u2fOperationResult, Boolean isDeny) {
        if (!StringUtils.equals(tokenResponse.getChallenge(), u2fOperationResult.getChallenge())) {
            setFinalStatus(R.string.challenge_doesnt_match);
        }

        if (StringUtils.equals("success", u2fOperationResult.getStatus())) {
            int message = R.string.auth_result_success;
            if (isDeny) {
                message = R.string.enroll_result_success;
            }
            setFinalStatus(message);

            ((TextView) getView().findViewById(R.id.status_text)).setText(getString(message) + ". Server: " + u2fMetaData.getIssuer());
        } else {
            setFinalStatus(R.string.auth_result_failed);
        }

        new Handler().postDelayed(() -> {
            Intent i = new Intent(getActivity(), BadgeStatusActivity.class);
            startActivity(i);
        }, 2000);
    }
}
