package com.thebridgestudio.amwayconference.services;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Message;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DataService extends IntentService {
    private static final String TAG = "MessageService";
    private DatabaseHelper databaseHelper = null;
    
    public DataService() {
        super("message-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            String action = intent.getAction();
            if (Intents.ACTION_SYNC_MESSAGE.equalsIgnoreCase(action)) {
                //sync server
                
                try {
                    Dao<Message, Integer> dao = getHelper().getDao(Message.class);
                    Message message = new Message("test", System.currentTimeMillis(), true);
                    dao.create(message);
                    
                    QueryBuilder<Message, Integer> queryBuilder = dao.queryBuilder();
                    List<Message> messages = queryBuilder.where().eq("content", "test").query();
                    Log.d("MessageService", "message count:" + messages.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        
        return databaseHelper;
    }

    @Override
    public void onDestroy() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
        super.onDestroy();
    }

}
