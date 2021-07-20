package com.utkarsh.long_running_services.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.utkarsh.long_running_services.Constant;
import com.utkarsh.long_running_services.R;
import com.utkarsh.long_running_services.activity.NotificationActivity;
import com.utkarsh.long_running_services.reciever.PeriodicNotifReciever;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicNotifServices extends Service {

    PeriodicNotifReciever periodicNotifReciever;
    private NotificationManager mNotificationManager;
    private int count = 0;

    /*
     * Called once when service will start
     * and it start service on device version
     * Method register also broadcast reciever
     * because if any time service
     * destroyed reciever can start service
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        //Check device version to start service
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }
        // Registering reciver
        try {
            registerReceiver(periodicNotifReciever, new IntentFilter("GET_SIGNAL_STRENGTH"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * Method for starting Foreground
     * Creating NotificationChannel
     * Building Foreground Notification bar
     * after starting foreground services
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.utkarsh.long_running_services";
        String channelName = "DemoApp Services";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    /*
     * Life cycle method it always call
     * when startService call
     * here we are  executing our task
     * for long running operation
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                count++;
                Log.e("Service Running", "Number Of Times" + count);
                sendNotification();
            }
        }, 0, 1, TimeUnit.MINUTES);

        return START_STICKY;
    }

    /*
     * if Some reason tas has been removed
     * then this method call and restart
     * */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    /*
     * Lifecycle Method when service
     * destroyed we will restart wether user want to
     * restart or not
     * Constant.ForceStoped is a flag which tells us User Attention
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Constant.forceStoped) {
            stopForeground(false);
        } else {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartService");
            broadcastIntent.setClass(this, PeriodicNotifReciever.class);
            this.sendBroadcast(broadcastIntent);
        }
    }

    /*
     * Life Cycle Method in case of started service it returns null
     * in case of bound services it returns some value
     * */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * Method for sending periodic Notification
     * on every 2 min we got notification
     * showing notificatio this method handled
     * creating Notification also handled here for
     * every time new generated notification
     *
     * */
    private void sendNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        mBuilder.setContentTitle(getString(R.string.notif_title));
        mBuilder.setContentText(getString(R.string.notif_desc) + count);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.channelID);
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.demo_app_channel),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());

    }


}

