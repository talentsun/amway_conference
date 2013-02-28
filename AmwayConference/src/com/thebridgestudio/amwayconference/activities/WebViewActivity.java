package com.thebridgestudio.amwayconference.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.brixd.amway_meeting.R;
import com.thebridgestudio.amwayconference.utils.NetworkUtils;

public class WebViewActivity extends BaseActivity {
  protected WebView webview;
  protected ImageView tag;
  protected ImageView loading;
  protected TextView noNetwork;

  @Override
  protected void onResume() {
    super.onResume();
    if (!NetworkUtils.isNetworkConnected(this)) {
      noNetwork.setVisibility(View.VISIBLE);
      webview.setVisibility(View.GONE);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.webview);

    initSidebar();

    webview = (WebView) findViewById(R.id.webview);
    tag = (ImageView) findViewById(R.id.tag);
    loading = (ImageView) findViewById(R.id.loading);
    noNetwork = (TextView) findViewById(R.id.no_network);

    webview.getSettings().setJavaScriptEnabled(true);
    webview.setDrawingCacheEnabled(true);
    webview.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!TextUtils.isEmpty(url)
            && (url.contains("terms_maps.html") || url.contains("local_url") ||
                url.contains("toscountry") || url.contains("cbk0.googleapis.com"))) {
          return true;
        }
        
        if (!TextUtils.isEmpty(url) && url.contains("tel:")) {
          Intent intent = new Intent();
          intent.setAction(Intent.ACTION_CALL);
          intent.setData(Uri.parse(url));
          startActivity(intent);
          
          return true;
        }
        
        return super.shouldOverrideUrlLoading(view, url);
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (!TextUtils.isEmpty(url)
            && (url.contains("map1") || url.contains("map2") || url.contains("map3") || url
                .contains("map4"))) {
          disableFling();
        } else {
          enableFling();
        }
        
        updateTag(url);
        loading.setVisibility(View.VISIBLE);
        super.onPageStarted(view, url, favicon);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        loading.setVisibility(View.GONE);
        super.onPageFinished(view, url);
      }

    });
    
    webview.setOnTouchListener(new OnTouchListener() {
      
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        onTouchEvent(event);
        return false;
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

  private void updateTag(String url) {
    if (url.contains("scenery.html")
        || url.contains("hotel.html") || url.contains("http://a.brixd.com/amwaysurvey/index.html")) {
      tag.setVisibility(View.VISIBLE);
    } else {
      tag.setVisibility(View.GONE);
    }
  }

}
