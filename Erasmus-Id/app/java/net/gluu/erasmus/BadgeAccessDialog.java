package net.gluu.erasmus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badge_access_dialog);

        mObjAPI = APIService.createService(APIInterface.class);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Setting permission...");

        String message = getIntent().getStringExtra(getString(R.string.key_message));
        String badgeTitle = getIntent().getStringExtra(getString(R.string.key_badge_title));
        badge = getIntent().getStringExtra(getString(R.string.key_badge));
        ((TextView) findViewById(R.id.tvMsg)).setText(message + " (" + badgeTitle + ")");
        findViewById(R.id.btnAllow).setOnClickListener(this);
        findViewById(R.id.btnDeny).setOnClickListener(this);

//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//        builder.setTitle("ERASMUS");
//        builder.setMessage(message);
//        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Allowed", Toast.LENGTH_LONG).show();
//            }
//        });
//        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAllow:
                setBadgePermission("true");
                break;
            case R.id.btnDeny:
                setBadgePermission("false");
                break;
        }
    }

    private void setBadgePermission(String access) {
        showProgressBar();

        Application.getAccessToken(new AccessToken() {
            @Override
            public void onAccessTokenSuccess(String accessToken) {
                Log.v("TAG", "Access token in setBadgePermission(): " + accessToken);
                SetPermissionRequest setPermissionRequest = new SetPermissionRequest(access, badge, Application.participant.getOpHost());
                Call<BadgeRequest> call = mObjAPI.setsetPermission(accessToken, setPermissionRequest);
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

    private void dismissDialog(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}
