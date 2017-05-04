package net.gluu.erasmus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.gluu.erasmus.adapters.BadgeRequestAdapter;
import net.gluu.erasmus.adapters.BadgeTemplatesAdapter;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.Badge;
import net.gluu.erasmus.model.BadgeRequest;
import net.gluu.erasmus.model.BadgeRequests;
import net.gluu.erasmus.model.BadgeTemplates;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestBadgeActivity extends AppCompatActivity {

    private ArrayList<BadgeRequest> badgeList;
    private BadgeTemplatesAdapter adapter;
    RecyclerView mRvBadges;
    APIInterface mObjAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_badge);
        mObjAPI = APIService.createService(APIInterface.class);
        initToolbar();
        initRecyclerView();
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

    public ArrayList<BadgeRequest> getBadgeList() {
        badgeList = new ArrayList<>();
        String[] ArrCity = getResources().getStringArray(R.array.badge);
        for (int i = 0; i < 15; i++) {

            BadgeRequest badge = new BadgeRequest();
            badge.setTemplateBadgeTitle(ArrCity[i]);
            badgeList.add(badge);
        }
        return badgeList;
    }

    private void getBadgeTemplates() {
        Application.showProgressBar();
        Log.v("TAG","Access token in getBadgeTemplates(): "+Application.AccessToken);
        Call<BadgeTemplates> call = mObjAPI.getBadgeTemplates(Application.AccessToken, Application.participant.getOpHost(), "all");
        call.enqueue(new Callback<BadgeTemplates>() {
            @Override
            public void onResponse(Call<BadgeTemplates> call, Response<BadgeTemplates> response) {
                Application.hideProgressBar();
                if (response.errorBody() == null && response.body() != null) {
                    BadgeTemplates objResponse = response.body();

                    if (objResponse != null) {
                        if (objResponse.getError()) {
                            Log.v("TAG", "Error in retrieving badge templates");
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
                Application.hideProgressBar();
            }
        });
    }
}
