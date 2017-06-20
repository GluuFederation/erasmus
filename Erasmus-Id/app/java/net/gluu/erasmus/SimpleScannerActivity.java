package net.gluu.erasmus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.api.AccessToken;
import net.gluu.erasmus.model.BadgeRequest;
import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.NotificationRequest;
import net.gluu.erasmus.model.ScanResponse;
import net.gluu.erasmus.model.ScanResponseSuccess;
import net.gluu.erasmus.model.SetPermissionRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static final String TAG = "TAG";
    APIInterface mObjAPI;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mObjAPI = APIService.createService(APIInterface.class);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("TAG", "Scan result: " + rawResult.getText()); // Prints scan results
        Log.v("TAG", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        sendNotification(rawResult.getText());
    }

    private void sendDataBack(Result barcode) {
        if (barcode != null) {
            Intent data = getIntent();
            data.putExtra("barcode", barcode.getText());
            setResult(Activity.RESULT_OK, data);
            finish();
        } else {
            Log.d("SimpleScannerActivity", "barcode data is null");
        }
    }

    private void resumeCameraPreview() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerActivity.this);
            }
        }, 3000);
        // If you would like to resume scanning, call this method below:
    }

    private void sendNotification(String badge) {
        if (Application.checkInternetConnection(SimpleScannerActivity.this)) {
            Application.getAccessToken(new AccessToken() {
                @Override
                public void onAccessTokenSuccess(String accessToken) {
                    Log.v("TAG", "Access token in sendNotification(): " + accessToken);
                    NotificationRequest notificationRequest = new NotificationRequest(badge, Application.participant.getOpHost(), Application.participant.getName());
                    Call<BadgeRequest> call = mObjAPI.sendNotification(accessToken, notificationRequest);
                    call.enqueue(new Callback<BadgeRequest>() {
                        @Override
                        public void onResponse(Call<BadgeRequest> call, Response<BadgeRequest> response) {
                            if (response.errorBody() == null && response.body() != null) {
                                BadgeRequest objResponse = response.body();

                                if (objResponse != null) {
                                    if (objResponse.getError()) {
                                        Log.v("TAG", "send notification response:" + objResponse.getErrorMsg());
                                    } else {
                                        Log.v("TAG", "send notification response:" + objResponse.getMessage());
                                    }
                                }
                            } else {
                                Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                                try {
                                    String error = response.errorBody().string();
                                    Log.v("TAG", "Error in sending notification :" + error);
                                    JSONObject jsonObjectError = new JSONObject(error);
                                    if (jsonObjectError.getBoolean("error")) {
                                        Log.v("TAG", "Error in sending notification :" + jsonObjectError.getString("errorMsg"));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<BadgeRequest> call, Throwable t) {
                            Log.v("TAG", "Response send notification failure" + t.getMessage());
                        }
                    });
                }

                @Override
                public void onAccessTokenFailure() {
                    Log.v("TAG", "Failed to get access token in sendNotification()");
                    Application.showAutoDismissAlertDialog(SimpleScannerActivity.this, getString(R.string.unable_to_process));
                }
            });
        } else {
            Log.v("TAG", getString(R.string.no_internet));
        }
    }
}
