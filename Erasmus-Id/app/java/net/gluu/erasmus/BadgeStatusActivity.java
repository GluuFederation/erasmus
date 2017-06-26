package net.gluu.erasmus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.gluu.erasmus.adapters.BadgePagerAdapter;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.api.AccessToken;
import net.gluu.erasmus.fragments.ApproveBadgeFragment;
import net.gluu.erasmus.fragments.PendingBadgeFragment;
import net.gluu.erasmus.model.APIBadgeRequest;
import net.gluu.erasmus.model.BadgeRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Meghna Joshi on 20/4/17.
 */
public class BadgeStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_REQUEST_CODE = 111;
    BadgePagerAdapter pagerAdapter;
    List<Fragment> fragments;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    LinearLayout llBottomMenu;
    TextView tvScan, tvLog, tvSetting, tvAbout;
    ProgressDialog mProgress;
    APIInterface mObjAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_status);

        mObjAPI = APIService.createService(APIInterface.class);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading badges..");

        initViews();
        initListeners();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_badge_status, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_badge) {
            Intent i = new Intent(getApplicationContext(), RequestBadgeActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListeners() {
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //noinspection deprecation
        mTabLayout.setOnTabSelectedListener(new TabSelectedListener());
    }

    private void init() {
        initTabs();
        initViewPager();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.badge_status);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        llBottomMenu = (LinearLayout) findViewById(R.id.ll_bottom_menu);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvLog = (TextView) findViewById(R.id.tv_logs);
        tvScan = (TextView) findViewById(R.id.tv_scan);
        tvSetting = (TextView) findViewById(R.id.tv_settings);
        tvScan.setOnClickListener(this);
    }

    private void initTabs() {
        //noinspection deprecation
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        mTabLayout.addTab(mTabLayout.newTab().setText("Approved Badges"), 0, true);
        mTabLayout.addTab(mTabLayout.newTab().setText("Pending Bagdes"), 1, false);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initViewPager() {
        getBadgeRequests();
    }

    private void getFragments() {
        ApproveBadgeFragment approveBadgeFragment = ApproveBadgeFragment.newInstance("", "");
        PendingBadgeFragment pendingBadgeFragment = PendingBadgeFragment.newInstance("", "");
        fragments = new ArrayList<>();
        fragments.add(approveBadgeFragment);
        fragments.add(pendingBadgeFragment);

        pagerAdapter = new BadgePagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_scan:
                checkPermission();
                break;
        }
    }

    private class TabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                Intent i = new Intent(BadgeStatusActivity.this, SimpleScannerActivity.class);
                startActivity(i);
            } else if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> strings = new ArrayList<>();

                if ((checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)) {
                    strings.add(Manifest.permission.CAMERA);
                }

                String[] strs = new String[strings.size()];
                int i = 0;
                for (String str : strings) {
                    strs[i] = str;
                    i++;
                }
                requestPermissions(strs, MY_REQUEST_CODE);
            }
        } else {
            Intent i = new Intent(BadgeStatusActivity.this, SimpleScannerActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionDenied = false;
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        permissionDenied = true;
                        break;
                    }
                }
            }
            if (!permissionDenied) {
                Intent i = new Intent(BadgeStatusActivity.this, SimpleScannerActivity.class);
                startActivity(i);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.app_name) + getString(R.string.require_permission));
                builder.setPositiveButton(R.string.retry, (dialog, which) -> checkPermission());

                builder.setNegativeButton(R.string.exit, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

                builder.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void getBadgeRequests() {
        if (Application.checkInternetConnection(this)) {

            showProgressBar();

            Application.getAccessToken(new AccessToken() {
                @Override
                public void onAccessTokenSuccess(String accessToken) {
                    Log.v("TAG", "Access token in getBadgeRequests(): " + accessToken);
                    APIBadgeRequest badgeRequest = new APIBadgeRequest(Application.participant.getOpHost(), "All");
                    Call<BadgeRequest> call = mObjAPI.getBadgeRequests(accessToken, badgeRequest);
                    call.enqueue(new Callback<BadgeRequest>() {
                        @Override
                        public void onResponse(Call<BadgeRequest> call, Response<BadgeRequest> response) {
                            hideProgressBar();
                            if (response.errorBody() == null && response.body() != null) {
                                BadgeRequest objResponse = response.body();

                                if (objResponse != null) {
                                    if (objResponse.getError()) {
                                        Log.v("TAG", "Error in retrieving badge requests");
                                        Application.showAutoDismissAlertDialog(BadgeStatusActivity.this, objResponse.getErrorMsg());
                                    } else {
                                        Log.v("TAG", "approved badge requests retrieved:" + objResponse.getBadgeRequests().getApprovedBadgeRequests().size());
                                        Log.v("TAG", "pending badge requests retrieved:" + objResponse.getBadgeRequests().getPendingBadgeRequests().size());
                                        Application.approvedBadgeRequests = objResponse.getBadgeRequests().getApprovedBadgeRequests();
                                        Application.pendingBadgeRequests = objResponse.getBadgeRequests().getPendingBadgeRequests();
                                    }
                                }
                            } else {
                                Log.v("TAG", "Error from server in retrieving badge requests:" + response.errorBody());
                                Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                            }
                            getFragments();
                        }

                        @Override
                        public void onFailure(Call<BadgeRequest> call, Throwable t) {
                            Log.v("TAG", "Response retrieving badge requests failure" + t.getMessage());
                            hideProgressBar();
                            getFragments();
                        }
                    });
                }

                @Override
                public void onAccessTokenFailure() {
                    Log.v("TAG", "Failed to get access token in getBadgeRequests()");
                    hideProgressBar();
                    Application.showAutoDismissAlertDialog(BadgeStatusActivity.this, getString(R.string.unable_to_process));
                }
            });
        } else {
            Application.showAutoDismissAlertDialog(BadgeStatusActivity.this, getString(R.string.no_internet));
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

}
