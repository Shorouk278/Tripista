package com.iti.intake40.tripista;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.Trip;
import com.iti.intake40.tripista.map.ShowMap;
import com.iti.intake40.tripista.utils.AlarmTest;

import java.util.Random;
import java.util.UUID;




public class AlertActivity extends Activity {
    NotificationManager notificationManager;
    Uri notification;
    Ringtone ringtone;
    Intent intent;
    String intentExtra;
    String tripIdIntent;
    int tripCancelId;
    public int notificationId = new Random().nextInt(10000);
    private FireBaseCore core;
    String tripStatus;
    String tripType;
    Intent roundIntent = new Intent();
    Intent p = new Intent();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_alert);
        core = FireBaseCore.getInstance();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        intent = getIntent();
        intentExtra = intent.getStringExtra("title");
        tripIdIntent = intent.getStringExtra("id");
        tripStatus = intent.getStringExtra("status");
        tripType = intent.getStringExtra("type");
        tripCancelId = intent.getIntExtra("secId",0);


        Toast.makeText(getBaseContext(),
                "beginId" + tripIdIntent,
                Toast.LENGTH_LONG).show();
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this.getApplicationContext(), notification);
        ringtone.play();
        displayAlert();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ringtone.play();
    }

    private void displayAlert() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(intentExtra).setCancelable(
                false).setPositiveButton("start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (getIntent() != null) {

                    if (tripType.equals(Trip.Type.ONE_WAY.toString())) {
                        core.changeStateOfTrip(Trip.Status.DONE.toString(), tripIdIntent);
                    } else {


                        //if it's round trip check for status first
                        if (tripStatus.equals(Trip.Status.UPCOMMING.toString())) {
                            core.changeStateOfTrip(Trip.Status.IN_PROGRESS.toString(), tripIdIntent);
                         }
                        if (tripStatus.equals(Trip.Status.IN_PROGRESS.toString())) {
                            core.changeStateOfTrip(Trip.Status.DONE.toString(), tripIdIntent);
                        }

                    }

                    Intent goMap = new Intent(AlertActivity.this, ShowMap.class);
                    goMap.putExtra("id", tripIdIntent);
                    startActivity(goMap);
                    dialogInterface.cancel();
                    ringtone.stop();
                    finish();


                }
            }
        }).setNeutralButton("snooze",
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        //core.changeStateOfTrip(, tripId);
                        Toast.makeText(getBaseContext(),
                                "snooze" + tripStatus,
                                Toast.LENGTH_LONG).show();

                        id = notificationId;
                        show_Notification();
                        ringtone.stop();
                    }


                }).setNegativeButton("stop",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tripIdIntent = getIntent().getExtras().getString("id");
                        core.changeStateOfTrip(Trip.Status.CANCELLED.toString(), tripIdIntent );
                        id = notificationId;
                        NotificationManager notificationManager = (NotificationManager)
                                getSystemService(Context.
                                        NOTIFICATION_SERVICE);
                        AlarmTest alarmTest = new AlarmTest();
                        alarmTest.clearAlarm(tripCancelId,getBaseContext());
                        notificationManager.cancel(id);
                        dialog.cancel();
                        ringtone.stop();
                        finish();

                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_Notification() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Intent notificationIntent = new Intent(getApplicationContext(), AlertActivity.class);
        String CHANNEL_ID = generateString();
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH);
        notificationIntent.putExtra("title",intentExtra);
        notificationIntent.putExtra("id",tripIdIntent);
        notificationIntent.putExtra("status",tripStatus);
        notificationIntent.putExtra("type",tripType);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentText("You are waiting for your trip")
                .setContentTitle(intentExtra)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon((BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher_foreground)))
                .setChannelId(CHANNEL_ID)
                .setOngoing(true)
                .setAutoCancel(true)
                .build();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(notificationId, notification);

//        } else {
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            Notification notification = new Notification.Builder(getApplicationContext())
//                    .setContentText("Your trip is waiting")
//                    .setContentTitle(intentExtra)
//                    .setContentIntent(pendingIntent)
//                    .setVibrate(new long[]{1000, 1000})
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setLargeIcon((BitmapFactory.decodeResource(this.getResources(),
//                            R.mipmap.ic_launcher_foreground)))
//                    .setOngoing(true)
//                    .setAutoCancel(true)
//                    .build();
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(notificationId, notification);
//        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ringtone.stop();
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return "uuid = " + uuid;
    }


}



