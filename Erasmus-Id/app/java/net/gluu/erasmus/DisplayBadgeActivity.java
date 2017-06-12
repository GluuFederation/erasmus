package net.gluu.erasmus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.gluu.erasmus.model.UserInfo;

public class DisplayBadgeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mTvBadgeName, mTvUserName, mTvId, mTvExpiresAt, mTvCopyURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_badge);
        initToolbar();
        mTvBadgeName = (TextView) findViewById(R.id.tv_badge_name);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mTvId = (TextView) findViewById(R.id.tv_id);
        mTvExpiresAt = (TextView) findViewById(R.id.tv_exprire_date);
        mTvCopyURL = (TextView) findViewById(R.id.tvURL);

        Glide.with(DisplayBadgeActivity.this)
                .load(Uri.parse(Application.displayBadge.getQrCode()))
                .fitCenter()
                .into((ImageView) findViewById(R.id.iv_qrcode));

        mTvBadgeName.setText(Application.displayBadge.getBadgeTitle());
        mTvExpiresAt.setText(Application.displayBadge.getExpiresAt());

        UserInfo userInfo = Application.getUserInfo(Application.displayBadge.getRecipient());
        if (userInfo != null) {
            mTvUserName.setText(userInfo.getName());
            Glide.with(DisplayBadgeActivity.this)
                    .load(Uri.parse(userInfo.getPicture()))
                    .fitCenter()
                    .into((ImageView) findViewById(R.id.iv_user_img));
        }

        copyURLToClipboard("");
        if (Application.displayBadge.getBadgePublicURL() == null || Application.displayBadge.getBadgePublicURL().equalsIgnoreCase("")) {
            mTvCopyURL.setVisibility(View.GONE);
        } else {
            mTvCopyURL.setVisibility(View.VISIBLE);
            mTvCopyURL.setOnClickListener(this);
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
            case R.id.tvURL:
                copyURLToClipboard(Application.displayBadge.getBadgePublicURL());
                Snackbar.make(mTvCopyURL, "URL copied", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void copyURLToClipboard(String url) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("URL", url);
        clipboard.setPrimaryClip(clip);
    }
}
