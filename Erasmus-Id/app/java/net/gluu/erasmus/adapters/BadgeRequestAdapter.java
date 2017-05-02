package net.gluu.erasmus.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gluu.erasmus.DisplayBadgeActivity;
import net.gluu.erasmus.R;
import net.gluu.erasmus.model.Badge;
import net.gluu.erasmus.model.BadgeRequest;

import java.util.List;

/**
 * Created by Meghna Joshi on 20/4/17.
 */

public class BadgeRequestAdapter extends RecyclerView.Adapter<BadgeRequestAdapter.ViewHolder> {

    private final List<BadgeRequest> mBadgeRequests;
    Context mContext;
    boolean isFromRequest;

    public BadgeRequestAdapter(Context context, List<BadgeRequest> items, boolean isFromRequest) {
        mContext = context;
        mBadgeRequests = items;
        this.isFromRequest = isFromRequest;
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
                            Intent i = new Intent(mContext, DisplayBadgeActivity.class);
                            i.putExtra("badgeName",badgeRequest.getTemplateBadgeTitle());
                            mContext.startActivity(i);
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

                mBadgeRequests.remove(position);
                notifyItemRemoved(position);
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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBadgeName = (TextView) view.findViewById(R.id.tv_badge_name);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete_badge);
        }

    }
}