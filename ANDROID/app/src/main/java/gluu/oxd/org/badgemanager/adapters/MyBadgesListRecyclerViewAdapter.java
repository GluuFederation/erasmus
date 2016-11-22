package gluu.oxd.org.badgemanager.adapters;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import atownsend.swipeopenhelper.BaseSwipeOpenViewHolder;
import gluu.oxd.org.badgemanager.R;
import gluu.oxd.org.badgemanager.Statics.Values;
import gluu.oxd.org.badgemanager.interfraces.OnListFragmentInteractionListener;
import gluu.oxd.org.badgemanager.models.Badges;

import java.util.List;

public class MyBadgesListRecyclerViewAdapter extends RecyclerView.Adapter<MyBadgesListRecyclerViewAdapter.ViewHolder> {

    private final List<Badges> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final ButtonCallbacks callbacks;


    public interface ButtonCallbacks {
        void removePosition(int position);

        void editPosition(int position);
    }

    public MyBadgesListRecyclerViewAdapter(List<Badges> items, OnListFragmentInteractionListener listener, ButtonCallbacks callbacks) {
        mValues = items;
        mListener = listener;
        this.callbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_badgeslist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        Uri uri = Uri.parse(Values.BaseURL + holder.mItem.getPicture());
        holder.ivPicture.setImageURI(uri);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setBorder(Color.BLACK, 1.0f);
        roundingParams.setRoundAsCircle(true);
        holder.ivPicture.getHierarchy().setRoundingParams(roundingParams);
        holder.mTvDisplayName.setText(holder.mItem.getDisplayName());
        holder.mTvOrgName.setText(holder.mItem.getGluuAssociatedOrganizationDetail().getDisplayName());
        holder.mTvManagerEmail.setText(holder.mItem.getGluuAssociatedOrganizationDetail().getGluuManager());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void removeItem(int position) {

    }

    public class ViewHolder extends BaseSwipeOpenViewHolder {
        public final View mView;
        public Badges mItem;
        public SimpleDraweeView ivPicture;
        public FrameLayout cardView;
        public TextView deleteButton;
        public TextView editButton;
        public android.support.v7.widget.AppCompatTextView mTvDisplayName;
        public android.support.v7.widget.AppCompatTextView mTvManagerEmail;
        public android.support.v7.widget.AppCompatTextView mTvOrgName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivPicture = (SimpleDraweeView) view.findViewById(R.id.ivProfile);
            cardView = (FrameLayout) view.findViewById(R.id.cvContent);
            deleteButton = (TextView) view.findViewById(R.id.delete_button);
            editButton = (TextView) view.findViewById(R.id.edit_button);
            mTvDisplayName = (android.support.v7.widget.AppCompatTextView) view.findViewById(R.id.tvDisplayName);
            mTvManagerEmail = (android.support.v7.widget.AppCompatTextView) view.findViewById(R.id.tvManagerEmail);
            mTvOrgName = (android.support.v7.widget.AppCompatTextView) view.findViewById(R.id.tvOrgName);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.removePosition(getAdapterPosition());
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.editPosition(getAdapterPosition());
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }

        @NonNull
        @Override
        public View getSwipeView() {
            return cardView;
        }

        @Override
        public float getEndHiddenViewSize() {
            return editButton.getMeasuredWidth();
        }

        @Override
        public float getStartHiddenViewSize() {
            return deleteButton.getMeasuredWidth();
        }
    }
}
