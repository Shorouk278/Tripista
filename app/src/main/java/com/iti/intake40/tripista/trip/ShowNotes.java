package com.iti.intake40.tripista.trip;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.map.MapContract;
import com.iti.intake40.tripista.map.MapPresenter;

public class ShowNotes extends AppCompatActivity implements MapContract.ViewInterface {
    private RecyclerView noteList;
    private MapContract.PresenterInterface mapPresenter;
    private FireBaseCore core;
    private View noteFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("show Notes");
        setContentView(R.layout.activity_show_notes);
        noteList = findViewById(R.id.note_list);
        noteFound = findViewById(R.id.not_found);
        String id = getIntent().getExtras().getString("id");
        core = FireBaseCore.getInstance();
        mapPresenter = new MapPresenter(core, this);
        mapPresenter.getTripById(id);
    }

    @Override
    public void setTripData(Trip trip) {
        if (trip.getNotes() == null) {
            noteFound.setVisibility(View.VISIBLE);
        } else {
            noteFound.setVisibility(View.GONE);
            NotesAdapter notesAdapter = new NotesAdapter(trip.getNotes(), this, trip.getTripId());
            noteList.setAdapter(notesAdapter);
            noteList.setHasFixedSize(true);
            noteList.setLayoutManager(new LinearLayoutManager(this));
        }
    }


}
