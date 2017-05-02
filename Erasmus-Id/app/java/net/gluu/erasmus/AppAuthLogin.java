package net.gluu.erasmus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.gluu.appauth.AuthorizationService;
import net.gluu.appauth.browser.AnyBrowserMatcher;
import net.gluu.appauth.browser.BrowserMatcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


public class AppAuthLogin extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String EXTRA_FAILED = "failed";

    private AuthorizationService mAuthService;
    private AuthStateManager mAuthStateManager;
    private Configuration mConfiguration;

    private final AtomicReference<String> mClientId = new AtomicReference<>();
    private ExecutorService mExecutor;

    @NonNull
    private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExecutor = Executors.newSingleThreadExecutor();
        mAuthStateManager = AuthStateManager.getInstance(this);
        mConfiguration = Configuration.getInstance(this);

        if (mAuthStateManager.getCurrent().isAuthorized()
            && !mConfiguration.hasConfigurationChanged()) {
            startActivity(new Intent(this, TokenActivity.class));
            finish();
            return;
        }

        initToolbar();
        setContentView(R.layout.activity_login_appauth);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_change_organization).setOnClickListener(this);

        if (getIntent().getStringExtra("opName") != null && !getIntent().getStringExtra("opName").isEmpty())
            ((TextView)findViewById(R.id.tv_organization)).setText(getIntent().getStringExtra("opName"));
    }

    private void initToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle= (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.login);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:
                Intent i = new Intent(getApplicationContext(), BadgeStatusActivity.class);
                startActivity(i);
                finish();
                break;

            case R.id.tv_change_organization:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mExecutor.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

            mAuthService.dispose();
            if (mAuthService != null) {
        }
    }

    @MainThread
    private void displayLoading(String loadingMessage) {
        findViewById(R.id.loading_container).setVisibility(View.VISIBLE);
        findViewById(R.id.auth_container).setVisibility(View.GONE);
        findViewById(R.id.error_container).setVisibility(View.GONE);

        ((TextView)findViewById(R.id.loading_description)).setText(loadingMessage);
    }
}
