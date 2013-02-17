package com.thebridgestudio.amwayconference;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {
    private static final String PREFS_NAME = "awmay_conference";
    private static final String KEY_LAST_SYNC_MESSAGE_TIME = "last_sync_message_time";
    private static final String KEY_LAST_SYNC_SCHEDULE_TIME = "last_sync_schedule_time";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_NAME = "name";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    
    public static long getLastSyncMessageTime(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(KEY_LAST_SYNC_MESSAGE_TIME, 0);
    }
    
    public static void setLastSyncMessageTime(Context context, long lastSyncTime) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(KEY_LAST_SYNC_MESSAGE_TIME, lastSyncTime);
        editor.commit();
    }
    
    public static long getLastSyncScheduleTime(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(KEY_LAST_SYNC_SCHEDULE_TIME, 0);
    }
    
    public static void setLastSyncScheduleTime(Context context, long lastSyncTime) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(KEY_LAST_SYNC_SCHEDULE_TIME, lastSyncTime);
        editor.commit();
    }
    
    public static String getAccount(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(KEY_ACCOUNT, null);
    }
    
    public static void setAccount(Context context, String account) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_ACCOUNT, account);
        editor.commit();
    }
    
    public static String getName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(KEY_NAME, null);
    }
    
    public static void setName(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_NAME, name);
        editor.commit();
    }
    
    public static String getStartDate(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(KEY_START_DATE, null);
    }
    
    public static void setStartDate(Context context, String date) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_START_DATE, date);
        editor.commit();
    }
    
    public static String getEndDate(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(KEY_END_DATE, null);
    }
    
    public static void setEndDate(Context context, String date) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_END_DATE, date);
        editor.commit();
    }
}
