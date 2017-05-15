package net.gluu.erasmus;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.gluu.erasmus.adapters.BadgeTemplatesAdapter;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.BadgeTemplates;
import net.gluu.erasmus.model.TemplateBadgeRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestBadgeActivity extends AppCompatActivity {

    private BadgeTemplatesAdapter adapter;
    RecyclerView mRvBadges;
    APIInterface mObjAPI;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_badge);
        mObjAPI = APIService.createService(APIInterface.class);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Requesting templates..");
        initToolbar();
        initRecyclerView();
        getBadgeTemplates();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.request_badges);
    }

    private void initRecyclerView() {
        mRvBadges = (RecyclerView) findViewById(R.id.rv_badges);
        mRvBadges.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getBadgeTemplates();
    }

    private void getBadgeTemplates() {
        showProgressBar();
        Log.v("TAG", "Access token in getBadgeTemplates(): " + Application.AccessToken);
        TemplateBadgeRequest templateBadgeRequest = new TemplateBadgeRequest(Application.participant.getOpHost(), "all");
        Call<BadgeTemplates> call = mObjAPI.getBadgeTemplates(Application.AccessToken, templateBadgeRequest);
        call.enqueue(new Callback<BadgeTemplates>() {
            @Override
            public void onResponse(Call<BadgeTemplates> call, Response<BadgeTemplates> response) {
                hideProgressBar();
                if (response.errorBody() == null && response.body() != null) {
                    BadgeTemplates objResponse = response.body();

                    if (objResponse != null) {
                        if (objResponse.getError()) {
                            Log.v("TAG", "Error in retrieving badge templates");
                            Application.showAutoDismissAlertDialog(RequestBadgeActivity.this, objResponse.getErrorMsg());
                        } else {
                            Log.v("TAG", "badge requests retrieved:" + objResponse.getBadges().size());
                            adapter = new BadgeTemplatesAdapter(RequestBadgeActivity.this, objResponse.getBadges());
                            mRvBadges.setAdapter(adapter);
                        }
                    }
                } else {
                    Log.v("TAG", "Error from server in retrieving badge templates:" + response.errorBody());
                    Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                }
            }

            @Override
            public void onFailure(Call<BadgeTemplates> call, Throwable t) {
                Log.v("TAG", "Response retrieving badge templates failure" + t.getMessage());
                hideProgressBar();
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
}
