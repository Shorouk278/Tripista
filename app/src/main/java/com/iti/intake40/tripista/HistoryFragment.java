package com.iti.intake40.tripista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    private RecyclerView historyRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Trip> tripList = new ArrayList<>();
    private FireBaseCore core = FireBaseCore.getInstance();
    private LinearLayout notFound;

    public HistoryFragment() {
        // Required empty public constructorzz
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        notFound = rootView.findViewById(R.id.not_found);
        historyRecyclerView = rootView.findViewById(R.id.history_rc);
        historyRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        historyRecyclerView.setLayoutManager(layoutManager);
        adapter = new HistoryTripAdapter(getContext(), tripList);
        if (tripList.size() == 0)
            notFound.setVisibility(View.VISIBLE);
        else {
            notFound.setVisibility(View.GONE);
        }
        historyRecyclerView.setAdapter(adapter);

        core.getHistoryTripsForCurrentUser(new OnTripsLoaded() {
            @Override
            public void onTripsLoaded(List<Trip> trips) {
                tripList.clear();
                tripList.addAll(trips);
                if (tripList.size() == 0)
                    notFound.setVisibility(View.VISIBLE);
                else {
                    notFound.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }


}
