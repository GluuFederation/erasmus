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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import net.gluu.erasmus.adapters.CitiesAdapter;
import net.gluu.erasmus.adapters.StateAdapter;
import net.gluu.erasmus.model.City;
import net.gluu.erasmus.model.State;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    CitiesAdapter citiesAdapter;
    ArrayList<City> cityList;
    private SearchView searchView;
    Spinner mSpState;
    RecyclerView mRvCities;
    ArrayList<State> arStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRvCities = (RecyclerView) findViewById(R.id.rv_cities);
        mRvCities.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        initToolbar();
        initSpinner();
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
        getStateCityList();

        StateAdapter adapter = new StateAdapter(MainActivity.this, arStates);
        mSpState = (Spinner) findViewById(R.id.sp_state);
        mSpState.setAdapter(adapter);
        mSpState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                try {
                    Application.State = parent.getItemAtPosition(pos).toString();
                    State obj = arStates.get(pos);
                    JSONArray jsonArrayCities = obj.getCities();
                    cityList = new ArrayList<>();
                    City city;
                    for (int i = 0; i < jsonArrayCities.length(); i++) {
                        city = new City();
                        city.setCityName(jsonArrayCities.getString(i));
                        cityList.add(city);
                    }
                    initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initRecyclerView() {
        citiesAdapter = new CitiesAdapter(MainActivity.this, cityList);
        mRvCities.setAdapter(citiesAdapter);
    }

    public void getStateCityList() {
        InputStream inputStream = getResources().openRawResource(R.raw.us_states_cities);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
            Iterator<?> keys = jObject.keys();
            arStates = new ArrayList<>();
            State obj;
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (jObject.get(key) instanceof JSONArray) {
                    obj = new State(key, jObject.getJSONArray(key));
                    arStates.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SearchTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            MainActivity.this.citiesAdapter.filter(newText);
            return false;
        }
    }

    private class SearchCloseListener implements SearchView.OnCloseListener {
        @Override
        public boolean onClose() {
            return false;
        }
    }
}
