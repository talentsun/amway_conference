package com.thebridgestudio.amwayconference.test;

import java.sql.SQLException;

import junit.framework.Assert;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis.LoginCallback;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis.LogoutCallback;
import com.thebridgestudio.amwayconference.cloudapis.SyncMessageResponse.Message;
import com.thebridgestudio.amwayconference.cloudapis.SyncScheduleResponse.Schedule;
import com.thebridgestudio.amwayconference.cloudapis.SyncScheduleResponse.ScheduleDetail;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;

import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.util.Log;

public class AccountApisTestCase extends AndroidTestCase {
    private static final String TAG = "AccountApisTestCase";
    
    public AccountApisTestCase() {
        // TODO Auto-generated constructor stub
    }
    
    public void testLoginAsync() {
        AccountApis.loginAsync(mContext, "1", "test", new LoginCallback() {
            
            @Override
            public void onLoginOK(String account, String name) {
                if (!TextUtils.isEmpty(account)) {
                    Log.i(TAG, "account: " + account);
                }
                
                if (!TextUtils.isEmpty(name)) {
                    Log.i(TAG, "name: " + name);
                }
                
                Assert.assertEquals("test", Config.getName(mContext));
                Assert.assertEquals("1", Config.getAccount(mContext));
            }
            
            @Override
            public void onLoginFailed(String errorMsg) {
                Log.i(TAG, "error: " + errorMsg);
                Assert.fail();
            }
        });
    }
    
    public void testLogout() {
        AccountApis.logout(mContext, "test", new LogoutCallback() {
            
            @Override
            public void onLogoutOK() {
                Assert.assertTrue(true);
            }
            
            @Override
            public void onLogoutFailed() {
                Assert.fail();
            }
        });
    }
}
