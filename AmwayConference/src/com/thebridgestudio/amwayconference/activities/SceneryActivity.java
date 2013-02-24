package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.R;
import android.os.Bundle;

public class SceneryActivity extends WebViewActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    tag.setImageResource(R.drawable.tag_green);
    webview.loadUrl("file:///android_asset/scenery.html");
  }
}
