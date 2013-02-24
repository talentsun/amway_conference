package com.thebridgestudio.amwayconference.services;

import it.restrung.rest.client.ContextAwareAPIDelegate;
import it.restrung.rest.client.RestClientFactory;
import it.restrung.rest.exceptions.APIException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.activities.MessageActivity;
import com.thebridgestudio.amwayconference.cloudapis.SyncMessageResponse;
import com.thebridgestudio.amwayconference.cloudapis.SyncScheduleResponse;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Message;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.models.ScheduleDetail;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class DataService extends IntentService {
  private static final String TAG = "MessageService";
  private static final int NOTIFICATION_ID = 10010;

  private DatabaseHelper mDatabaseHelper = null;
  private Dao<Message, Long> mMessageDao = null;
  private Dao<Schedule, Long> mScheduleDao = null;
  private Dao<ScheduleDetail, Long> mScheduleDetailDao = null;

  private Handler mMainThreadHandler;

  public DataService() {
    super("message-service");
    mMainThreadHandler = new Handler();
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
      } else if (Intents.ACTION_REGISTER_ALARMMANAGER.equalsIgnoreCase(action)) {
        registerAlarmManager();
      } else if (Intents.ACTION_UNREGISTER_ALARMMANAGER.equalsIgnoreCase(action)) {
        unregisterAlarmManager();
      } else if (Intents.ACTION_SYNC_MESSAGE_WITH_NOTIFICATION.equalsIgnoreCase(action)) {
        try {
          syncMessage();
          sendMessageNotification();
        } catch (APIException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void sendMessageNotification() {
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    try {
      List<Message> messages =
          mMessageDao.queryBuilder().orderBy("date", false).where().eq("read", false).query();

      if (messages.size() > 0) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
            this);
        builder.setTicker(String.format(
            getResources().getString(R.string.amway_new_message),
            messages.size()));
        builder.setContentTitle(getResources().getText(R.string.app_name));
        builder.setContentText(messages.get(0).getContent());
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentInfo("" + messages.size());
        builder.setAutoCancel(true);

        Intent messageIntent = new Intent(this, MessageActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MessageActivity.class);
        taskStackBuilder.addNextIntent(messageIntent);
        PendingIntent messagePendingIntent =
            taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(messagePendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
      }
    } catch (SQLException e) {
      Log.e(TAG, "send message notification failed");
      e.printStackTrace();
    }
  }

  private void syncMessage() throws APIException {
    if (TextUtils.isEmpty(Config.getAccount(this))) {
      Log.i(TAG, "no account, stop sync message");
      return;
    }

    SyncMessageResponse response =
        RestClientFactory.getClient().get(
            new ContextAwareAPIDelegate<SyncMessageResponse>(this, SyncMessageResponse.class) {

              @Override
              public void onError(Throwable arg0) {
                mMainThreadHandler.post(new Runnable() {

                  @Override
                  public void run() {
                    Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed,
                        Toast.LENGTH_SHORT).show();
                  }
                });
              }

              @Override
              public void onResults(SyncMessageResponse arg0) {}

            },
            String.format(
                "http://a.brixd.com/conference/remote?method=message.sync&account=%s&date=%s",
                Config.getAccount(this), Config.getLastSyncMessageTime(this)), 2 * 1000);

    boolean hasNewMessage = false;
    if (mMessageDao != null && response != null
        && response.getMessages() != null && response.getResult() == 1) {
      for (SyncMessageResponse.Message msg : response.getMessages()) {
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
              mMessageDao.create(new Message(msg.getId(), msg.getContent(), false, msg.getDate()));
              hasNewMessage = true;
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

      if (hasNewMessage) {
        Intent intent = new Intent();
        intent.setAction(Intents.ACTION_NEW_MESSAGE);
        sendBroadcast(intent);
      }

      Config.setLastSyncMessageTime(DataService.this, System.currentTimeMillis());
    } else {
      mMainThreadHandler.post(new Runnable() {

        @Override
        public void run() {
          Toast.makeText(DataService.this, R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  private void syncSchedule() throws APIException {
    if (TextUtils.isEmpty(Config.getAccount(this))) {
      Log.i(TAG, "no account, stop sync schedule");
      return;
    }

    SyncScheduleResponse response =
        RestClientFactory.getClient().get(
            new ContextAwareAPIDelegate<SyncScheduleResponse>(this, SyncScheduleResponse.class) {

              @Override
              public void onError(Throwable arg0) {
                mMainThreadHandler.post(new Runnable() {

                  @Override
                  public void run() {
                    Toast.makeText(getContextProvider().getContext(), R.string.sync_data_failed,
                        Toast.LENGTH_SHORT).show();
                  }
                });
              }

              @Override
              public void onResults(SyncScheduleResponse arg0) {}
            },
            String.format(
                "http://a.brixd.com/conference/remote?method=schedule.sync&account=%s&date=%s",
                Config.getAccount(this), Config.getLastSyncScheduleTime(this)), 2 * 1000);

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
                mScheduleDao.create(new Schedule(schedule.getId(), schedule.getContent(), schedule
                    .getDate(), schedule.getTime(), schedule.getTips()));
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
                  mScheduleDetailDao.create(new ScheduleDetail(detail.getId(), mScheduleDao
                      .queryForId(detail.getSid()), detail.getContent(), detail.getTime(), detail
                      .getFeature(), detail.getType()));
                  Log.i(TAG, "create schedule detail #" + detail.getId());
                } else {
                  Log.w(TAG, "ignore schedule detail #" + detail.getId() + " because schedule #"
                      + detail.getSid() + " not exist");
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
                  mScheduleDao.create(new Schedule(schedule.getId(), schedule.getContent(),
                      schedule.getDate(), schedule.getTime(), schedule.getTips()));
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
                    UpdateBuilder<ScheduleDetail, Long> updateBuilder =
                        mScheduleDetailDao.updateBuilder();
                    updateBuilder.updateColumnValue("schedule_id", detail.getSid());
                    updateBuilder.updateColumnValue("time", detail.getTime());
                    updateBuilder.updateColumnValue("content", detail.getContent());
                    updateBuilder.updateColumnValue("feature", detail.getFeature());
                    updateBuilder.updateColumnValue("type", detail.getType());
                    updateBuilder.where().idEq(detail.getId());
                    updateBuilder.update();

                    Log.i(TAG, "update schedule detail #" + detail.getId());
                  } else {
                    mScheduleDetailDao.create(new ScheduleDetail(detail.getId(), mScheduleDao
                        .queryForId(detail.getSid()), detail.getContent(), detail.getTime(), detail
                        .getFeature(), detail.getType()));
                    Log.i(TAG, "create schedule detail #" + detail.getId());
                  }
                } else {
                  Log.w(TAG, "ignore schedule detail #" + detail.getId() + " because no schedule #"
                      + detail.getSid());
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
      mMainThreadHandler.post(new Runnable() {

        @Override
        public void run() {
          Toast.makeText(DataService.this, R.string.sync_data_failed, Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  private void syncScheduleDate() {
    if (mScheduleDao != null) {
      try {
        Calendar calendar = Calendar.getInstance();

        Schedule startSchedule =
            mScheduleDao.queryBuilder().selectColumns("date").orderBy("date", true).queryForFirst();
        if (startSchedule != null) {
          calendar.setTimeInMillis(startSchedule.getDate());
          Config.setStartDate(this, new SimpleDateFormat("MM.dd").format(calendar.getTime()));
        }

        Schedule endSchedule =
            mScheduleDao.queryBuilder().selectColumns("date").orderBy("date", false)
                .queryForFirst();
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

  private void registerAlarmManager() {
    unregisterAlarmManager();

    Intent intent = new Intent();
    intent.setAction(Intents.ACTION_SYNC_MESSAGE_WITH_NOTIFICATION);
    intent.setClass(this, DataService.class);

    PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    if (calendar.get(Calendar.MINUTE) > 30) {
      calendar.add(Calendar.HOUR, 1);
      calendar.set(Calendar.MINUTE, 0);
    } else {
      calendar.set(Calendar.MINUTE, 30);
    }

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
        AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
  }

  private void unregisterAlarmManager() {
    Intent intent = new Intent();
    intent.setAction(Intents.ACTION_SYNC_MESSAGE_WITH_NOTIFICATION);
    intent.setClass(this, DataService.class);

    PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(pendingIntent);
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
