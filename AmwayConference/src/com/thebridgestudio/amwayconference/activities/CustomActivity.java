package com.thebridgestudio.amwayconference.activities;

import com.brixd.amway_meeting.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class CustomActivity extends FragmentActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
  }

  @Override
  protected void onResume() {
    super.onResume();
    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
  }
}
