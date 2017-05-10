package net.gluu.erasmus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.gluu.erasmus.model.Recipient;
import net.gluu.erasmus.utils.JWTUtils;

import org.json.JSONObject;

public class DisplayBadgeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_BARCODE_CAPTURE = 101;
    private static final String TAG = "DisplayBadgeActivity";

    TextView mTvBadgeName, mTvUserName, mTvId, mTvExpiresAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_badge);
        initToolbar();
        mTvBadgeName = (TextView) findViewById(R.id.tv_badge_name);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mTvId = (TextView) findViewById(R.id.tv_id);
        mTvExpiresAt = (TextView) findViewById(R.id.tv_exprire_date);

        Glide.with(DisplayBadgeActivity.this)
                .load(Uri.parse(Application.displayBadge.getQrCode()))
                .fitCenter()
                .into((ImageView) findViewById(R.id.iv_qrcode));

        mTvBadgeName.setText(Application.displayBadge.getBadgeTitle());
        mTvExpiresAt.setText(Application.displayBadge.getExpiresAt());

        Recipient recipient = Application.displayBadge.getRecipient();
        if (recipient != null) {
            try {
                String userInfo = JWTUtils.decoded(recipient.getIdentity());
                Log.v("TAG", "User info is:" + userInfo);
                if (userInfo != null && userInfo.length() > 0) {
                    JSONObject jObj = new JSONObject(userInfo);
                    JSONObject jsonObject = new JSONObject(jObj.getString("userinfo"));
                    mTvUserName.setText(jsonObject.getString("name") == null ? "" : jsonObject.getString("name"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.badge_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
//            case R.id.btn_scan:
//                Intent intent = new Intent(getApplicationContext(), SimpleScannerActivity.class);
////                intent.putExtra(QRCaptureActivity.AutoFocus, true);
////                intent.putExtra(QRCaptureActivity.UseFlash, false);
//                startActivityForResult(intent, RC_BARCODE_CAPTURE);
//                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_BARCODE_CAPTURE) {
//            if (resultCode == CommonStatusCodes.SUCCESS_CACHE) {
//                if (data != null) {
//                    Barcode barcode = data.getParcelableExtra(QRCaptureActivity.BarcodeObject);
//                    String strResponse = barcode.displayValue;
//                    if (strResponse != null) {
//
//                    }
//                } else {
//                    Log.d(TAG, "No barcode captured, intent data is null");
//                }
//            } else {
//            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
