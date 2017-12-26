package net.gluu.erasmus.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import net.gluu.appauth.AuthState;
import net.gluu.erasmus.Application;
import net.gluu.erasmus.AuthStateManager;
import net.gluu.erasmus.Configuration;
import net.gluu.erasmus.LoginActivity;
import net.gluu.erasmus.R;
import net.gluu.erasmus.model.Participant;
import net.gluu.erasmus.utils.PrefrenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OPAdapter extends RecyclerView.Adapter<OPAdapter.ViewHolder> {

    private final List<Participant> mValues;
    Context mContext;
    private List<Participant> organizationList;

    public OPAdapter(Context context, List<Participant> items) {
        mContext = context;
        mValues = items;
        organizationList = new ArrayList<>();
        organizationList.addAll(mValues);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Participant op = mValues.get(position);
        holder.mOpName.setText(op.getName());

        holder.mView.setTag(R.string.app_name, position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout mLL = (LinearLayout) v;
                Integer mPos = (Integer) mLL.getTag(R.string.app_name);
                Application.participant = organizationList.get(mPos);
                new PrefrenceUtils().setGetOpDetails(new Gson().toJson(Application.participant));
                Configuration configuration = Configuration.getInstance(Application.mApplicationContext);
                AuthStateManager mAuthStateManager = AuthStateManager.getInstance(Application.mApplicationContext);
                mAuthStateManager.replace(new AuthState());
                configuration.acceptConfiguration();
                Intent i = new Intent(mContext, LoginActivity.class);
                i.putExtra("opName", op.getName());
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
        public final TextView mOpName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mOpName = (TextView) view.findViewById(R.id.tv_cityname);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mValues.clear();
        if (charText.length() == 0) {
            mValues.addAll(organizationList);
        } else {
            for (Participant op : organizationList) {
                if (op.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mValues.add(op);
                }
            }
        }
        notifyDataSetChanged();
    }
}
