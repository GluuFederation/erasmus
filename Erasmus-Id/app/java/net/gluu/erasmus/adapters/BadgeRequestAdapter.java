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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.gluu.erasmus.Application;
import net.gluu.erasmus.BadgeStatusActivity;
import net.gluu.erasmus.DisplayBadgeActivity;
import net.gluu.erasmus.R;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.APIBadgeRequest;
import net.gluu.erasmus.model.Badge;
import net.gluu.erasmus.model.BadgeRequest;
import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.Issuer;
import net.gluu.erasmus.model.PrivacyRequest;

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

public class BadgeRequestAdapter extends RecyclerView.Adapter<BadgeRequestAdapter.ViewHolder> {

    private final List<BadgeRequest> mBadgeRequests;
    private Context mContext;
    private boolean isFromRequest;
    private ProgressDialog mProgress;
    private APIInterface mObjAPI;

    public BadgeRequestAdapter(Context context, List<BadgeRequest> items, boolean isFromRequest) {
        mContext = context;
        mBadgeRequests = items;
        this.isFromRequest = isFromRequest;
        mObjAPI = APIService.createService(APIInterface.class);
        mProgress = new ProgressDialog(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_badge_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BadgeRequest badgeRequest = mBadgeRequests.get(position);
        holder.mBadgeName.setText(badgeRequest.getTemplateBadgeTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromRequest) {
                    new AlertDialog.Builder(mContext).setMessage("Do you want to Request " + badgeRequest.getTemplateBadgeTitle() + " Badge?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getBadgeDetails(badgeRequest);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });

        if (!isFromRequest) {
            holder.ivDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivDelete.setVisibility(View.GONE);
        }

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(mContext).setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteBadgeRequest(badgeRequest, position);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        holder.mTbPrivacy.setText("Make Private");

        holder.mTbPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled-make private
                    setBadgePrivacy(badgeRequest, "Private");
                    holder.mTbPrivacy.setTextOn("Make Public");
                } else {
                    // The toggle is disabled-make public
                    setBadgePrivacy(badgeRequest, "Public");
                    holder.mTbPrivacy.setTextOff("Make Private");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBadgeRequests.size();
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
            mTbPrivacy = (ToggleButton) view.findViewById(R.id.tb_privacy);
        }
    }

    private void showProgressBar(String msg) {
        if (mProgress == null)
            return;

        mProgress.setMessage(msg);
        mProgress.show();
    }

    private void hideProgressBar() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }

    private void deleteBadgeRequest(BadgeRequest badgeRequest, int position) {
        showProgressBar("Deleting..");
        Call<BadgeRequest> call = mObjAPI.deleteBadge(badgeRequest.getInum());
        call.enqueue(new Callback<BadgeRequest>() {
            @Override
            public void onResponse(Call<BadgeRequest> call, Response<BadgeRequest> response) {
                hideProgressBar();
                if (response.errorBody() == null && response.body() != null) {
                    BadgeRequest objResponse = response.body();

                    if (objResponse != null) {
                        if (objResponse.getError()) {
                            Log.v("TAG", "Error in deleting badge request.");
                            Application.showAutoDismissAlertDialog(mContext, objResponse.getErrorMsg());
                        } else {
                            mBadgeRequests.remove(position);
                            notifyItemRemoved(position);
                            Application.showAutoDismissAlertDialog(mContext, objResponse.getMessage());
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

    private void getBadgeDetails(BadgeRequest badgeRequest) {
        showProgressBar("Requesting badge..");
        Call<DisplayBadge> call = mObjAPI.getBadge(badgeRequest.getInum());
        call.enqueue(new Callback<DisplayBadge>() {
            @Override
            public void onResponse(Call<DisplayBadge> call, Response<DisplayBadge> response) {
                hideProgressBar();
                if (response.errorBody() == null && response.body() != null) {
                    DisplayBadge objResponse = response.body();

                    if (objResponse != null) {
                        Application.displayBadge = objResponse;
                        Intent i = new Intent(mContext, DisplayBadgeActivity.class);
                        mContext.startActivity(i);
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
            public void onFailure(Call<DisplayBadge> call, Throwable t) {
                Log.v("TAG", "Response retrieving badge templates failure" + t.getMessage());
                hideProgressBar();
            }
        });
    }

    private void setBadgePrivacy(BadgeRequest badgeRequest, String privacy) {
        showProgressBar("Changing privacy..");
        PrivacyRequest privacyRequest = new PrivacyRequest(badgeRequest.getInum(), privacy);
        Call<BadgeRequest> call = mObjAPI.setPrivacy(privacyRequest);
        call.enqueue(new Callback<BadgeRequest>() {
            @Override
            public void onResponse(Call<BadgeRequest> call, Response<BadgeRequest> response) {
                hideProgressBar();
                if (response.errorBody() == null && response.body() != null) {
                    BadgeRequest objResponse = response.body();

                    if (objResponse != null) {
                        if (objResponse.getError()) {
                            Application.showAutoDismissAlertDialog(mContext, objResponse.getErrorMsg());
                        } else {
                            Application.showAutoDismissAlertDialog(mContext, objResponse.getMessage());
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
}
