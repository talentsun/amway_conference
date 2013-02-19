package com.thebridgestudio.amwayconference.cloudapis;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.table.TableUtils;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.cloudapis.SyncMessageResponse.Message;
import com.thebridgestudio.amwayconference.cloudapis.SyncScheduleResponse.Schedule;
import com.thebridgestudio.amwayconference.cloudapis.SyncScheduleResponse.ScheduleDetail;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;

import it.restrung.rest.client.ContextAwareAPIDelegate;
import it.restrung.rest.client.RestClientFactory;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class AccountApis {
    private static final String TAG = "AccountApis";
    
    
    public static void loginAsync(Context context, String account, String name, final LoginCallback callback) {
        if (TextUtils.isEmpty(account)) {
            Log.e(TAG, "account is null, can not login!");
            return;
        }
        
        String requestUrl = String.format("http://a.brixd.com/conference/remote?method=user.login&account=%s", account);
        if (!TextUtils.isEmpty(name)) {
            requestUrl = requestUrl + "&name=" + name;
        }
        
        RestClientFactory.getClient().getAsync(new ContextAwareAPIDelegate<LoginResponse>(context, LoginResponse.class) {

            @Override
            public void onError(Throwable arg0) {
                if (callback != null) {
                    callback.onLoginFailed(getContextProvider().getContext().getResources().getString(R.string.login_failed_caused_by_network));
                }
                Log.e(TAG, "login failed caused by network");
                arg0.printStackTrace();
            }

            @Override
            public void onResults(LoginResponse arg0) {
                if (arg0.getResult() == 1) {
                    if (arg0.getData() != null) {
                        Config.setAccount(getContextProvider().getContext(), arg0.getData().getAccount());
                        if (!TextUtils.isEmpty(arg0.getData().getName())) {
                            Config.setName(getContextProvider().getContext(), arg0.getData().getName());
                        }
                        
                        if (callback != null) {
                            callback.onLoginOK(arg0.getData().getAccount(), arg0.getData().getName());
                        }
                    } else {
                        Log.e(TAG, "login failed caused by response data");
                        if (callback != null) {
                            callback.onLoginFailed(getContextProvider().getContext().getResources().getString(R.string.login_failed));
                        }
                    }
                } else if (arg0.getResult() == 0) {
                    Log.i(TAG, "login failed");
                    if (callback != null) {
                        callback.onLoginFailed(arg0.getErrorMsg());
                    }
                }
            }
        }, requestUrl);
    }
    
    public interface LoginCallback {
        void onLoginOK(String account, String name);
        void onLoginFailed(String errorMsg);
    }
    
    public static void logout(Context context, String account, LogoutCallback callback) {
        DatabaseHelper databaseHelper = (DatabaseHelper) OpenHelperManager.getHelper(context, DatabaseHelper.class);

        try {
            TableUtils.clearTable(databaseHelper.getConnectionSource(), Message.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), Schedule.class);
            TableUtils.clearTable(databaseHelper.getConnectionSource(), ScheduleDetail.class);
        } catch (SQLException e) {
            Log.e(TAG, "clear data failed");
            e.printStackTrace();
        }

        OpenHelperManager.releaseHelper();
    }
    
    public interface LogoutCallback {
        void onLogoutOK();
        void onLogoutFailed();
    }
}
