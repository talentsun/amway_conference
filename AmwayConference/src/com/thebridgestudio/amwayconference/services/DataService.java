package com.thebridgestudio.amwayconference.services;

import it.restrung.rest.client.ContextAwareAPIDelegate;
import it.restrung.rest.client.RestClientFactory;
import it.restrung.rest.exceptions.APIException;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private Dao<ScheduleDetail, Long> mScheduleDetailDao = null;
    
    public DataService() {
        super("message-service");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            mMessageDao = getHelper().getDao(Message.class);
            mScheduleDao = getHelper().getDao(Schedule.class);
            mScheduleDetailDao = getHelper().getDao(ScheduleDetail.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            String action = intent.getAction();
            if (Intents.ACTION_SYNC_MESSAGE.equalsIgnoreCase(action)) {
                try {
                    syncMessage();
                } catch (APIException e) {
                    Log.e(TAG, "sync message failed");
                    e.printStackTrace();
                }
            } else if (Intents.ACTION_SYNC_SCHEDULE.equalsIgnoreCase(action)) {
                try {
                    syncSchedule();
                    syncScheduleDate();
                } catch (APIException e) {
                    Log.e(TAG, "sync schedule failed");
                    e.printStackTrace();
                }
            } else if (Intents.ACTION_SYNC_ALL.equalsIgnoreCase(action)) {
                try {
                    syncSchedule();
                    syncScheduleDate();
                } catch (APIException e) {
                    Log.e(TAG, "sync schedule failed");
                    e.printStackTrace();
                }
                
                try {
                    syncMessage();
                } catch (APIException e) {
                    Log.e(TAG, "sync message failed");
                    e.printStackTrace();
                }
            }
        }
    }

    private void syncMessage() throws APIException {
        SyncMessageResponse response = RestClientFactory.getClient().get(new ContextAwareAPIDelegate<SyncMessageResponse>(this, SyncMessageResponse.class) {

            @Override
            public void onError(Throwable arg0) {
                Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(SyncMessageResponse arg0) {
            }
            
        }, String.format("http://a.brixd.com/conference/remote?method=message.sync&account=%s&date=%s", Config.getAccount(this), Config.getLastSyncMessageTime(this)), 2 * 1000);
        
        if (mMessageDao != null && response != null
                && response.getMessages() != null && response.getResult() == 1) {
            for(SyncMessageResponse.Message msg : response.getMessages()) {
                try {
                    if (msg.getValid()) {
                        if (mMessageDao.idExists(msg.getId())) {
                            UpdateBuilder<Message, Long> updateBuilder = mMessageDao.updateBuilder();
                            updateBuilder.updateColumnValue("content", msg.getContent());
                            updateBuilder.updateColumnValue("date", msg.getDate());
                            updateBuilder.where().idEq(msg.getId());
                            updateBuilder.update();
                            
                            Log.i(TAG, "update message #" + msg.getId());
                        } else {
                            //for test
                            mMessageDao.create(new Message(msg.getId(), msg.getContent(), false, msg.getDate()));
                            Log.i(TAG, "create message #" + msg.getId());
                        }
                    } else {
                        mMessageDao.deleteById(msg.getId());
                        Log.i(TAG, "delete message #" + msg.getId());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "sync message #" + msg.getId() + " failed");
                }
            }

            Config.setLastSyncMessageTime(DataService.this, System.currentTimeMillis());
        } else {
            Toast.makeText(DataService.this, R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void syncSchedule() throws APIException {
        if (TextUtils.isEmpty(Config.getAccount(this))) {
            Log.i(TAG, "no account, stop sync schedule");
            return;
        }
        
        SyncScheduleResponse response = RestClientFactory.getClient().get(new ContextAwareAPIDelegate<SyncScheduleResponse>(this, SyncScheduleResponse.class) {

            @Override
            public void onError(Throwable arg0) {
                Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(SyncScheduleResponse arg0) {
            }
        }, String.format("http://a.brixd.com/conference/remote?method=schedule.sync&account=%s&date=%s", Config.getAccount(this), Config.getLastSyncScheduleTime(this)), 2 * 1000);
        
        if (mScheduleDao != null && response != null
                && response.getData() != null && response.getResult() == 1) {
            if (response.getData().getNeedRefresh() == 1) {
                try {
                    mScheduleDao.executeRawNoArgs("truncate table schedules");
                    Log.i(TAG, "empty schedules and schedule details");
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "empty schedules failed!");
                    return;
                }

                if (response.getData().getSchedules() != null) {
                    for (SyncScheduleResponse.Schedule schedule : response.getData().getSchedules()) {
                        if (schedule.getValid()) {
                            try {
                                mScheduleDao.create(new Schedule(schedule.getId(), schedule.getContent(), schedule.getDate(), schedule.getTime(),schedule.getTips()));
                                Log.i(TAG, "create schedule #" + schedule.getId());
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Log.e(TAG, "create schedule #" + schedule.getId() + " failed");
                            }
                        } else {
                            Log.i(TAG, "ignore schedule #" + schedule.getId() + " because invalid");
                        }
                    }
                }
                
                if (response.getData().getScheduleDetails() != null) {
                    for (SyncScheduleResponse.ScheduleDetail detail : response.getData().getScheduleDetails()) {
                        if (detail.getValid()) {
                            try {
                                if (mScheduleDao.idExists(detail.getSid())) {
                                    mScheduleDetailDao.create(new ScheduleDetail(detail.getId(), mScheduleDao.queryForId(detail.getSid()), detail.getContent(), detail.getTime(), detail.getFeature(), detail.getType()));
                                    Log.i(TAG, "create schedule detail #" + detail.getId());
                                } else {
                                    Log.w(TAG, "ignore schedule detail #" + detail.getId() + " because schedule #" + detail.getSid() + " not exist");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Log.e(TAG, "sync schedule detail #" + detail.getId() + " failed");
                            }
                        } else {
                            Log.w(TAG, "ignore schedule detail #" + detail.getId() + " because invalid");
                        }
                    }
                }
            } else {
                if (response.getData().getSchedules() != null) {
                    for (SyncScheduleResponse.Schedule schedule : response.getData().getSchedules()) {
                        try {
                            if (schedule.getValid()) {
                                if (mScheduleDao.idExists(schedule.getId())) {
                                    UpdateBuilder<Schedule, Long> updateBuilder = mScheduleDao.updateBuilder();
                                    updateBuilder.updateColumnValue("date", schedule.getDate());
                                    updateBuilder.updateColumnValue("time", schedule.getTime());
                                    updateBuilder.updateColumnValue("content", schedule.getContent());
                                    updateBuilder.updateColumnValue("tips", schedule.getTips());
                                    updateBuilder.where().idEq(schedule.getId());
                                    updateBuilder.update();
                                    
                                    Log.i(TAG, "update schedule #" + schedule.getId());
                                } else {
                                    mScheduleDao.create(new Schedule(schedule.getId(), schedule.getContent(), schedule.getDate(), schedule.getTime(), schedule.getTips()));
                                    Log.i(TAG, "create schedule #" + schedule.getId());
                                }
                            } else if (!schedule.getValid()) {
                                mScheduleDao.deleteById(schedule.getId());
                                Log.i(TAG, "delete schedule #" + schedule.getId());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.e(TAG, "sync schedule #" + schedule.getId() + " failed");
                        }
                    }
                }

                if (response.getData().getScheduleDetails() != null) {
                    for (SyncScheduleResponse.ScheduleDetail detail : response.getData().getScheduleDetails()) {
                        try {
                            if (detail.getValid()) {
                                if (mScheduleDao.idExists(detail.getSid())) {
                                    if (mScheduleDetailDao.idExists(detail.getId())) {
                                        UpdateBuilder<ScheduleDetail, Long> updateBuilder = mScheduleDetailDao.updateBuilder();
                                        updateBuilder.updateColumnValue("schedule_id", detail.getSid());
                                        updateBuilder.updateColumnValue("time", detail.getTime());
                                        updateBuilder.updateColumnValue("content", detail.getContent());
                                        updateBuilder.updateColumnValue("feature", detail.getFeature());
                                        updateBuilder.updateColumnValue("type", detail.getType());
                                        updateBuilder.where().idEq(detail.getId());
                                        updateBuilder.update();

                                        Log.i(TAG, "update schedule detail #" + detail.getId());
                                    } else {
                                        mScheduleDetailDao.create(new ScheduleDetail(detail.getId(), mScheduleDao.queryForId(detail.getSid()), detail.getContent(), detail.getTime(), detail.getFeature(), detail.getType()));
                                        Log.i(TAG, "create schedule detail #" + detail.getId());
                                    }
                                } else {
                                    Log.w(TAG, "ignore schedule detail #" + detail.getId() + " because no schedule #" + detail.getSid());
                                }
                            } else {
                                mScheduleDetailDao.deleteById(detail.getId());
                                Log.i(TAG, "delete schedule detail #" + detail.getId());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.e(TAG, "sync schedule detail #" + detail.getId() + " failed");
                        }
                    }
                }
            }
            
            Config.setLastSyncScheduleTime(DataService.this, System.currentTimeMillis());
        } else {
            //FIXME when send hide message to this thread, it is already a dead thread
            Toast.makeText(DataService.this, R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void syncScheduleDate() {
        if (mScheduleDao != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                
                Schedule startSchedule = mScheduleDao.queryBuilder().selectColumns("date").orderBy("date", true).queryForFirst();
                if (startSchedule != null) {
                    calendar.setTimeInMillis(startSchedule.getDate());
                    Config.setStartDate(this, new SimpleDateFormat("MM.dd").format(calendar.getTime()));
                }
                
                Schedule endSchedule = mScheduleDao.queryBuilder().selectColumns("date").orderBy("date", false).queryForFirst();
                if (endSchedule != null) {
                    calendar.setTimeInMillis(endSchedule.getDate());
                    Config.setEndDate(this, new SimpleDateFormat("MM.dd").format(calendar.getTime()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "sync schedule date failed");
            }
        }
    }

    private DatabaseHelper getHelper() {
        Log.i(TAG, "get helper");
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(DataService.this, DatabaseHelper.class);
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
