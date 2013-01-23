package com.thebridgestudio.amwayconference.services;

import it.restrung.rest.client.ContextAwareAPIDelegate;
import it.restrung.rest.client.RestClientFactory;

import java.sql.SQLException;
import java.util.HashMap;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.cloudapis.SyncMessageResponse;
import com.thebridgestudio.amwayconference.cloudapis.SyncScheduleResponse;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Message;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.models.ScheduleDetail;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class DataService extends IntentService {
    private static final String TAG = "MessageService";
    private DatabaseHelper mDatabaseHelper = null;
    private Dao<Message, Long> mMessageDao = null;
    private Dao<Schedule, Long> mScheduleDao = null;
    
    public DataService() {
        super("message-service");
        
        try {
            mMessageDao = getHelper().getDao(Message.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            String action = intent.getAction();
            if (Intents.ACTION_SYNC_MESSAGE.equalsIgnoreCase(action)) {
                syncMessage();
            } else if (Intents.ACTION_SYNC_SCHEDULE.equalsIgnoreCase(action)) {
                syncSchedule();
            } else if (Intents.ACTION_SYNC_ALL.equalsIgnoreCase(action)) {
                syncSchedule();
                syncMessage();
            }
        }
    }

    private void syncMessage() {
        RestClientFactory.getClient().getAsync(new ContextAwareAPIDelegate<SyncMessageResponse>(this, SyncMessageResponse.class) {

            @Override
            public void onError(Throwable arg0) {
                Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(SyncMessageResponse arg0) {
                if (mMessageDao != null && arg0.getResult() == 1) {
                    for(SyncMessageResponse.Message msg : arg0.getMessages()) {
                        try {
                            if (msg.getValid() == 0) {
                                if (mMessageDao.idExists(msg.getId())) {
                                    UpdateBuilder<Message, Long> updateBuilder = mMessageDao.updateBuilder();
                                    updateBuilder.updateColumnValue("title", msg.getTitle());
                                    updateBuilder.updateColumnValue("content", msg.getContent());
                                    updateBuilder.where().idEq(msg.getId());
                                    updateBuilder.update();
                                } else {
                                    Message message = new Message(msg.getId(), msg.getTitle(), msg.getContent(), false);
                                    mMessageDao.create(message);
                                }
                            } else {
                                mMessageDao.deleteById(msg.getId());
                            }
                            
                            
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    Config.setLastSyncMessageTime(getContextProvider().getContext(), System.currentTimeMillis());
                } else {
                    Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
                }
            }
            
        }, "http://bri/message.sync?date=%s", Config.getLastSyncMessageTime(this));
    }
    
    private void syncSchedule() {
        if (TextUtils.isEmpty(Config.getAccount(this))) {
            Log.i(TAG, "no account, stop sync schedule");
            return;
        }
        
        RestClientFactory.getClient().getAsync(new ContextAwareAPIDelegate<SyncScheduleResponse>(this, SyncScheduleResponse.class) {

            @Override
            public void onError(Throwable arg0) {
                Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(SyncScheduleResponse arg0) {
                if (mScheduleDao != null && arg0.getResult() == 1) {
                    try {
                        if (arg0.getData().getNeedRefresh() == 1) {
                            mScheduleDao.executeRawNoArgs("truncate table schedules");
                            
                            HashMap<Long, Schedule> schedules = new HashMap<Long, Schedule>();
                            for (SyncScheduleResponse.Schedule schedule : arg0.getData().getSchedules()) {
                                schedules.put(schedule.getId(), new Schedule(schedule.getId(), schedule.getContent(), schedule.getDate(), schedule.getTime()));
                            }
                            
                            for (SyncScheduleResponse.ScheduleDetail detail : arg0.getData().getScheduleDetails()) {
                                if (detail.getValid() == 0) {
                                    Schedule schedule = schedules.get(detail.getSid());
                                    if (schedule != null) {
                                        schedule.getScheduleDetails().add(new ScheduleDetail(detail.getId(), schedule, detail.getContent(), detail.getTime(), detail.getFeature(), detail.getType()));
                                    }
                                }
                            }
                            
                            for (Schedule schedule : schedules.values()) {
                                mScheduleDao.create(schedule);
                            }
                        } else {
                            //TODO sync schedules
                        }
                    } catch (SQLException e) {
                        
                    }
                } else {
                    Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }, "http://bri/conference.schedule.sync?account=%s&date=%s", Config.getAccount(this), Config.getLastSyncScheduleTime(this));
    }
    
    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        
        return mDatabaseHelper;
    }

    @Override
    public void onDestroy() {
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
        super.onDestroy();
    }

}
