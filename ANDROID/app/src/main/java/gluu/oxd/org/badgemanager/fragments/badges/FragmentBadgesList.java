package gluu.oxd.org.badgemanager.fragments.badges;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;
import gluu.oxd.org.badgemanager.R;
import gluu.oxd.org.badgemanager.adapters.MyBadgesListRecyclerViewAdapter;
import gluu.oxd.org.badgemanager.interfraces.OnListFragmentInteractionListener;
import gluu.oxd.org.badgemanager.models.Badges;
import gluu.oxd.org.badgemanager.models.Organizations;
import gluu.oxd.org.badgemanager.network.ApiCallbacks;
import gluu.oxd.org.badgemanager.network.BadgesCommands;
import gluu.oxd.org.badgemanager.network.OrganizationCommands;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentBadgesList extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyBadgesListRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private View view;
    private int edit_position;
    private AlertDialog.Builder alertDialog;
    private Paint p = new Paint();
    private View viewAlert;
    private RequestQueue queue;
    private static final String TAG = "FragmentBadgesList";
    private ArrayList<Badges> badges = new ArrayList<>();
    private ArrayList<Organizations> organizationses = new ArrayList<>();
    private ArrayList<String> orgNames = new ArrayList<>();
    BadgesCommands badgesCommands = new BadgesCommands();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentBadgesList() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentBadgesList newInstance(int columnCount) {
        FragmentBadgesList fragment = new FragmentBadgesList();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_badgeslist_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

        }
        queue = Volley.newRequestQueue(getActivity());

        this.view = view;
        setNetworkdata();
        return view;
    }

    private void setNetworkdata() {

        badgesCommands.getBadges(new ApiCallbacks() {
            @Override
            public void success(JSONObject jsonObject) {
                Log.d(TAG, "success() called with: jsonObject = [" + jsonObject + "]");
                Gson gson = new Gson();
                try {
                    TypeToken<ArrayList<Badges>> token = new TypeToken<ArrayList<Badges>>() {
                    };

                    badges = gson.fromJson(jsonObject.getJSONArray("badges").toString(), token.getType());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setAdapter();
            }

            @Override
            public void error(VolleyError error) {

                Log.d(TAG, "error() called with: error = [" + error + "]");
            }
        });

        new OrganizationCommands().getOrganizations(new ApiCallbacks() {
            @Override
            public void success(JSONObject jsonObject) {
                Log.d(TAG, "success() called with: jsonObject = [" + jsonObject + "]");
                Gson gson = new Gson();
                try {
                    TypeToken<ArrayList<Organizations>> token = new TypeToken<ArrayList<Organizations>>() {
                    };

                    organizationses = gson.fromJson(jsonObject.getJSONArray("organizations").toString(), token.getType());
                    for (Organizations organizations : organizationses) {
                        orgNames.add(organizations.getDisplayName());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(VolleyError error) {
                Log.d(TAG, "error() called with: error = [" + error + "]");
            }
        });
    }

    private void setAdapter() {

        recyclerView.setAdapter(new MyBadgesListRecyclerViewAdapter(badges, mListener, new MyBadgesListRecyclerViewAdapter.ButtonCallbacks() {
            @Override
            public void removePosition(int position) {

            }

            @Override
            public void editPosition(int position) {
                initDialog(badges.get(position));
                alertDialog.show();
            }
        }));
        initSwipe();

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


    private void initSwipe() {
        SwipeOpenItemTouchHelper helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(
                SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));
        helper.attachToRecyclerView(recyclerView);
    }

    private void removeView() {

        if (viewAlert != null && viewAlert.getParent() != null) {
            ((ViewGroup) viewAlert.getParent()).removeView(viewAlert);
        }
    }

    private void initDialog(final Badges badges) {
        final Badges badgeChange = new Badges(badges);
        android.support.v7.widget.AppCompatSpinner mSpOrg;
        final android.support.design.widget.TextInputEditText mEtiDescription;
        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(badgeChange.getDisplayName());
        viewAlert = getActivity().getLayoutInflater().inflate(R.layout.dialog_layout, null);
        alertDialog.setView(viewAlert);

        mSpOrg = (android.support.v7.widget.AppCompatSpinner) viewAlert.findViewById(R.id.spOrg);
        mEtiDescription = (android.support.design.widget.TextInputEditText) viewAlert.findViewById(R.id.etiDescription);

        mSpOrg.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, orgNames));
        mEtiDescription.setText(badges.getDescription());

        mSpOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                badgeChange.setGluuAssociatedOrganization(organizationses.get(i).getDn());
                badgeChange.setGluuAssociatedOrganizationDetail(organizationses.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                badgeChange.setDescription(mEtiDescription.getText().toString());
                badgesCommands.upDate(badgeChange, new ApiCallbacks() {
                    @Override
                    public void success(JSONObject jsonObject) {
                        Log.d(TAG, "success() called with: jsonObject = [" + jsonObject + "]");
                        if (!jsonObject.isNull("success")) {
                            badges.setDescription(badgeChange.getDescription());
                            badges.setGluuAssociatedOrganization(badgeChange.getGluuAssociatedOrganization());
                            badges.setGluuAssociatedOrganizationDetail(badgeChange.getGluuAssociatedOrganizationDetail());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void error(VolleyError error) {
                        Log.d(TAG, "error() called with: error = [" + error + "]");
                    }
                });
                dialog.dismiss();
            }

        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

    }


}
