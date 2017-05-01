package net.gluu.erasmus;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.gluu.erasmus.adapters.OPAdapter;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.Participant;
import net.gluu.erasmus.model.ParticipantsResponse;
import net.gluu.erasmus.model.Op;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectOpActivity extends AppCompatActivity {

    private SearchView searchView;
    OPAdapter adapter;
    RecyclerView mRvOrganization;
    APIInterface mObjAPI;
    List<Participant> lstParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_op);

        mObjAPI = APIService.createService(APIInterface.class);

        initToolbar();
        mRvOrganization = (RecyclerView) findViewById(R.id.rvOrganization);
        mRvOrganization.setLayoutManager(new LinearLayoutManager(this));
        getOp();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.select_organization);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        ((ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_go_btn)).setColorFilter(Color.WHITE);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);
        ((ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn)).setColorFilter(Color.WHITE);
        ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchTextListener());
        searchView.setOnCloseListener(new SearchCloseListener());
        return super.onCreateOptionsMenu(menu);
    }

    private class SearchTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            adapter.filter(newText);
            return false;
        }
    }

    private class SearchCloseListener implements SearchView.OnCloseListener {
        @Override
        public boolean onClose() {
            return false;
        }
    }

    public ArrayList<Op> getOPList() {
        ArrayList<Op> opList = new ArrayList<>();
        String[] ArrOp = getResources().getStringArray(R.array.city);
        for (int i = 0; i < 11; i++) {
            Op op = new Op();
            op.setCityId(i);
            op.setOpName(ArrOp[i]);
            op.setOpId(i);
            opList.add(op);
        }
        return opList;
    }

    private void getOp() {
        Call<ParticipantsResponse> call = mObjAPI.getParticipants("all", "all");
        call.enqueue(new Callback<ParticipantsResponse>() {
            @Override
            public void onResponse(Call<ParticipantsResponse> call, Response<ParticipantsResponse> response) {

                if (response.errorBody() == null && response.body() != null) {
                    ParticipantsResponse objResponse = response.body();

                    if (objResponse != null) {
                        if (objResponse.getError()) {
                            Log.v("TAG", "Error in retrieving participants");
                        } else {
                            Log.v("TAG", "Participants retrieved:" + objResponse.getParticipants().size());
                            adapter = new OPAdapter(SelectOpActivity.this, objResponse.getParticipants());
                            mRvOrganization.setAdapter(adapter);
                        }
                    }
                } else {
                    Log.v("TAG", "Error from server in user card add:" + response.errorBody());
                    Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                }
            }

            @Override
            public void onFailure(Call<ParticipantsResponse> call, Throwable t) {
                Log.v("TAG", "Response user card add failure" + t.getMessage());
            }
        });
    }
}
