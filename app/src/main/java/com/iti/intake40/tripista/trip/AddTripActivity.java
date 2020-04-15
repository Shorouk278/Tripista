package com.iti.intake40.tripista.trip;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.iti.intake40.tripista.AlarmReceiver;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.UpcommingTripAdapter;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.utils.AlarmTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class AddTripActivity extends AppCompatActivity implements AddTripContract.ViewInterface {
    final static int RQS_1 = 1;
    public int RQS;
    public String time;
    public String date;
    public Spinner mSpinner;
    public String coordinates;
    public String name;
    public int id = new Random().nextInt(10000);
    public int secId = new Random().nextInt(20000) + 1;
    String placeTAG = "place";
    //    Button backDateBtn;
//    Button backTimeBtn;
    String flag;
    Intent intent;
    String TAG = "addTripActivity";
    //UI items
    private ImageButton backDateBtn;
    private ImageButton backTimeBtn;
    private AutocompleteSupportFragment startAutoCompleteFragment;
    private AutocompleteSupportFragment endAutoCompleteFragment;
    private TextView info;
    private TextView titleTextView;
    private Button addTripBtn;
    private FireBaseCore core = FireBaseCore.getInstance();
    //    private Button timeBtn;
//    private Button dateBtn;
    Status status;
    private ImageButton timeBtn;
    private ImageButton dateBtn;
    private TextView returnDetails;
    private RadioGroup tripType;
    private RadioButton oneWayTrip;
    private RadioButton roundTrip;
    private RadioGroup tripRepeated;
    private RadioButton none;
    private RadioButton daily;
    private RadioButton weekly;
    private RadioButton monthly;

    //global variables
    private Trip tripModel;
    private String startPlace;
    private String endPlace;
    private String backStartPlace;
    private String backEndPlace;
    private String strDate;
    private String strTime;
    private String backStrDate;
    private String backStrTime;
    private DatePickerDialog datePicker;
    private DatePickerDialog datePicker2;
    private TimePickerDialog timePicker;
    private TimePickerDialog timePicker2;
    private Calendar cal;
    private Calendar cal2;
    private Calendar now;
    private Calendar current;
    private String[] routes;
    private String tripTitle;
    private int mYear, mMonth, mDay, hour, min, sec;
    private int mYear2, mMonth2, mDay2, hour2, minute2, sec2;
    private ArrayAdapter mAdapter;
    private AddTripContract.PresenterInterface addTripPresenter;
    private Intent updateIntent;
    private boolean isUpdate; // use the view to update or to ddd
    private boolean isRoundTrip; // is round trip or one way
    private double startLat;
    private double startLg;
    private double endLat;
    private double endLg;
    private Intent oneWayintent;
    private PendingIntent oneWayPendingIntent;
    private AlarmManager oneWayAlarmManager;
    private Boolean isRoundTripValid;
    AlarmTest alarmTest = new AlarmTest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent   setIntent = getIntent();

        setIntent.getStringExtra("firstLogin");
        setIntent.putExtra("firstLogin","");
        core = FireBaseCore.getInstance();
        tripModel = new Trip();
        cal = Calendar.getInstance();
        cal2 = Calendar.getInstance();
        now = Calendar.getInstance();
        current = Calendar.getInstance();
        core = FireBaseCore.getInstance();
        addTripPresenter = new AddTripPresenter(core, this);
        setViews();
        handleRadioButtons();
        handleRepeatedRadioButtons();
        getPlaces();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check if this is to edit trip
        updateIntent = getIntent();
        if (updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.ID) != null) {
            isUpdate = true;
            //set toolbar title
            AddTripActivity.this.setTitle(R.string.edit_trip);
            addTripBtn.setText(R.string.update_trip);
            titleTextView.setText(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.TITLE));
            startAutoCompleteFragment.setText(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.START_POINT));
            endAutoCompleteFragment.setText(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.END_POINT));
            String t = updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.TYPE);
            Log.d(placeTAG, "onStart: " + t);
            if (t.equals(Trip.Type.ONE_WAY.toString())) {
                //make one way selected
                setRoundTripVisability(View.GONE);
            } else {
                //make round trip selected
                //show back date and time buttons
                setRoundTripVisability(View.VISIBLE);
            }
        } else {
            isUpdate = false;
            addTripBtn.setText(R.string.add_trip);
            AddTripActivity.this.setTitle(R.string.add_new_trip);

        }
    }


    public void tripDate() {
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        datePicker = new DatePickerDialog(AddTripActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        cal.set(year, monthOfYear, dayOfMonth);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        strDate = dateFormat.format(cal.getTime());

                    }
                }, mYear, mMonth, mDay);
        //disable past date
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();


    }

    public void tripTime() {
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        sec = cal.get(Calendar.SECOND);
        sec = 0;
        timePicker = new TimePickerDialog(AddTripActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, 0);
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:00");
                        strTime = dateFormat.format(cal.getTime());


                    }
                }, hour, min, false);
        timePicker.show();

    }

    public void setViews() {
        setContentView(R.layout.activity_add_trip);
        addTripBtn = findViewById(R.id.addTrip);
        titleTextView = findViewById(R.id.title);
        info = findViewById(R.id.info);
        dateBtn = findViewById(R.id.dateBtn);
        backDateBtn = findViewById(R.id.backDate);
        backTimeBtn = findViewById(R.id.backTime);
        startAutoCompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.startfragment);

        endAutoCompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.endfragment);
        tripType = findViewById(R.id.trip_type);
        oneWayTrip = findViewById(R.id.one_way_trip);
        roundTrip = findViewById(R.id.round_trip);
        returnDetails = findViewById(R.id.return_details);

        returnDetails.setVisibility(View.GONE);
        backDateBtn.setVisibility(View.GONE);
        backTimeBtn.setVisibility(View.GONE);

        tripRepeated = findViewById(R.id.trip_repeated);
        none = findViewById(R.id.repeat_none);
        daily = findViewById(R.id.repeat_daily);
        weekly = findViewById(R.id.repeat_weekly);
        monthly = findViewById(R.id.repeat_monthly);
    }

    public void getPlaces() {
        // Initialize Places.
        String apiKey = "AIzaSyDIHrWWuzN2st31DRm6G9KnULCEKSpcV-A";
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDIHrWWuzN2st31DRm6G9KnULCEKSpcV-A");
        }


