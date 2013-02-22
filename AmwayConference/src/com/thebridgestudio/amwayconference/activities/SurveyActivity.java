package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.R;

import android.os.Bundle;

public class SurveyActivity extends WebViewActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    tag.setImageResource(R.drawable.tag_green);
    webview.loadUrl("http://www.brixd.com/amwaysurvey/index.html");
  }
}
