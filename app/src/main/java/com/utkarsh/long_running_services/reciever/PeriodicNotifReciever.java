package com.utkarsh.long_running_services.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.utkarsh.long_running_services.services.PeriodicNotifServices;

public class PeriodicNotifReciever extends BroadcastReceiver {

    /*
    * onReceive method call when
    * app destoyed
    * reciever method will start service if
    * service got destroyed
    * start service on the basis of android version
    * */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent (context, PeriodicNotifServices.class));
        } else {
            context.startService(new Intent (context, PeriodicNotifServices.class));
        }
    }
}
