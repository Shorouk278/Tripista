package com.iti.intake40.tripista;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.google.firebase.FirebaseApp;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;
import java.util.Calendar;
import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {


    String date;
    String time;
    String[] myformat;
    String[] myformat2;
    Context context;
    Intent intent;

    public int notificationId;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context arg0, Intent arg1) {

        FirebaseApp.initializeApp(arg0);
        if (Intent.ACTION_BOOT_COMPLETED.equals(arg1.getAction())) {
            FireBaseCore core = FireBaseCore.getInstance();
            context = arg0;
            intent = arg1;
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
                            setAlarm(notificationId, t.getTitle());
                        }
                        //if the trip is round get the back date & time
                        if (t.getStatus() == Trip.Status.UPCOMMING && t.getType() == Trip.Type.ROUND_TRIP) {
                            date = t.getDate();
                            time = t.getTime();
                            notificationId = t.getCancelID();
                            //set alarm
                            setAlarm(notificationId, t.getTitle());
                            date = t.getBackDate();
                            time = t.getBackTime();
                            notificationId = t.getBackCancelID();
                            //set alarm
                            setSecAlarm(notificationId, t.getTitle());
                        }
                    }
                }

            });

        } else {

            if (arg1.getExtras().getString("id") != null) {
                String tripTitle = arg1.getStringExtra("title");
                String id = arg1.getExtras().getString("id");
                String tripStatus = arg1.getStringExtra("status");
                String tripType = arg1.getStringExtra("type");
                int tripCancelId = arg1.getIntExtra("secId",0);
                Intent alarmIntent = new Intent(arg0, AlertActivity.class);
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmIntent.putExtra("id", id);
                alarmIntent.putExtra("title", tripTitle);
                alarmIntent.putExtra("status", tripStatus);
                alarmIntent.putExtra("type", tripType);
                alarmIntent.putExtra("secId",tripCancelId);
                arg0.startActivity(alarmIntent);

            }
        }


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
    public void setAlarm(int id, String str) {
        String[] dateArr = getDate();
        String[] timeArr = getTime();
        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
        AlarmManager tripAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent tripAlarmIntent = new Intent(context, AlertActivity.class);
        tripAlarmIntent.putExtra("title", str);

        PendingIntent tripAlarmPendingIntent = PendingIntent.getActivity(context, id, tripAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        tripAlarmManager.setExact(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), tripAlarmPendingIntent);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setSecAlarm(int id, String str) {
        String[] dateArr = getDate();
        String[] timeArr = getTime();
        Calendar myAlarmDate = Calendar.getInstance();
        myAlarmDate.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]) - 1, Integer.parseInt(dateArr[2]), Integer.parseInt(timeArr[0]) + 12, Integer.parseInt(timeArr[1]), Integer.parseInt(timeArr[2]));
        AlarmManager tripAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent tripAlarmIntent = new Intent(context, AlertActivity.class);
        PendingIntent tripAlarmPendingIntent = PendingIntent.getActivity(context, id, tripAlarmIntent, 0);
        tripAlarmManager.setExact(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), tripAlarmPendingIntent);


    }

}



