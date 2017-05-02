package net.gluu.erasmus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.gluu.erasmus.adapters.BadgeRequestAdapter;
import net.gluu.erasmus.model.Badge;
import net.gluu.erasmus.model.BadgeRequest;

import java.util.ArrayList;

public class RequestBadgeActivity extends AppCompatActivity {

    private ArrayList<BadgeRequest> badgeList;
    private BadgeRequestAdapter adapter;
    RecyclerView mRvBadges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_badge);
        initToolbar();
        initRecyclerView();
   }

    private void initToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle= (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.request_badges);
    }

    private void initRecyclerView() {

        mRvBadges= (RecyclerView) findViewById(R.id.rv_badges);
        mRvBadges.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new BadgeRequestAdapter(RequestBadgeActivity.this, getBadgeList(),true);
        mRvBadges.setAdapter(adapter);
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
}
