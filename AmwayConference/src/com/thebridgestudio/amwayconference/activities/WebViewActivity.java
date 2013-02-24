package com.thebridgestudio.amwayconference.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    webview.setWebViewClient(new WebViewClient() {

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.contains("scenery.html") || url.contains("hotel.html")) {
          tag.setVisibility(View.VISIBLE);
        } else {
          tag.setVisibility(View.GONE);
        }
        return super.shouldOverrideUrlLoading(view, url);
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (url.contains("scenery.html") || url.contains("hotel.html")) {
          tag.setVisibility(View.VISIBLE);
        } else {
          tag.setVisibility(View.GONE);
        }
        super.onPageStarted(view, url, favicon);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        if (url.contains("scenery.html") || url.contains("hotel.html")) {
          tag.setVisibility(View.VISIBLE);
        } else {
          tag.setVisibility(View.GONE);
        }
        super.onPageFinished(view, url);
      }

    });
  }

  @Override
  public void onBackPressed() {
    if (webview.canGoBack()) {
      webview.goBack();
    } else {
      super.onBackPressed();
    }
  }

}
