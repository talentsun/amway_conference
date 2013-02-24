package com.thebridgestudio.amwayconference.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.services.DataService;

public class StartupReceiver extends BroadcastReceiver {
  private final static String TAG = "StartupReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    if (TextUtils.isEmpty(Config.getAccount(context))) {
      Log.i(TAG, "no account, not sync message per 30mins");
      return;
    }
    String action = intent.getAction();
    if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
      registerAlarmManager(context);
    }
  }

  private void registerAlarmManager(Context context) {
    Intent intent = new Intent();
    intent.setAction(Intents.ACTION_REGISTER_ALARMMANAGER);
    intent.setClass(context, DataService.class);
    context.startService(intent);
  }
}
