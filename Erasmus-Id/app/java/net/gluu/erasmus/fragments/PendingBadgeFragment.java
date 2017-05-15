package net.gluu.erasmus.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gluu.erasmus.Application;
import net.gluu.erasmus.R;
import net.gluu.erasmus.adapters.BadgeRequestAdapter;
import net.gluu.erasmus.api.APIInterface;
import net.gluu.erasmus.api.APIService;
import net.gluu.erasmus.model.APIBadgeRequest;
import net.gluu.erasmus.model.Badge;
import net.gluu.erasmus.model.BadgeRequests;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PendingBadgeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PendingBadgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingBadgeFragment extends Fragment {
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

    public PendingBadgeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingBadgeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingBadgeFragment newInstance(String param1, String param2) {
        PendingBadgeFragment fragment = new PendingBadgeFragment();
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
        mProgress.setMessage("Loading badges..");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_badge, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvBadges = (RecyclerView) view.findViewById(R.id.rv_badges);
        mRvBadges.setLayoutManager(new LinearLayoutManager(getActivity()));
        getPendingBadgeRequests();
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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

    private void getPendingBadgeRequests() {
        showProgressBar();
        APIBadgeRequest badgeRequest = new APIBadgeRequest(Application.participant.getOpHost(), "Pending");
        Call<BadgeRequests> call = mObjAPI.getBadgeRequests(Application.AccessToken, badgeRequest);
        call.enqueue(new Callback<BadgeRequests>() {
            @Override
            public void onResponse(Call<BadgeRequests> call, Response<BadgeRequests> response) {
                hideProgressBar();
                if (response.errorBody() == null && response.body() != null) {
                    BadgeRequests objResponse = response.body();

                    if (objResponse != null) {
                        if (objResponse.getError()) {
                            Log.v("TAG", "Error in retrieving pending badge requests");
                            Application.showAutoDismissAlertDialog(getActivity(), objResponse.getErrorMsg());
                        } else {
                            Log.v("TAG", "pending badge requests retrieved:" + objResponse.getBadgeRequests().size());
                            mRvBadges.setAdapter(new BadgeRequestAdapter(getActivity(), objResponse.getBadgeRequests(), false));
                        }
                    }
                } else {
                    Log.v("TAG", "Error from server in retrieving pending badge requests:" + response.errorBody());
                    Log.v("TAG", "Error Code:" + response.code() + " Error message:" + response.message());
                }
            }

            @Override
            public void onFailure(Call<BadgeRequests> call, Throwable t) {
                Log.v("TAG", "Response retrieving pending badge requests failure" + t.getMessage());
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
