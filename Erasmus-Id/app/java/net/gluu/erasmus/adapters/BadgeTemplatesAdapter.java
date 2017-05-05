package net.gluu.erasmus.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.gluu.erasmus.Application;
import net.gluu.erasmus.BadgeStatusActivity;
import net.gluu.erasmus.R;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.APIBadgeRequest;
import net.gluu.erasmus.model.Badge;
import net.gluu.erasmus.model.BadgeRequest;
import net.gluu.erasmus.model.Issuer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Meghna Joshi on 20/4/17.
 */

public class BadgeTemplatesAdapter extends RecyclerView.Adapter<BadgeTemplatesAdapter.ViewHolder> {

    private final List<Badge> mBadgeTemplates;
    private Context mContext;
    private APIInterface mObjAPI;
    private ProgressDialog mProgress;

    public BadgeTemplatesAdapter(Context context, List<Badge> items) {
        mContext = context;
        mBadgeTemplates = items;
        mObjAPI = APIService.createService(APIInterface.class);
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Processing request..");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_badge_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Badge badgeTemplate = mBadgeTemplates.get(position);
        holder.mTbPrivacy.setVisibility(View.GONE);
        holder.mBadgeName.setText(badgeTemplate.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext).setMessage("Do you want to Request " + badgeTemplate.getName() + " Badge?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createBadgeRequest(badgeTemplate);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        holder.ivDelete.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mBadgeTemplates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mBadgeName;
        public final ImageView ivDelete;
        public final ToggleButton mTbPrivacy;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBadgeName = (TextView) view.findViewById(R.id.tv_badge_name);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete_badge);
            mTbPrivacy= (ToggleButton) view.findViewById(R.id.tb_privacy);
        }
    }

    private void createBadgeRequest(Badge badgeTemplate) {
        String[] badgeId = badgeTemplate.getId().split("/");
        String templateBadgeId = "", participant = "";
        if (badgeId.length > 0) {
            templateBadgeId = badgeId[badgeId.length - 1];
        }
        if (templateBadgeId == null || templateBadgeId.length() == 0) {
            Log.v("TAG", "Can't make badge request. Invalid template badge id");
            return;
        }
        Issuer issuer = badgeTemplate.getIssuer();
        if (issuer == null) {
            Log.v("TAG", "Can't make badge request. Participant is null");
            return;
        }
        String[] arParticipantId = issuer.getId().split("/");
        if (arParticipantId.length > 0) {
            participant = arParticipantId[arParticipantId.length - 1];
        }
        if (participant == null || participant.length() == 0) {
            Log.v("TAG", "Can't make badge request. Invalid participant");
            return;
        }
        showProgressBar();
        APIBadgeRequest badgeRequest = new APIBadgeRequest(Application.participant.getOpHost(), participant, templateBadgeId, badgeTemplate.getName());
        Call<BadgeRequest> call = mObjAPI.makeBadgeRequest(Application.AccessToken, badgeRequest);
        call.enqueue(new Callback<BadgeRequest>() {
            @Override
            public void onResponse(Call<BadgeRequest> call, Response<BadgeRequest> response) {
                hideProgressBar();
                if (response.errorBody() == null && response.body() != null) {
                    BadgeRequest objResponse = response.body();

                    if (objResponse != null) {
                        if (objResponse.getError()) {
                            Log.v("TAG", "Error in making badge request.");
                        } else {
                            Application.showAutoDismissAlertDialog(mContext, "Badge requested successfully");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent i = new Intent(mContext, BadgeStatusActivity.class);
                                    mContext.startActivity(i);
                                    ((Activity) mContext).finish();
                                }
                            }, 2000);
                        }
                    }
                } else {
                    Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                    try {
                        String error = response.errorBody().string();
                        Log.v("TAG", "Error in creating badge request is:" + error);
                        JSONObject jsonObjectError = new JSONObject(error);
                        if (jsonObjectError.getBoolean("error")) {
                            Application.showAutoDismissAlertDialog(mContext, jsonObjectError.getString("errorMsg"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BadgeRequest> call, Throwable t) {
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
