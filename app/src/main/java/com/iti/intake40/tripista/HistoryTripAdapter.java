package com.iti.intake40.tripista;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.trip.ShowNotes;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HistoryTripAdapter extends RecyclerView.Adapter<HistoryTripAdapter.ViewHolder> {

    private static final String TAG = "historyAdapter";
    private final Context context;
    private Trip currentTrip;
    private FireBaseCore core = FireBaseCore.getInstance();
    private List<Trip> trips;
    private List<Trip> tripList = new ArrayList<>();


    public HistoryTripAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trip_history_single_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        this.currentTrip = this.trips.get(position);
        Log.d(TAG, "onBindViewHolder: " + currentTrip.toString());
        String tripStatus = null;
        String tripType = null;
        switch (currentTrip.getStatus()) {
            case DONE:
                tripStatus = "Done";
                break;
            case CANCELLED:
                tripStatus = "Cancelled";
                break;
            case IN_PROGRESS:
                tripStatus = "In Progress";
                break;
        }

        switch (currentTrip.getType()) {
            case ONE_WAY:
                tripType = "One Way";
                break;
            case ROUND_TRIP:
                tripType = "Round Trip";
                break;
        }
        holder.tripDate.setText(currentTrip.getDate());
        holder.tripTime.setText(currentTrip.getTime());
        holder.tripTitle.setText(currentTrip.getTitle());
        holder.tripStatus.setText(tripStatus);
        holder.startPoint.setText(currentTrip.getStartPoint());
        holder.endPoint.setText(currentTrip.getEndPoint());
        //holder.distance.setText(); // get distance here
        holder.type.setText(tripType);

        StringBuilder urlStringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?");
        //urlStringBuilder.append("center=Brooklyn+Bridge,New+York,NY"); //required if markers not present - defines the center of the map
        //urlStringBuilder.append("&zoom=13"); //(required if markers not present) defines the zoom level of the map
        urlStringBuilder.append("&size=600x300")
                .append("&maptype=roadmap")
                //markers
                .append("&markers=color:green")
                .append("%7C").append(currentTrip.getStartLat()).append(",").append(currentTrip.getStartLg())
                .append("&markers=color:red")
                .append("%7C").append(currentTrip.getEndLat()).append(",").append(currentTrip.getEndLg())
                //path
                .append("&path=color:blue")
                .append("%7C").append(currentTrip.getStartLat()).append(",").append(currentTrip.getStartLg())
                .append("%7C").append(currentTrip.getEndLat()).append(",").append(currentTrip.getEndLg())
                //API KEY
                .append("&key=AIzaSyA1J0I7OlNHN2BjD_tdKhRbgTNSDMDxWZw");
        Log.d(TAG, "onBindViewHolder: " + urlStringBuilder);
        URL url = null;
        try {
            url = new URL(urlStringBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.no_preview_available)
                .into(holder.mapPreview);
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;

            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    //collapse
                    changeVisibilityTo(holder, View.GONE);
                    isExpanded = false;

                } else {
                    //expand();
                    changeVisibilityTo(holder, View.VISIBLE);
                    isExpanded = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tripDate;
        private TextView tripTime;
        private TextView tripTitle;
        private TextView tripStatus;
        private TextView startPoint;
        private TextView endPoint;
        private TextView distance;
        private TextView type;
        private Button delete;
        private Button showNotes;
        private ImageView mapPreview;
        private TextView kmText;
        private ImageView arrowImage;
        private ConstraintLayout rootLayout;

        ViewHolder(final View itemView) {
            super(itemView);

            tripDate = itemView.findViewById(R.id.history_date_text);
            tripTime = itemView.findViewById(R.id.history_time_text);
            tripTitle = itemView.findViewById(R.id.history_title_text);
            tripStatus = itemView.findViewById(R.id.history_status_text);
            startPoint = itemView.findViewById(R.id.history_from_text);
            endPoint = itemView.findViewById(R.id.history_to_text);
            distance = itemView.findViewById(R.id.history_distance_text);
            type = itemView.findViewById(R.id.history_type_text);
            delete = itemView.findViewById(R.id.history_delete);
            showNotes = itemView.findViewById(R.id.history_show_notes);
            mapPreview = itemView.findViewById(R.id.map_preview);
            kmText = itemView.findViewById(R.id.km_text_view);
            arrowImage = itemView.findViewById(R.id.arrow_imageView);
            rootLayout = itemView.findViewById(R.id.trip_row);

            showNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tripId = trips.get(getAdapterPosition()).getTripId();
                    showTripNotes(tripId);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteTrip(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void deleteTrip(final int tripPos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Delete Trip")
                .setMessage("Are you sure you want to delete this trip?")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tripId = trips.get(tripPos).getTripId();
                        core.deleteTrip(tripId, context);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showTripNotes(String tripId) {
        Intent notesIntent = new Intent(context, ShowNotes.class);
        notesIntent.putExtra("id", tripId);
        context.startActivity(notesIntent);
    }

    private void changeVisibilityTo(ViewHolder holder, int visability) {
        holder.showNotes.setVisibility(visability);
        holder.delete.setVisibility(visability);
        holder.startPoint.setVisibility(visability);
        holder.endPoint.setVisibility(visability);
        holder.type.setVisibility(visability);
        holder.distance.setVisibility(visability);
        holder.kmText.setVisibility(visability);
        if (visability == View.VISIBLE) {
            holder.arrowImage.setImageResource(R.drawable.ic_up_arrow);
        } else {
            holder.arrowImage.setImageResource(R.drawable.ic_down_arrow);
        }
    }
}


