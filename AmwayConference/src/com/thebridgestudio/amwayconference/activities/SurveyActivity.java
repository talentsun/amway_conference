package com.thebridgestudio.amwayconference.activities;

import com.brixd.amway_meeting.R;

import android.os.Bundle;

public class SurveyActivity extends WebViewActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    tag.setImageResource(R.drawable.tag_yellow);
    webview.loadUrl("http://a.brixd.com/amwaysurvey/index.html");
  }
}
