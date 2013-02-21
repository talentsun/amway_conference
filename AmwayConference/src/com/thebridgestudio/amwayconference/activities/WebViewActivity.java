package com.thebridgestudio.amwayconference.activities;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import com.thebridgestudio.amwayconference.R;

public class WebViewActivity extends BaseActivity {
  protected WebView webview;
  protected ImageView tag;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.webview);

    initSidebar();

    webview = (WebView) findViewById(R.id.webview);
    tag = (ImageView) findViewById(R.id.tag);
    
    webview.getSettings().setJavaScriptEnabled(true);

  }


}
