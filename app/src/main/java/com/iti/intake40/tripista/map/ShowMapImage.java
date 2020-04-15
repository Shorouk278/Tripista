package com.iti.intake40.tripista.map;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.iti.intake40.tripista.OnTripsLoaded;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ShowMapImage extends Fragment implements MapDelegate{
    ImageView mapImage;
    public ShowMapImage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_map_image, container, false);
         mapImage  = view.findViewById(R.id.map_image);

        return view;
    }



    @Override
    public void setMapUri(Uri uri) {
        Glide.with(getActivity()).load(uri).into(mapImage);

    }
}
