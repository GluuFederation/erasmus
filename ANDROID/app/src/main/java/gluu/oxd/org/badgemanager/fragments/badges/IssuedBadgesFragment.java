package gluu.oxd.org.badgemanager.fragments.badges;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import gluu.oxd.org.badgemanager.R;
import gluu.oxd.org.badgemanager.adapters.MyIssuedBadgesRecyclerViewAdapter;
import gluu.oxd.org.badgemanager.interfraces.OnListFragmentInteractionListener;
import gluu.oxd.org.badgemanager.models.BadgeInstances;
import gluu.oxd.org.badgemanager.network.ApiCallbacks;
import gluu.oxd.org.badgemanager.network.IssuedBadgesCommands;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class IssuedBadgesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    IssuedBadgesCommands issuedBadgesCommands;
    private HashMap<String, ArrayList<BadgeInstances>> badges;
    private static final String TAG = "IssuedBadgesFragment";
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IssuedBadgesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static IssuedBadgesFragment newInstance(int columnCount) {
        IssuedBadgesFragment fragment = new IssuedBadgesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        issuedBadgesCommands = new IssuedBadgesCommands();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issuedbadges_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        setNotworkData();
        return view;
    }

    private void setNotworkData() {
        issuedBadgesCommands.getIssuedRequestes(new ApiCallbacks() {
            @Override
            public void success(JSONObject jsonObject) {
                Gson gson = new Gson();
                Log.d(TAG, "success() called with: jsonObject = [" + jsonObject + "]");

                try {
                    TypeToken<HashMap<String, ArrayList<BadgeInstances>>> token = new TypeToken<HashMap<String, ArrayList<BadgeInstances>>>() {
                    };

                    badges = gson.fromJson(jsonObject.getJSONObject("badges").toString(), token.getType());
                    setAdpaters();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(VolleyError error) {

            }
        });
    }

    private void setAdpaters() {
        recyclerView.setAdapter(new MyIssuedBadgesRecyclerViewAdapter(badges));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}
