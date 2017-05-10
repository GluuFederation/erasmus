package net.gluu.erasmus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.ScanResponse;
import net.gluu.erasmus.model.ScanResponseSuccess;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static final String TAG = "SimpleScannerActivity";
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
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
        Log.v("SimpleScannerActivity", rawResult.getText()); // Prints scan results
        Log.v("SimpleScannerActivity", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        sendDataBack(rawResult);

        String url=rawResult.getText();

        Call<ScanResponseSuccess> call = APIService.createService(APIInterface.class).getScanAllResult(url);
        call.enqueue(new Callback<ScanResponseSuccess>() {
            @Override
            public void onResponse(Call<ScanResponseSuccess> call, Response<ScanResponseSuccess> response) {

                if (response.isSuccessful()) {
                    if (response.errorBody() == null && response.body() != null) {
                        ScanResponseSuccess scanResponseObj=response.body();

                        if (scanResponseObj != null) {
                            Application.scanResponseSuccess = scanResponseObj;
                            Intent i = new Intent(SimpleScannerActivity.this, ScanSuccessActivity.class);
                            startActivity(i);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ScanResponseSuccess> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t.getCause());
            }
        });




        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
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

}
