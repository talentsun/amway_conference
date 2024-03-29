package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.brixd.amway_meeting.R;
import com.thebridgestudio.amwayconference.services.DataService;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends CustomActivity {
  private static final String TAG = "WelcomeActivity";

  // Views
//  private Button mScanEntry;
  private Button mLogin;

  private FinishReceiver mFinishReceiver;

  public class FinishReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      WelcomeActivity.this.finish();
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (!TextUtils.isEmpty(Config.getAccount(this))) {
      Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
      startActivity(scheduleIntent);
      finish();
    }

    super.onCreate(savedInstanceState);
    setContentView(R.layout.welcome);

    initViews();
    initListener();

    mFinishReceiver = new FinishReceiver();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intents.ACTION_FINISH);
    registerReceiver(mFinishReceiver, intentFilter);
  }

  @Override
  protected void onDestroy() {
    if (mFinishReceiver != null) {
      unregisterReceiver(mFinishReceiver);
    }
    super.onDestroy();
  }

  private void initViews() {
//    mScanEntry = (Button) findViewById(R.id.scan_entry);
    mLogin = (Button) findViewById(R.id.login);
  }

  private void initListener() {
//    mScanEntry.setOnClickListener(new View.OnClickListener() {
//
//      @Override
//      public void onClick(View v) {
//        Intent scanIntent = new Intent(getApplicationContext(),
//            CaptureActivity.class);
//        startActivityForResult(scanIntent, 0);
//      }
//    });

    mLogin.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent loginIntent = new Intent(getApplicationContext(),
            LoginActivity.class);
        startActivityForResult(loginIntent, 0);
      }
    });
  }

  @Override
  protected void onStart() {
    Intent intent = new Intent();
    intent.setClass(WelcomeActivity.this, DataService.class);
    intent.setAction(Intents.ACTION_SYNC_ALL);

    startService(intent);
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }
}
