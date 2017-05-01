package net.gluu.erasmus.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gluu.erasmus.Application;
import net.gluu.erasmus.R;
import net.gluu.erasmus.SelectOpActivity;
import net.gluu.erasmus.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.ViewHolder> {

    private final List<City> mValues;
    Context mContext;
    private List<City> cityList;

    public CitiesAdapter(Context context, List<City> items) {
        mContext = context;
        mValues = items;
        cityList = new ArrayList<>();
        cityList.addAll(mValues);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final City city = mValues.get(position);
        holder.mCityName.setText(city.getCityName());

        holder.mCityName.setTag(mContext.getString(R.string.app_name));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.City = holder.mCityName.getText().toString();
                Intent i = new Intent(mContext, SelectOpActivity.class);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCityName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCityName = (TextView) view.findViewById(R.id.tv_cityname);
        }

    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mValues.clear();
        if (charText.length() == 0) {
            mValues.addAll(cityList);
        } else {
            for (City city : cityList) {
                if (city.getCityName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mValues.add(city);
                }
            }
        }
        notifyDataSetChanged();
    }
}