//StartAutoComplete
        startAutoCompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, Place.Field.LAT_LNG));
        startAutoCompleteFragment.setCountry("Eg");
        startAutoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(placeTAG, "Place: " + place.getName() + ", ");
                startPlace = place.getName();
                startLat = place.getLatLng().latitude;
                startLg = place.getLatLng().longitude;

                backEndPlace = startPlace;

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(placeTAG, "An error occurred: " + status);
            }
        });

//EndAutoComplete
        endAutoCompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, Place.Field.LAT_LNG));
        endAutoCompleteFragment.setCountry("Eg");
        endAutoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                System.out.println(place.getName());
                Log.i(placeTAG, "Place: " + place.getName() + ", " + place.getId());
                endPlace = place.getName();
                endLat = place.getLatLng().latitude;
                endLg = place.getLatLng().longitude;

                backStartPlace = endPlace;
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(placeTAG, "An error occurred: " + status);


            }
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void setBackDate(View view) {
        backTripDate();
    }

    public void setbackTime(View view) {
        backTripTime();
    }

    public void setDate(View view) {
        tripDate();
    }

    public void setTime(View view) {
        tripTime();
    }

    //onClickAddTripButton
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addTrip(View view) {
        if (isUpdate) {

            updateTrip();
        } else {
            tripTitle = titleTextView.getText().toString();
            if (isRoundTrip) {
                setRoundTrip();
                if(isRoundTripValid) {
                    setOneWayTrip();
                    setRoundTrip();

                }
            } else {
                setOneWayTrip();

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setOneWayTrip() {
        if (cal.compareTo(current) <= 0 || strDate == null || strTime == null || tripTitle == null || startPlace == null || endPlace == null) {
            Toast.makeText(getApplicationContext(),
                    "Invalid Data",
                    Toast.LENGTH_LONG).show();

        } else if (cal.compareTo(current) > 0 && strDate != null && strTime != null && tripTitle != null && startPlace != null && endPlace != null) {
            addTripToFirebase();
            addTripPresenter.addTrip(tripModel, cal);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setRoundTrip() {

        if (cal.compareTo(current) <= 0 || cal2.compareTo(current) <= 0 || cal2.compareTo(cal) <= 0 || tripTitle == null || startPlace == null || endPlace == null || strDate == null || strTime == null || backStrDate == null || backStrTime == null || cal2.compareTo(cal) == 0) {
            isRoundTripValid = false;
            Toast.makeText(getApplicationContext(),
                    "Invalid Data"
                    , Toast.LENGTH_LONG).show();

        } else if (cal.compareTo(current) > 0 && cal2.compareTo(current) > 0 && strDate != null && strTime != null && backStrTime != null && backStrDate != null && cal2.compareTo(cal) > 0 && tripTitle != null && startPlace != null && endPlace != null) {
            isRoundTripValid = true;
            setSecAlarm(cal2);
        }
    }

    public void backTripTime() {
        hour2 = cal2.get(Calendar.HOUR_OF_DAY);
        minute2 = cal2.get(Calendar.MINUTE);
        sec2 = cal2.get(Calendar.SECOND);
        sec2 = 0;
        timePicker2 = new TimePickerDialog(AddTripActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        cal2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal2.set(Calendar.MINUTE, minute);
                        cal2.set(Calendar.SECOND, 0);
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:00");
                        backStrTime = dateFormat.format(cal2.getTime());
                    }
                }, hour2, minute2, false);
        timePicker2.show();
    }

    public void backTripDate() {
        mYear2 = cal2.get(Calendar.YEAR);
        mMonth2 = cal2.get(Calendar.MONTH);
        mDay2 = cal2.get(Calendar.DAY_OF_MONTH);
        datePicker2 = new DatePickerDialog(AddTripActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal2.set(Calendar.YEAR, year);
                        cal2.set(Calendar.MONTH, month);
                        cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        backStrDate = dateFormat.format(cal2.getTime());
                    }
                }, mYear2, mMonth2, mDay2);
        //disable past date
        datePicker2.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker2.show();
    }

    public void addTripToFirebase() {
        //calculate distance
        double dist = distance(startLat, startLg, endLat, endLg);
        core = FireBaseCore.getInstance();
        tripModel.setDistance(dist);
        tripModel.setTitle(tripTitle);
        tripModel.setStartPoint(startPlace);
        tripModel.setEndPoint(endPlace);
        tripModel.setDate(strDate);
        tripModel.setTime(strTime);
        tripModel.setCancelID(id);
        tripModel.setStartLat(startLat);
        tripModel.setStartLg(startLg);
        tripModel.setEndLat(endLat);
        tripModel.setEndLg(endLg);
//        tripModel.setStatus(status);
        //set trip type to upcoming
        tripModel.setStatus(Trip.Status.UPCOMMING);
        //set trip type to round or one way
        if (isRoundTrip) {
            tripModel.setBackDate(backStrDate);
            tripModel.setBackTime(backStrTime);
            tripModel.setBackStartPoint(backStartPlace);
            tripModel.setBackEndPoint(backEndPlace);
            tripModel.setType(Trip.Type.ROUND_TRIP);
            tripModel.setBackCancelID(secId);
        } else {
            tripModel.setType(Trip.Type.ONE_WAY);
        }

        //handle trip repeatation

        if (tripModel.getRepeatation().toString().equals(null)) {
            tripModel.setRepeatation(Trip.Repeatation.NONE);
        }
        switch (tripModel.getRepeatation()) {
            case NONE:
                break;
            case DAILY:
                break;
            case WEEKLY:
                break;
            case MONTHLY:
                break;
        }

        Toast.makeText(getApplicationContext(),
                "Trip added!",
                Toast.LENGTH_LONG).show();
        //after the trip is added finish the activity
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateTrip() {

        Trip trip = new Trip();
        alarmTest.clearAlarm(trip.getCancelID(),getBaseContext());

        if (strDate == null || strTime == null) {
            Toast.makeText(this, "enter date and time", Toast.LENGTH_SHORT).show();
        } else {
//            alarmTest.setSpecificAlarm(cal,getBaseContext(),id,trip.getTitle(),trip.getTripId());
            if (startPlace == null) {
                trip.setStartPoint(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.START_POINT));
                trip.setStartLg(Double.parseDouble(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.START_LG)));
                trip.setStartLat(Double.parseDouble(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.START_LT)));
            } else {
                trip.setStartPoint(startPlace);
                trip.setStartLat(startLat);
                trip.setStartLg(startLg);
            }

            if (endPlace == null) {
                trip.setEndPoint(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.END_POINT));
                trip.setEndLg(Double.parseDouble(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.END_LG)));
                trip.setEndLat(Double.parseDouble(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.END_LT)));
            } else {
                trip.setEndPoint(endPlace);
                trip.setEndLat(endLat);
                trip.setEndLg(endLg);
            }


            trip.setTripId(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.ID));
            trip.setTitle(titleTextView.getText().toString());


            trip.setDate(strDate);
            trip.setTime(strTime);
            trip.setDistance(Double.parseDouble(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.DISTANCE)));

            trip.setStatus(Trip.Status.UPCOMMING);
            //trip.setType(updateIntent.getStringExtra(UpcommingTripAdapter.IntentKeys.TYPE));

            //clear old alarms

            //add new alarms



            //check trip type
            if (isRoundTrip) {
                trip.setType(Trip.Type.ROUND_TRIP);
                trip.setBackDate(backStrDate);
                trip.setBackTime(backStrTime);
                trip.setBackStartPoint(backStartPlace);
                trip.setBackEndPoint(backEndPlace);
            } else {
                trip.setType(Trip.Type.ONE_WAY);
            }
            core.updateTrip(trip);
            // after updating the trip finish the activity
            finish();
        }
    }

    @Override
    public void sentMessage(int message) {

    }

    @Override
    public void sentError(int message) {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void setAlarm(Trip trip, Calendar calendar,String status,String type) {
        oneWayintent = new Intent(getBaseContext(), AlarmReceiver.class);
        status =  trip.getStatus().toString();
        type = trip.getType().toString();
        oneWayintent.putExtra("id", trip.getTripId());
        oneWayintent.putExtra("title", trip.getTitle());
        oneWayintent.putExtra("status", status);
        oneWayintent.putExtra("type", type);
        oneWayintent.putExtra("secId",secId);

        oneWayPendingIntent = PendingIntent.getBroadcast(getBaseContext(), id, oneWayintent, PendingIntent.FLAG_UPDATE_CURRENT);
        oneWayAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        oneWayAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), oneWayPendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setSecAlarm(Calendar targetCal) {

        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        intent.putExtra("id", tripModel.getTripId());
        intent.putExtra("title", tripModel.getTitle());
        intent.putExtra("status","IN_PROGRESS");
        intent.putExtra("type","ROUND_TRIP");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), secId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }


    private Trip createTripFromInput() {
        Trip trip = new Trip();

        return trip;
    }

    private void handleRadioButtons() {
        tripType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.one_way_trip:
                        setRoundTripVisability((View.GONE));
                        isRoundTrip = false;
                        break;

                    case R.id.round_trip:
                        setRoundTripVisability((View.VISIBLE));
                        isRoundTrip = true;
                        break;
                }
            }
        });
    }

    private void handleRepeatedRadioButtons() {
        tripRepeated.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.repeat_none:
                        tripModel.setRepeatation(Trip.Repeatation.NONE);
                        break;

                    case R.id.repeat_daily:
                        tripModel.setRepeatation(Trip.Repeatation.DAILY);
                        break;

                    case R.id.repeat_weekly:
                        tripModel.setRepeatation(Trip.Repeatation.WEEKLY);
                        break;

                    case R.id.repeat_monthly:
                        tripModel.setRepeatation(Trip.Repeatation.MONTHLY);
                        break;
                }
            }
        });
        none.performClick();
    }

    private void setRoundTripVisability(int visability) {
        returnDetails.setVisibility(visability);
        backDateBtn.setVisibility(visability);
        backTimeBtn.setVisibility(visability);
    }
}