package com.iti.intake40.tripista.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.iti.intake40.tripista.trip.FloatingWidgetService;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;

public class ShowMap extends AppCompatActivity implements MapContract.ViewInterface {
    private static final int DRAW_OVER_OTHER_APP_PERMISSION = 123;
    private String id ;
    private MapContract.PresenterInterface presenter;
    private FireBaseCore core;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        core = FireBaseCore.getInstance();
        presenter = new MapPresenter(core,this);
        if (getIntent() != null) {
            id = getIntent().getExtras().getString("id");
            presenter.getTripById(id);

        }

    }

    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();


        // To prevent starting the service if the required permission is NOT granted.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            if(id!=null) {
                startService(new Intent(ShowMap.this, FloatingWidgetService.class).putExtra("id", id));
                finish();
            }
        } else {
           askForSystemOverlayPermission();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION && resultCode ==RESULT_OK) {
            if(id!=null) {
                startService(new Intent(ShowMap.this, FloatingWidgetService.class).putExtra("id", id));
                finish();
            }
        }
    }

    private void errorToast() {
        Toast.makeText(this, "Draw over other app permission not available. Can't start the application without the permission.", Toast.LENGTH_LONG).show();
    }


    @Override
    public void setTripData(Trip trip) {
        //"http://maps.google.com/maps?saddr="+start+"&daddr="+end)
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + trip.getEndPoint())).setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
// To prevent starting the service if the required permission is NOT granted.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            if(id!=null) {
                startService(new Intent(ShowMap.this, FloatingWidgetService.class).putExtra("id", id));
                finish();
            }
        } else {
            askForSystemOverlayPermission();
        }
    }
}
