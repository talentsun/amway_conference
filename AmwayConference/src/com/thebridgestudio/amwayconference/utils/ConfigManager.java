package com.thebridgestudio.amwayconference.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigManager {
  private static final String SHARED_PREFERENCE_NAME = "amy_config";
  private static final String KEY_AUTO_FOCUS = "AUTO_FOCUS";
  private static final String KEY_FRONT_LIGHT = "FRONT_LIGHT";
  private static final String KEY_DECODE_1D = "DECODE_1D";
  private static final String KEY_DECODE_QR = "DECODE_QR";
  private static final String KEY_DECODE_DATA_MATRIX = "DECODE_DATA_MATRIX";
  private static ConfigManager configManager;
  private SharedPreferences pref;

  private ConfigManager(Context context) {
    pref = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
        Context.MODE_WORLD_WRITEABLE);
  }

  // you should use application context
  public static synchronized ConfigManager getInstance(Context context) {
    if (configManager != null) {
      return configManager;
    } else {
      return new ConfigManager(context);
    }
  }

  public void setAutoFocus(boolean b) {
    Editor editor = pref.edit();
    editor.putBoolean(KEY_AUTO_FOCUS, b);
    editor.commit();
  }

  public boolean getFrontLight() {
    return pref.getBoolean(KEY_FRONT_LIGHT, false);
  }

  public void setFrontLight(boolean b) {
    Editor editor = pref.edit();
    editor.putBoolean(KEY_FRONT_LIGHT, b);
    editor.commit();
  }

  public boolean getDecode1D() {
    return pref.getBoolean(KEY_DECODE_1D, false);
  }

  public void setDecode1D(boolean b) {
    Editor editor = pref.edit();
    editor.putBoolean(KEY_DECODE_1D, b);
    editor.commit();
  }

  public boolean getDecodeQr() {
    return pref.getBoolean(KEY_DECODE_QR, false);
  }

  public void setDecodeQr(boolean b) {
    Editor editor = pref.edit();
    editor.putBoolean(KEY_DECODE_QR, b);
    editor.commit();
  }

  public boolean getDecodeDataMatrix() {
    return pref.getBoolean(KEY_DECODE_DATA_MATRIX, false);
  }

  public void setDecodeDataMatrix(boolean b) {
    Editor editor = pref.edit();
    editor.putBoolean(KEY_DECODE_DATA_MATRIX, b);
    editor.commit();
  }

  public boolean getAutoFocus() {
    return pref.getBoolean(KEY_AUTO_FOCUS, true);
  }
}
