package net.gluu.erasmus.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import net.gluu.erasmus.DisplayBadgeActivity;
import net.gluu.erasmus.R;
import net.gluu.erasmus.RequestBadgeActivity;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.api.AccessToken;
import net.gluu.erasmus.model.APIBadgeDetail;
import net.gluu.erasmus.model.BadgeRequest;
import net.gluu.erasmus.model.DisplayBadge;
import net.gluu.erasmus.model.PrivacyRequest;

import org.androidannotations.annotations.App;
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
            holder.mTbPrivacy.setVisibility(View.GONE);
        } else {
            holder.mTbPrivacy.setVisibility(View.VISIBLE);
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

        Log.v("TAG", "badge privacy: " + badgeRequest.getPrivacy());
        if (badgeRequest.getPrivacy().equalsIgnoreCase("Public")) {
            holder.mTbPrivacy.setText("Make Private");
        } else {
            holder.mTbPrivacy.setText("Make Public");
        }

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
        if (Application.checkInternetConnection(mContext)) {
            showProgressBar("Deleting..");

            Application.getAccessToken(new AccessToken() {
                @Override
                public void onAccessTokenSuccess(String accessToken) {
                    Log.v("TAG", "Access token in deleteBadgeRequest(): " + accessToken);
                    APIBadgeDetail badgeDetail = new APIBadgeDetail();
                    badgeDetail.setBadgeRequestInum(badgeRequest.getInum());
                    badgeDetail.setOpHost(Application.participant.getOpHost());
                    Call<BadgeRequest> call = mObjAPI.deleteBadge(accessToken, badgeDetail);
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
                                    Log.v("TAG", "Error in deleting badge request is:" + error);
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
                            Log.v("TAG", "Response deleting badge request failure" + t.getMessage());
                            hideProgressBar();
                        }
                    });
                }

                @Override
                public void onAccessTokenFailure() {
                    Log.v("TAG", "Failed to get access token in deleteBadgeRequest()");
                    hideProgressBar();
                    Application.showAutoDismissAlertDialog(mContext, mContext.getString(R.string.unable_to_process));
                }
            });
        } else {
            Application.showAutoDismissAlertDialog(mContext, mContext.getString(R.string.no_internet));
        }
    }

    private void getBadgeDetails(BadgeRequest badgeRequest) {
        if (Application.checkInternetConnection(mContext)) {
            showProgressBar("Requesting badge..");

            Application.getAccessToken(new AccessToken() {
                @Override
                public void onAccessTokenSuccess(String accessToken) {
                    Log.v("TAG", "Access token in getBadgeDetails(): " + accessToken);
                    APIBadgeDetail badgeDetail = new APIBadgeDetail();
                    badgeDetail.setBadgeRequestInum(badgeRequest.getInum());
                    badgeDetail.setOpHost(Application.participant.getOpHost());
                    Call<DisplayBadge> call = mObjAPI.getBadge(accessToken, badgeDetail);
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
                            Log.v("TAG", "Response creating badge request failure" + t.getMessage());
                            hideProgressBar();
                        }
                    });
                }

                @Override
                public void onAccessTokenFailure() {
                    Log.v("TAG", "Failed to get access token in getBadgeDetails()");
                    hideProgressBar();
                    Application.showAutoDismissAlertDialog(mContext, mContext.getString(R.string.unable_to_process));
                }
            });
        } else {
            Application.showAutoDismissAlertDialog(mContext, mContext.getString(R.string.no_internet));
        }
    }

    private void setBadgePrivacy(BadgeRequest badgeRequest, String privacy) {
        if (Application.checkInternetConnection(mContext)) {
            showProgressBar("Changing privacy..");

            Application.getAccessToken(new AccessToken() {
                @Override
                public void onAccessTokenSuccess(String accessToken) {
                    Log.v("TAG", "Access token in setBadgePrivacy(): " + accessToken);
                    PrivacyRequest privacyRequest = new PrivacyRequest(badgeRequest.getInum(), Application.participant.getOpHost(), privacy);
                    Call<BadgeRequest> call = mObjAPI.setPrivacy(accessToken, privacyRequest);
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

                @Override
                public void onAccessTokenFailure() {
                    Log.v("TAG", "Failed to get access token in setBadgePrivacy()");
                    hideProgressBar();
                    Application.showAutoDismissAlertDialog(mContext, mContext.getString(R.string.unable_to_process));
                }
            });
        } else {
            Application.showAutoDismissAlertDialog(mContext, mContext.getString(R.string.no_internet));
        }
    }
}
