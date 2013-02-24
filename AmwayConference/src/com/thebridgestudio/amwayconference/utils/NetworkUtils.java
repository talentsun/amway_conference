package com.thebridgestudio.amwayconference.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager connManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
      return true;
    } else {
      return false;
    }
  }
}
