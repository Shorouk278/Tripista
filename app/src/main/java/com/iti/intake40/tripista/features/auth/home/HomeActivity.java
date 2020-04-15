package com.iti.intake40.tripista.features.auth.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iti.intake40.tripista.AlarmReceiver;
import com.iti.intake40.tripista.HistoryFragment;
import com.iti.intake40.tripista.OnTripsLoaded;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.UpcommingFragment;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.core.model.UserModel;
import com.iti.intake40.tripista.features.auth.signin.SigninActivity;
import com.iti.intake40.tripista.map.MapDelegate;
import com.iti.intake40.tripista.map.ShowMapImage;
import com.iti.intake40.tripista.trip.AddTripActivity;
import com.iti.intake40.tripista.utils.AlarmTest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.iti.intake40.tripista.UpcommingTripAdapter.cancelOneWayTripId;
import static com.iti.intake40.tripista.UpcommingTripAdapter.cancelRoundWayTripId;
import static com.iti.intake40.tripista.features.auth.signin.PhoneVerficiation.PREF_NAME;
import static com.iti.intake40.tripista.features.auth.signin.SigninActivity.PHONE_ARG;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeContract.ViewInterface {

    private static final String TAG = "Home";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private View header;
    private DrawerLayout drawerLayout;
    private ImageView profilePictureView;
    private TextView userNameTextView;
    private TextView emailTextView;
    private URL img_value = null;
    private static final String GOOGLE_MAP_API = "https://maps.google.com/maps/api/staticmap?size=600x600";
    private static final String API_KEY = "AIzaSyDE9fxP7sernImHPGVNjI6JimiKG5GgpB0";
    private FireBaseCore core;
    private List<Trip> tripList = new ArrayList<>();
    private HomeContract.PresenterInterface homePresenter;
    private FloatingActionButton goToAddTrip;
    private MapDelegate mapDelegate;
    AlarmTest alarmTest = new AlarmTest();
     Intent setAlarmIntent;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setViews();

//               setAlarmIntent = getIntent();
//             if (setAlarmIntent.getStringExtra("firstLogin") != null) {
//                 alarmTest.setAlarms(getBaseContext());
//        }
//        if (getSharedPreferences(PREF_NAME, 0) == null) {
//            alarmTest.setAlarms(getBaseContext());
//
//
//        }


        toolbar.setTitle(R.string.upcomming_trips);





        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //handle toggle button click
        drawerLayout.addDrawerListener(toggle);
        //add animation to toggle button
        toggle.syncState();

        if (savedInstanceState == null) {
            //open the first fragment imdedaitely
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new UpcommingFragment())
                    .commit();

            //select the first item
            navigationView.setCheckedItem(R.id.nav_upcomming);

        }

        getUserInfo();

        goToAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddTripActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_upcomming:
                mapDelegate = null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UpcommingFragment()).commit();
                toolbar.setTitle(R.string.upcomming_trips);
                break;
            case R.id.nav_history:
                toolbar.setTitle(R.string.history);
                mapDelegate = null;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HistoryFragment()).commit();
                toolbar.setTitle(R.string.trip_history);
                break;
            case R.id.nav_map_history:
//                toolbar.setTitle(R.string.map_history);
                toolbar.setTitle("map history");

                mapDelegate = new ShowMapImage();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, (Fragment) mapDelegate)
                        .commit();
                core = FireBaseCore.getInstance();
                core.getHistoryTripsForCurrentUser(new OnTripsLoaded() {
                    @Override
                    public void onTripsLoaded(List<Trip> trips) {

                        Uri mapUri = Uri.parse(getAllTrips(trips));
                        if (mapDelegate != null)
                            mapDelegate.setMapUri(mapUri);

                    }
                });


                break;
            case R.id.nav_logout:
                //log the user out

                core.getTripsForCurrentUser(new OnTripsLoaded() {
                    @Override
                    public void onTripsLoaded(List<Trip> trips) {
                        //receive all upcoming trips in list
                        //loop the list for all the trips
                        for (Trip t : trips) {
                            //get the date & time for each trip
                            cancelOneWayTripId = core.getTripCancelID(t);
                            cancelRoundWayTripId = core.getTripBackCancelID(t);
                            if (t.getType() == Trip.Type.ROUND_TRIP && t.getStatus() == Trip.Status.UPCOMMING) {
                                alarmTest.clearAlarm(cancelOneWayTripId,getBaseContext());
                                alarmTest.clearAlarm(cancelRoundWayTripId,getBaseContext());
                            } else if (t.getType() == Trip.Type.ONE_WAY && t.getStatus() == Trip.Status.UPCOMMING) {
                                alarmTest.clearAlarm(cancelOneWayTripId,getBaseContext());

                            }

                        }
                    }

                });


                //go to signin screen

                homePresenter.signOut();
                LoginManager.getInstance().logOut();
                SharedPreferences preferences = getSharedPreferences(PREF_NAME, 0);
                preferences.edit().clear().commit();
                Intent signoutIntent = new Intent(this, SigninActivity.class);
                startActivity(signoutIntent);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private String getAllTrips(List<Trip> trips) {
        StringBuilder url = new StringBuilder(GOOGLE_MAP_API);
        for (int i = 0; i < trips.size(); i++) {
            String color = Integer.toHexString(new Random().nextInt(16777215));
            url.append("&markers=color:green")
                    .append("|label:S|")
                    .append(trips.get(i).getStartLat())
                    .append(",")
                    .append(trips.get(i).getStartLg())
                    .append("&markers=color:red")
                    .append("|label:E|")
                    .append(trips.get(i).getEndLat())
                    .append(",")
                    .append(trips.get(i).getEndLg())
                    .append("&path=color:0x")
                    .append(color)
                    .append("|weight:5|")
                    .append(trips.get(i).getStartLat())
                    .append(",")
                    .append(trips.get(i).getStartLg())
                    .append("|")
                    .append(trips.get(i).getEndLat())
                    .append(",")
                    .append(trips.get(i).getEndLg());
        }
        url.append("&key=").append(API_KEY);
        return url.toString();
    }

    @Override
    public void sentMessage(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_LONG).show();

    }

    @Override
    public void sentError(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_LONG).show();

    }


    @Override
    public void changeActivity() {

    }

    @Override
    public void showUserInfo(UserModel model) {
        userNameTextView.setText(model.getName());
        emailTextView.setText(model.getEmail());
        Glide.with(this)
                .load(model.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait)
                .into(profilePictureView);
    }

    private void setViews() {
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        profilePictureView = header.findViewById(R.id.nav_profile_image);
        userNameTextView = header.findViewById(R.id.nav_header_userName);
        emailTextView = header.findViewById(R.id.nav_header_email);
        goToAddTrip = findViewById(R.id.floatingActionButton);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getUserInfo() {
        //set prsenter and firebase core
        core = FireBaseCore.getInstance();
        homePresenter = new HomePresenter(core, this);
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, 0);
        String phone = preferences.getString(PHONE_ARG, "");
        if (!phone.equals("")) {
            homePresenter.fetchUserInfoByPhone(phone);
        } else {
            homePresenter.fetchUserInFo();
        }
    }



}