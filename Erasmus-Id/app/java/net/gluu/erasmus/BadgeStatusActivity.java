package net.gluu.erasmus;

import android.Manifest;
import android.content.DialogInterface;
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
import android.widget.Toast;

import net.gluu.erasmus.adapters.BagdePagerAdapter;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.fragments.ApproveBadgeFragment;
import net.gluu.erasmus.fragments.PendingBadgeFragment;
import net.gluu.erasmus.model.ScanResponse;
import net.gluu.erasmus.model.ScanResponseSuccess;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lcom16 on 20/4/17.
 */

public class BadgeStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_REQUEST_CODE = 111;
    BagdePagerAdapter pagerAdapter;
    private List<Fragment> fragments;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    LinearLayout llBottomMenu;
    TextView tvScan, tvLog, tvSetting, tvAbout;
    private static final int REQUEST_CODE = 121;

    private static final String TAG = "TokenActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_status);

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
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        mTabLayout.addTab(mTabLayout.newTab().setText("Approved Badges"), 0, true);
        mTabLayout.addTab(mTabLayout.newTab().setText("Pending Bagdes"), 1, false);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initViewPager() {
        getFragments();
        pagerAdapter = new BagdePagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pagerAdapter);
    }

    private void getFragments() {
        ApproveBadgeFragment approveBadgeFragment = ApproveBadgeFragment.newInstance("", "");
        PendingBadgeFragment pendingBadgeFragment = PendingBadgeFragment.newInstance("", "");
        fragments = new ArrayList<>();
        fragments.add(approveBadgeFragment);
        fragments.add(pendingBadgeFragment);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {

                String id = data.getStringExtra("barcode");
                Call<ScanResponseSuccess> call = APIService.createService(APIInterface.class).getScanAllResult(id);
                call.enqueue(new Callback<ScanResponseSuccess>() {
                    @Override
                    public void onResponse(Call<ScanResponseSuccess> call, Response<ScanResponseSuccess> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getError()) {
                                Toast.makeText(getApplicationContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                            } else {
                                Application.scanResponseSuccess = response.body();
                                Intent i = new Intent(BadgeStatusActivity.this, ScanSuccessActivity.class);
                                startActivity(i);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ScanResponseSuccess> call, Throwable t) {
                        Log.e(TAG, "onFailure: ", t.getCause());
                    }
                });
            }
        }
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {

                Intent i = new Intent(BadgeStatusActivity.this, SimpleScannerActivity.class);
                startActivityForResult(i, REQUEST_CODE);

            } else if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ArrayList<String> strings = new ArrayList<String>();

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
            startActivityForResult(i, REQUEST_CODE);
        }

        return;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionDenied = false;
        if (requestCode == MY_REQUEST_CODE) {

            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionDenied = true;
                        break;
                    }
                }
            }
            if (!permissionDenied) {

                Intent i = new Intent(BadgeStatusActivity.this, SimpleScannerActivity.class);
                startActivityForResult(i, REQUEST_CODE);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.app_name) + getString(R.string.require_permission));
                builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        checkPermission();
                    }
                });

                builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

                builder.show();

            }
        }

    }

}
