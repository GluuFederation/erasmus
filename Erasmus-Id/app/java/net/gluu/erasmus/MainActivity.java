package net.gluu.erasmus;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import net.gluu.erasmus.adapters.CitiesAdapter;
import net.gluu.erasmus.model.City;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    CitiesAdapter adapter;
    ArrayList<City> cityList;
    private SearchView searchView;
    Spinner mSpState;
    RecyclerView mRvCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initSpinner();
        initRecyclerView();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mTvTitle = (TextView) toolbar.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.select_city);
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

    private void initSpinner() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.state)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpState = (Spinner) findViewById(R.id.sp_state);
        mSpState.setAdapter(spinnerArrayAdapter);
        mSpState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Application.State = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initRecyclerView() {
        mRvCities = (RecyclerView) findViewById(R.id.rv_cities);
        mRvCities.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new CitiesAdapter(MainActivity.this, getCityList());
        mRvCities.setAdapter(adapter);
    }

    public ArrayList<City> getCityList() {
        cityList = new ArrayList<>();
        String[] ArrCity = getResources().getStringArray(R.array.city);
        int[] ArrState = getResources().getIntArray(R.array.state_id);
        for (int i = 0; i < 11; i++) {

            City city = new City();
            city.setCityId(i);
            city.setCityName(ArrCity[i]);
            if (i <= 3) {
                city.setStateId(ArrState[0]);
            } else if (i >= 4 && i <= 6) {
                city.setStateId(ArrState[1]);
            } else if (i >= 7 && i < 9) {
                city.setStateId(ArrState[2]);
            } else {
                city.setStateId(ArrState[3]);
            }

            cityList.add(city);
        }
        return cityList;
    }

    private class SearchTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            MainActivity.this.adapter.filter(newText);
            return false;
        }
    }

    private class SearchCloseListener implements SearchView.OnCloseListener {
        @Override
        public boolean onClose() {
//            adapter.notifyDataSetChanged();
            return false;
        }
    }
}
