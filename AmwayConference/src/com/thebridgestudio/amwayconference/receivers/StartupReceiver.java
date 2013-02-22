package com.thebridgestudio.amwayconference.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.services.DataService;
import com.thebridgestudio.amwayconference.utils.NetworkUtils;

public class StartupReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (NetworkUtils.isNetworkConnected(context)) {
                registerAlarmManager(context);
            } else {
                unregisterAlarmManager(context);
            }
        }
    }
    
    private void registerAlarmManager(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intents.ACTION_REGISTER_ALARMMANAGER);
        intent.setClass(context, DataService.class);
        context.startService(intent);
    }
    
    private void unregisterAlarmManager(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intents.ACTION_UNREGISTER_ALARMMANAGER);
        intent.setClass(context, DataService.class);
        context.startService(intent);
    }

}
