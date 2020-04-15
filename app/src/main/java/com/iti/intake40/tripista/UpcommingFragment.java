package com.iti.intake40.tripista;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.utils.AlarmUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcommingFragment extends Fragment {

    private static final String TAG = "upcomming";
    private RecyclerView upcommingRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Trip> tripList = new ArrayList<>();
    private FireBaseCore core = FireBaseCore.getInstance();
    private LinearLayout notFound;

    public UpcommingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_upcomming, container, false);

        getActivity().setTitle(R.string.upcomming_trips);
        notFound = rootView.findViewById(R.id.not_found);
        upcommingRecyclerView = rootView.findViewById(R.id.upcommming_rc);
        upcommingRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        upcommingRecyclerView.setLayoutManager(layoutManager);
        adapter = new UpcommingTripAdapter(getContext(), tripList);
        upcommingRecyclerView.setAdapter(adapter);
        if (tripList.size() == 0)
            notFound.setVisibility(View.VISIBLE);
        else {
            notFound.setVisibility(View.GONE);

        }
        core.getTripsForCurrentUser(new OnTripsLoaded() {
            @Override
            public void onTripsLoaded(List<Trip> trips) {
                tripList.clear();
                tripList.addAll(trips);
                adapter.notifyDataSetChanged();
                if (tripList.size() == 0)
                    notFound.setVisibility(View.VISIBLE);
                else {
                    notFound.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                Log.d(TAG, "onTripsLoaded: " + trips.toString());
            }
        });

        return rootView;
    }

}
