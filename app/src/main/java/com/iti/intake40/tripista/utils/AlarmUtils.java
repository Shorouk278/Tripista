package com.iti.intake40.tripista.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

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
import static com.facebook.FacebookSdk.getApplicationContext;

public class AlarmUtils {


    String date = null;
    String time = null;
    int notificationId;
    FireBaseCore core = FireBaseCore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setMultipleAlarms(final Context context) {
        core.getTripsForCurrentUser(new OnTripsLoaded() {
            @Override
            public void onTripsLoaded(List<Trip> trips) {
                //receive all upcoming trips in list
                //loop the list for all the trips
                for (Trip t : trips) {
                    String id = t.getTripId();
                    //get the date & time for each trip
                    if (t.getStatus() == Trip.Status.UPCOMMING && t.getType() == Trip.Type.ONE_WAY) {
                        date = t.getDate();
                        time = t.getTime();
                        notificationId = t.getCancelID();
                        //set alarm
                        setAlarm(notificationId, t.getTitle(), t.getTripId(),DateUtils.getDateArr(date), DateUtils.getTimeArr(time),context);
                    }
                    //if the trip is round get the back date & time
                    if (t.getStatus() == Trip.Status.UPCOMMING && t.getType() == Trip.Type.ROUND_TRIP) {
                        date = t.getDate();
                        time = t.getTime();
                        notificationId = t.getCancelID();
                        //set alarm
                        setAlarm(notificationId, t.getTitle(), t.getTripId(),DateUtils.getDateArr(date), DateUtils.getTimeArr(time),context);

                        date = t.getBackDate();
                        time = t.getBackTime();
                        notificationId = t.getBackCancelID();
                        //set alarm
                        setAlarm(notificationId, t.getTitle(), t.getTripId(),DateUtils.getDateArr(date), DateUtils.getTimeArr(time),context);
                    } else if (t.getStatus() == Trip.Status.IN_PROGRESS && t.getType() == Trip.Type.ROUND_TRIP) {
                        date = t.getBackDate();
                        time = t.getBackTime();
                        notificationId = t.getBackCancelID();
                        //set alarm
                        setAlarm(notificationId, t.getTitle(), t.getTripId(),DateUtils.getDateArr(date), DateUtils.getTimeArr(time),context);
                    }
                }
            }
        });

    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  void setAlarm(int alarmId, String alarmTitle,String tripId
                                ,String[] dateArr, String[] timeArr,Context context) {

        Calendar myAlarmDate = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        myAlarmDate.set(Integer.parseInt(dateArr[0]),
                Integer.parseInt(dateArr[1]) - 1,
                Integer.parseInt(dateArr[2]),
                Integer.parseInt(timeArr[0]),
                Integer.parseInt(timeArr[1]),
                Integer.parseInt(timeArr[2]));
        AlarmManager tripAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent tripAlarmIntent = new Intent(context, AlertActivity.class);
        tripAlarmIntent.putExtra("title", alarmTitle);
        tripAlarmIntent.putExtra("id", tripId);
        if (myAlarmDate.compareTo(current) > 0) {
            PendingIntent tripAlarmPendingIntent = PendingIntent.getActivity(context, alarmId,
                    tripAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            tripAlarmManager.setExact(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), tripAlarmPendingIntent);
        }
    }
    public  static void clearAlarm(int alarmId,final Context context) {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}
