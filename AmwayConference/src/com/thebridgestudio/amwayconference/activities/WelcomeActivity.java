package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.services.DataService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {
  private static final String TAG = "WelcomeActivity";

  // Views
  private Button mScanEntry;
  private Button mLogin;

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
  }

  private void initViews() {
    mScanEntry = (Button) findViewById(R.id.scan_entry);
    mLogin = (Button) findViewById(R.id.login);
  }

  private void initListener() {
    mScanEntry.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent scanIntent = new Intent(getApplicationContext(),
            CaptureActivity.class);
        startActivity(scanIntent);
      }
    });

    mLogin.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent loginIntent = new Intent(getApplicationContext(),
            LoginActivity.class);
        startActivity(loginIntent);
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
