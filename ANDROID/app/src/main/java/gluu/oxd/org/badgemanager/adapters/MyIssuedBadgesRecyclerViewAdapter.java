package gluu.oxd.org.badgemanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import gluu.oxd.org.badgemanager.R;
import gluu.oxd.org.badgemanager.fragments.badges.dummy.DummyContent.DummyItem;
import gluu.oxd.org.badgemanager.models.BadgeInstances;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class MyIssuedBadgesRecyclerViewAdapter extends SectionedRecyclerViewAdapter<MyIssuedBadgesRecyclerViewAdapter.ViewHolder> {

    private final HashMap<String, ArrayList<BadgeInstances>> mValues;
    ArrayList<String> keys = new ArrayList<>();

    public MyIssuedBadgesRecyclerViewAdapter(HashMap<String, ArrayList<BadgeInstances>> items) {
        mValues = items;
        setKeys();
    }

    private void setKeys() {
        for (String key : mValues.keySet()) {
            keys.add(key);

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == VIEW_TYPE_HEADER ? R.layout.stickyheader : R.layout.fragment_issuedbadges, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public int getSectionCount() {
        return mValues.size();
    }

    @Override
    public int getItemCount(int section) {
        return mValues.get(keys.get(section)).size();
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, int section) {

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int section, int relativePosition, int absolutePosition) {
        holder.mItem = mValues.get(keys.get(section)).get(relativePosition);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mSection;
        public BadgeInstances mItem;
        private TextView mDelete_button;
        private TextView mEdit_button;
        private FrameLayout mCvContent;
        private com.facebook.drawee.view.SimpleDraweeView mIvProfile;
        private android.support.v7.widget.AppCompatTextView mTvBadgeName;
        private android.support.v7.widget.AppCompatTextView mTvIssuerEmail;
        private android.support.v7.widget.AppCompatTextView mTvOwnerName;
        private android.support.v7.widget.AppCompatTextView mTvDescription;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSection = (TextView) view.findViewById(R.id.section);

            mDelete_button = (TextView) view.findViewById(R.id.delete_button);
            mEdit_button = (TextView) view.findViewById(R.id.edit_button);
            mCvContent = (FrameLayout) view.findViewById(R.id.cvContent);
            mIvProfile = (com.facebook.drawee.view.SimpleDraweeView) view.findViewById(R.id.ivProfile);
            mTvBadgeName = (android.support.v7.widget.AppCompatTextView) view.findViewById(R.id.tvBadgeName);
            mTvIssuerEmail = (android.support.v7.widget.AppCompatTextView) view.findViewById(R.id.tvIssuerEmail);
            mTvOwnerName = (android.support.v7.widget.AppCompatTextView) view.findViewById(R.id.tvOwnerName);
            mTvDescription = (android.support.v7.widget.AppCompatTextView) view.findViewById(R.id.tvDescription);

        }

    }


}
