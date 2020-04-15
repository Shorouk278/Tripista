package com.iti.intake40.tripista.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.FirebaseApp;
import com.iti.intake40.tripista.AlarmReceiver;
import com.iti.intake40.tripista.AlertActivity;
import com.iti.intake40.tripista.OnTripsLoaded;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;


public class AlarmTest {

    String date;
    String time;
    String[] myformat;
    String[] myformat2;

    Context context;
    Intent intent;
    FireBaseCore core = FireBaseCore.getInstance();
    public int notificationId;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public void setAlarms(final Context context) {
        core.getTripsForCurrentUser(new OnTripsLoaded() {
            @Override
            public void onTripsLoaded(List<Trip> trips) {

                //receive all upcoming trips in list
                //loop the list for all the trips
                for (Trip t : trips) {
                    //get the date & time for each trip

                    if (t.getStatus() == Trip.Status.UPCOMMING && t.getType() == Trip.Type.ONE_WAY) {

                        date = t.getDate();
                        time = t.getTime();
                        notificationId = t.getCancelID();
                        //set alarm
                        setAlarm(notificationId, t.getTitle(),context,t.getTripId());

                        setAlarm(notificationId, t.getTitle(), context, t.getTripId());

                        Toast.makeText(context,
                                "set Alarm" + notificationId,
                                Toast.LENGTH_LONG).show();
                    }
                    //if the trip is round get the back date & time
                    if (t.getStatus() == Trip.Status.UPCOMMING && t.getType() == Trip.Type.ROUND_TRIP) {
                        date = t.getDate();
                        time = t.getTime();
                        notificationId = t.getCancelID();
                        //set alarm
                        setAlarm(notificationId, t.getTitle(),context,t.getTripId());

                        date = t.getBackDate();
                        time = t.getBackTime();
                        notificationId = t.getBackCancelID();
                        //set alarm

                        setSecAlarm(notificationId, t.getTitle(),context);

                    }
                }

            }

        });

    }


    public String[] getDate() {
        myformat = date.split("-");
        System.out.println(myformat[0]);
        System.out.println(myformat[1]);
        System.out.println(myformat[2]);
        return myformat;
    }

    public String[] getTime() {
        myformat2 = time.split(":");
        System.out.println(myformat2[0]);
        System.out.println(myformat2[1]);
        System.out.println(myformat2[2]);
        return myformat2;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public void setAlarm(int id, String str, Context context, String tripId) {
        String[] dateArr = getDate();
        String[] timeArr = getTime();
        Calendar current = Calendar.getInstance();
        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
        AlarmManager tripAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent tripAlarmIntent = new Intent(context, AlertActivity.class);
        tripAlarmIntent.putExtra("title", str);
        tripAlarmIntent.putExtra("id", tripId);
        if (myAlarmDate.compareTo(current) >= 0) {
            PendingIntent tripAlarmPendingIntent = PendingIntent.getActivity(context, id, tripAlarmIntent, PendingIntent.FLAG_ONE_SHOT);
            tripAlarmManager.setExact(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), tripAlarmPendingIntent);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setSecAlarm(int id, String str, Context context) {
        String[] dateArr = getDate();
        String[] timeArr = getTime();
        Calendar myAlarmDate = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        myAlarmDate.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]) + 12, Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
        AlarmManager tripAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent tripAlarmIntent = new Intent(context, AlertActivity.class);
        if (myAlarmDate.compareTo(current) >= 0) {
            PendingIntent tripAlarmPendingIntent = PendingIntent.getActivity(context, id, tripAlarmIntent, PendingIntent.FLAG_ONE_SHOT);
            tripAlarmManager.setExact(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), tripAlarmPendingIntent);
        }

    }

    public void clearAlarm(int alarmId, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);


    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setSpecificAlarm(Calendar targetCal,Context context,int id,String title,String tripId) {

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", tripId);
        intent.putExtra("title", title);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);

    }

}





