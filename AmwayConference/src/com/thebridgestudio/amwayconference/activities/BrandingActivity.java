package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Config;
import com.brixd.amway_meeting.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

public class BrandingActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.branding);

    new BrandingTask().execute();
  }

  class BrandingTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      super.onPostExecute(result);

      Intent enterIntent = new Intent();
      if (!TextUtils.isEmpty(Config.getAccount(BrandingActivity.this))) {
        enterIntent.setClass(BrandingActivity.this, ScheduleActivity.class);
      } else {
        enterIntent.setClass(BrandingActivity.this, WelcomeActivity.class);
      }
      startActivity(enterIntent);
      finish();
    }

  }
}
