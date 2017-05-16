package net.gluu.erasmus;

import android.app.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class ScanSuccessActivity extends AppCompatActivity {
    ImageView badgeImg;
    TextView badgeName, userName, opName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_success);
        initToolbar();
        init();
        setData();
    }

    private void setData() {

        Glide.with(this)
                .load(Application.scanResponseSuccess.getBadge().getImage())
                .into(badgeImg);
        badgeName.setText(Application.scanResponseSuccess.getBadge().getName());
        userName.setText(Application.scanResponseSuccess.getBadge().getIssuer().getName());
      //  opName.setText();

    }

    private void init() {
        badgeImg = (ImageView) findViewById(R.id.iv_user_img);
        badgeName = (TextView) findViewById(R.id.tv_badge_name);
        userName = (TextView) findViewById(R.id.tv_user_name);
        opName = (TextView) findViewById(R.id.tv_op_name);

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.badge_detail);
    }
}
