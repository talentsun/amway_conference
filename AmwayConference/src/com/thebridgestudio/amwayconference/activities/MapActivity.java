package com.thebridgestudio.amwayconference.activities;

import com.brixd.amway_meeting.R;

import android.os.Bundle;

public class MapActivity extends WebViewActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    tag.setImageResource(R.drawable.tag_red);
    webview.loadUrl("file:///android_asset/hotel.html");
  }
}
