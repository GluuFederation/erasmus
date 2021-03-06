package net.gluu.erasmus.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gluu.erasmus.Application;
import net.gluu.erasmus.R;
import net.gluu.erasmus.adapters.ApprovedBadgeRequestAdapter;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.api.AccessToken;
import net.gluu.erasmus.model.APIBadgeRequest;
import net.gluu.erasmus.model.BadgeRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ApproveBadgeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ApproveBadgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApproveBadgeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    RecyclerView mRvBadges;
    APIInterface mObjAPI;
    ProgressDialog mProgress;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public ApproveBadgeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApproveBadgeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApproveBadgeFragment newInstance(String param1, String param2) {
        ApproveBadgeFragment fragment = new ApproveBadgeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObjAPI = APIService.createService(APIInterface.class);
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Loading approved badges..");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approve_badge, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvBadges = (RecyclerView) view.findViewById(R.id.rv_badges);
        mRvBadges.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setNestedScrollingEnabled(true);
        mSwipeRefreshLayout.setEnabled(true);

        showApprovedBadgeRequests();

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.v("TAG", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        getApprovedBadgeRequests();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showApprovedBadgeRequests() {
        mRvBadges.setAdapter(new ApprovedBadgeRequestAdapter(getActivity(), Application.approvedBadgeRequests));
    }

    private void getApprovedBadgeRequests() {
        if (Application.checkInternetConnection(getActivity())) {

            showProgressBar();

            Application.getAccessToken(new AccessToken() {
                @Override
                public void onAccessTokenSuccess(String accessToken) {
                    Log.v("TAG", "Access token in getApprovedBadgeRequests(): " + accessToken);
                    APIBadgeRequest badgeRequest = new APIBadgeRequest(Application.participant.getOpHost(), "Approved");
                    Call<BadgeRequest> call = mObjAPI.getBadgeRequests(accessToken, badgeRequest);
                    call.enqueue(new Callback<BadgeRequest>() {
                        @Override
                        public void onResponse(Call<BadgeRequest> call, Response<BadgeRequest> response) {
                            hideProgressBar();
                            if (response.errorBody() == null && response.body() != null) {
                                BadgeRequest objResponse = response.body();

                                if (objResponse != null) {
                                    if (objResponse.getError()) {
                                        Log.v("TAG", "Error in retrieving approved badge requests");
                                        Application.showAutoDismissAlertDialog(getActivity(), objResponse.getErrorMsg());
                                        mRvBadges.setAdapter(new ApprovedBadgeRequestAdapter(getActivity(), new ArrayList<>()));

                                    } else {
                                        Log.v("TAG", "approved badge requests retrieved:" + objResponse.getBadgeRequests().getApprovedBadgeRequests().size());
                                        Application.approvedBadgeRequests = objResponse.getBadgeRequests().getApprovedBadgeRequests();
                                        showApprovedBadgeRequests();
                                    }
                                }
                            } else {
                                Log.v("TAG", "Error from server in retrieving approved badge requests:" + response.errorBody());
                                Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                                mRvBadges.setAdapter(new ApprovedBadgeRequestAdapter(getActivity(), new ArrayList<>()));

                            }
                        }

                        @Override
                        public void onFailure(Call<BadgeRequest> call, Throwable t) {
                            Log.v("TAG", "Response retrieving approved badge requests failure" + t.getMessage());
                            mRvBadges.setAdapter(new ApprovedBadgeRequestAdapter(getActivity(), new ArrayList<>()));

                            hideProgressBar();
                        }
                    });
                }

                @Override
                public void onAccessTokenFailure() {
                    Log.v("TAG", "Failed to get access token in getApprovedBadgeRequests()");
                    hideProgressBar();
                    Application.showAutoDismissAlertDialog(getActivity(), getString(R.string.unable_to_process));
                }
            });
        } else {
            Application.showAutoDismissAlertDialog(getActivity(), getString(R.string.no_internet));
        }
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
