package net.gluu.erasmus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.api.AccessToken;
import net.gluu.erasmus.model.BadgeRequest;
import net.gluu.erasmus.model.SetPermissionRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BadgeAccessDialog extends Activity implements View.OnClickListener {

    private String badge;
    APIInterface mObjAPI;
    ProgressDialog mProgress;
    boolean mIsNotify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badge_access_dialog);

        String message = getIntent().getStringExtra(getString(R.string.key_message));

        if (message.contains("verified")) {
            ((TextView) findViewById(R.id.tvMsg)).setText(message);
            ((Button) findViewById(R.id.btnAllow)).setText(getString(R.string.ok));
            findViewById(R.id.btnAllow).setOnClickListener(this);
            findViewById(R.id.btnDeny).setVisibility(View.GONE);
            mIsNotify = true;
        } else if (message.contains("revoked")) {
            ((TextView) findViewById(R.id.tvMsg)).setText(getString(R.string.badge_permission_denied));
            ((Button) findViewById(R.id.btnAllow)).setText(getString(R.string.ok));
            findViewById(R.id.btnAllow).setOnClickListener(this);
            findViewById(R.id.btnDeny).setVisibility(View.GONE);
            mIsNotify = true;
        } else {
            mIsNotify = false;
            mObjAPI = APIService.createService(APIInterface.class);
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Setting permission...");

            String badgeTitle = getIntent().getStringExtra(getString(R.string.key_badge_title));
            badge = getIntent().getStringExtra(getString(R.string.key_badge));
            ((TextView) findViewById(R.id.tvMsg)).setText(message + " (" + badgeTitle + ")");
            findViewById(R.id.btnAllow).setOnClickListener(this);
            findViewById(R.id.btnDeny).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAllow:
                if (mIsNotify) {
                    dismissDialog();
                    startActivity(new Intent(BadgeAccessDialog.this, BadgeStatusActivity.class));
                } else
                    setBadgePermission("true");
                break;
            case R.id.btnDeny:
                setBadgePermission("false");
                break;
        }
    }

    private void setBadgePermission(String access) {
        if (Application.checkInternetConnection(BadgeAccessDialog.this)) {
            showProgressBar();

            Application.getAccessToken(new AccessToken() {
                @Override
                public void onAccessTokenSuccess(String accessToken) {
                    Log.v("TAG", "Access token in setBadgePermission(): " + accessToken);
                    SetPermissionRequest setPermissionRequest = new SetPermissionRequest(access, badge, Application.participant.getOpHost());
                    Call<BadgeRequest> call = mObjAPI.setBadgePermission(accessToken, setPermissionRequest);
                    call.enqueue(new Callback<BadgeRequest>() {
                        @Override
                        public void onResponse(Call<BadgeRequest> call, Response<BadgeRequest> response) {
                            hideProgressBar();
                            if (response.errorBody() == null && response.body() != null) {
                                BadgeRequest objResponse = response.body();

                                if (objResponse != null) {
                                    if (objResponse.getError()) {
                                        Application.showAutoDismissAlertDialog(BadgeAccessDialog.this, objResponse.getErrorMsg());
                                    } else {
                                        Application.showAutoDismissAlertDialog(BadgeAccessDialog.this, objResponse.getMessage());
                                    }
                                }
                            } else {
                                Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                                try {
                                    String error = response.errorBody().string();
                                    Log.v("TAG", "Error in setting badge permission :" + error);
                                    JSONObject jsonObjectError = new JSONObject(error);
                                    if (jsonObjectError.getBoolean("error")) {
                                        Application.showAutoDismissAlertDialog(BadgeAccessDialog.this, jsonObjectError.getString("errorMsg"));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            dismissDialog();
                        }

                        @Override
                        public void onFailure(Call<BadgeRequest> call, Throwable t) {
                            Log.v("TAG", "Response setting badge permission failure" + t.getMessage());
                            hideProgressBar();
                            dismissDialog();
                        }
                    });
                }

                @Override
                public void onAccessTokenFailure() {
                    Log.v("TAG", "Failed to get access token in setBadgePermission()");
                    hideProgressBar();
                    Application.showAutoDismissAlertDialog(BadgeAccessDialog.this, getString(R.string.unable_to_process));
                    dismissDialog();
                }
            });
        } else {
            Log.v("TAG", getString(R.string.no_internet));
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

    private void dismissDialog() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}
