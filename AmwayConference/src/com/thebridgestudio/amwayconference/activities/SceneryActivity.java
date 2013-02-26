package com.thebridgestudio.amwayconference.activities;

import com.brixd.amway_meeting.R;
import android.os.Bundle;

public class SceneryActivity extends WebViewActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    tag.setImageResource(R.drawable.tag_blueness);
    webview.loadUrl("file:///android_asset/scenery.html");
  }
}
