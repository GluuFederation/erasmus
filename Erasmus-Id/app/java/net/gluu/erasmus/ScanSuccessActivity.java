package net.gluu.erasmus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.gluu.erasmus.model.UserInfo;


public class ScanSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_success);
        initToolbar();
        setData();
    }

    private void setData() {

        ((TextView) findViewById(R.id.tv_badge_name)).setText(Application.scanResponseSuccess.getBadge().getName());
        ((TextView) findViewById(R.id.tv_op_name)).setText(Application.participant.getName());
        UserInfo userInfo = Application.getUserInfo(Application.scanResponseSuccess.getRecipient());
        if (userInfo != null) {

            Glide.with(this)
                    .load(userInfo.getPicture())
                    .into((ImageView) findViewById(R.id.iv_user_img));

            ((TextView) findViewById(R.id.tv_user_name)).setText(userInfo.getName());
            ((TextView) findViewById(R.id.tvGender)).setText(userInfo.getGender());
            ((TextView) findViewById(R.id.tvBirthdate)).setText(userInfo.getBirthdate());
            ((TextView) findViewById(R.id.tvEmail)).setText(userInfo.getEmail());
            ((TextView) findViewById(R.id.tvProfile)).setText(userInfo.getProfile());
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.badge_detail);
    }
}
