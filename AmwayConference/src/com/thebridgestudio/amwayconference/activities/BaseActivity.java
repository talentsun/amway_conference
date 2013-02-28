package com.thebridgestudio.amwayconference.activities;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.brixd.amway_meeting.R;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Message;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.views.AnimationLayout;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivity extends CustomActivity implements
    AnimationLayout.Listener, OnClickListener {
  private final static String TAG = "BaseActivity";
  protected static final float SLIDE_DISTANCE_X = 80;
  protected static final float SLIDE_DISTANCE_Y = 40;
  protected AnimationLayout mSidebar;
  protected ImageView mTag;
  protected DatabaseHelper mDatabaseHelper = null;
  protected Dao<Message, Long> mMessageDao = null;
  protected Dao<Schedule, Long> mScheduleDao = null;

  private NewMessageReceiver mNewMessageReceiver;
  private GestureDetector gestureDetector;
  private BlockingQueue<Runnable> finishQueue;
  private Handler handler = new Handler();
  
  private boolean mFlingEnabled = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MobclickAgent.onError(this);
    UmengUpdateAgent.update(this);

    initMessageDao();
    initScheduleDao();

    mNewMessageReceiver = new NewMessageReceiver();
    IntentFilter newMessageIntentFilter = new IntentFilter();
    newMessageIntentFilter.addAction(Intents.ACTION_NEW_MESSAGE);
    registerReceiver(mNewMessageReceiver, newMessageIntentFilter);
    finishQueue = new LinkedBlockingQueue<Runnable>();

    gestureDetector = new GestureDetector(this,
        new GestureDetector.SimpleOnGestureListener() {

          @Override
          public boolean onScroll(MotionEvent e1, MotionEvent e2,
              float distanceX, float distanceY) {
            if (!mFlingEnabled) {
              return false;
            }
            
            if (e2.getX() - e1.getX() > SLIDE_DISTANCE_X
                && Math.abs(e2.getY() - e1.getY()) < SLIDE_DISTANCE_Y) {
              mSidebar.openSidebar();
              return true;
            } else if (e1.getX() - e2.getX() > SLIDE_DISTANCE_X 
                && Math.abs(e2.getY() - e1.getY()) < SLIDE_DISTANCE_Y) {
              mSidebar.closeSidebar();
              return true;
            }
            
            return false;
          }

          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2,
              float velocityX, float velocityY) {
            if (!mFlingEnabled) {
              return false;
            }
            
            if (e2.getX() - e1.getX() > SLIDE_DISTANCE_X
                && Math.abs(e2.getY() - e1.getY()) < SLIDE_DISTANCE_Y) {
              mSidebar.openSidebar();
              return true;
            } else if (e1.getX() - e2.getX() > SLIDE_DISTANCE_X
                && Math.abs(e2.getY() - e1.getY()) < SLIDE_DISTANCE_Y) {
              mSidebar.closeSidebar();
              return true;
            }
            
            return false;
          }

          @Override
          public boolean onDown(MotionEvent e) {
            return false;
          }
        });

  }
  
  protected void disableFling() {
    mFlingEnabled = false;
  }
  
  protected void enableFling() {
    mFlingEnabled = true;
  }

  @Override
  protected void onDestroy() {
    if (mDatabaseHelper != null) {
      OpenHelperManager.releaseHelper();
      mDatabaseHelper = null;
    }

    if (mNewMessageReceiver != null) {
      unregisterReceiver(mNewMessageReceiver);
    }

    super.onDestroy();
  }

  protected void initMessageDao() {
    try {
      mMessageDao = getHelper().getDao(Message.class);
    } catch (SQLException e) {
      e.printStackTrace();
      Log.e(TAG, "load message dao failed");
    }
  }

  protected void initScheduleDao() {
    try {
      mScheduleDao = getHelper().getDao(Schedule.class);
    } catch (SQLException e) {
      e.printStackTrace();
      Log.e(TAG, "load message dao failed");
    }
  }

  private boolean initNewMessage() {
    boolean hasNewMessage = false;

    try {
      List<Message> messages = mMessageDao.queryBuilder().where().eq("read", false).query();
      TextView newMessageText = (TextView) findViewById(R.id.new_message);
      if (messages.size() > 0) {
        newMessageText.setText(String.format(
            getResources().getString(R.string.sidebar_new_message), messages.size()));
        newMessageText.setVisibility(View.VISIBLE);

        hasNewMessage = true;
      } else {
        newMessageText.setVisibility(View.GONE);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return hasNewMessage;
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mSidebar.isOpening()) {
      mSidebar.clearAnimation();
      mSidebar.closeSidebar();
    }

    MobclickAgent.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    MobclickAgent.onPause(this);
  }

  @Override
  protected void onStart() {
    onSyncMessage(initNewMessage());
    super.onStart();
  }

  protected void initSidebar() {
    mSidebar = (AnimationLayout) findViewById(R.id.animation_layout);
    mSidebar.setListener(this);

    findViewById(R.id.schedule_item).setOnClickListener(this);
    findViewById(R.id.message_item).setOnClickListener(this);
    findViewById(R.id.survey_item).setOnClickListener(this);
    findViewById(R.id.map_item).setOnClickListener(this);
    findViewById(R.id.scenery_item).setOnClickListener(this);

    findViewById(R.id.entry_schedule).setOnClickListener(this);
    findViewById(R.id.entry_message).setOnClickListener(this);
    findViewById(R.id.entry_survey).setOnClickListener(this);
    findViewById(R.id.entry_map).setOnClickListener(this);
    findViewById(R.id.entry_scenery).setOnClickListener(this);

    findViewById(R.id.exit_login).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(BaseActivity.this);
        AlertDialog logoutAlert =
            logoutAlertBuilder.setMessage(R.string.confirm_logout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                  }
                }).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    AccountApis.logout(BaseActivity.this, Config.getAccount(BaseActivity.this),
                        new AccountApis.LogoutCallback() {

                          @Override
                          public void onLogoutOK() {
                            Intent welcomeIntent =
                                new Intent(getBaseContext(), WelcomeActivity.class);
                            startActivity(welcomeIntent);
                            finish();
                          }

                          @Override
                          public void onLogoutFailed() {
                            // TODO Auto-generated method stub

                          }
                        });
                  }
                }).create();
        logoutAlert.show();
      }
    });
    mTag = (ImageView) findViewById(R.id.tag);
    mTag.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        mSidebar.toggleSidebar();
      }
    });

  }

  protected DatabaseHelper getHelper() {
    if (mDatabaseHelper == null) {
      mDatabaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
    }

    return mDatabaseHelper;
  }

  public class NewMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      onSyncMessage(initNewMessage());
    }
  }

  protected void onSyncMessage(boolean hasNewMessage) {}

  @Override
  public void onSidebarOpened() {

  }

  @Override
  public void onSidebarClosed() {
    if (finishQueue != null && finishQueue.size() > 0) {
      try {
        while (!finishQueue.isEmpty()) {
          handler.post(finishQueue.take());
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public boolean onContentTouchedWhenOpening() {
    mSidebar.closeSidebar();
    return true;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.schedule_item:
      case R.id.entry_schedule:
        if (this instanceof ScheduleActivity) {
          mSidebar.closeSidebar();
        } else {
          mSidebar.closeSidebar();
          Intent scheduleIntent = new Intent();
          scheduleIntent.setClass(getBaseContext(), ScheduleActivity.class);
          startActivity(scheduleIntent);
          mSidebar.closeSidebar();
          finish();
        }
        break;
      case R.id.message_item:
      case R.id.entry_message:
        if (this instanceof MessageActivity) {
          mSidebar.closeSidebar();
        } else {
          mSidebar.closeSidebar();

          finishQueue.add(new Runnable() {

            @Override
            public void run() {
              Intent messageIntent = new Intent();
              messageIntent.setClass(getBaseContext(), MessageActivity.class);
              startActivity(messageIntent);
              finish();
            }
          });

        }
        break;
      case R.id.survey_item:
      case R.id.entry_survey:
        if (this instanceof SurveyActivity) {
          mSidebar.closeSidebar();
        } else {
          mSidebar.closeSidebar();

          finishQueue.add(new Runnable() {

            @Override
            public void run() {
              Intent messageIntent = new Intent();
              messageIntent.setClass(getBaseContext(), SurveyActivity.class);
              startActivity(messageIntent);
              finish();
            }
          });

        }
        break;
      case R.id.map_item:
      case R.id.entry_map:
        if (this instanceof MapActivity) {
          mSidebar.closeSidebar();
        } else {
          mSidebar.closeSidebar();
          Intent messageIntent = new Intent();
          messageIntent.setClass(getBaseContext(), MapActivity.class);
          startActivity(messageIntent);
          finish();
        }
        break;
      case R.id.scenery_item:
      case R.id.entry_scenery:
        if (this instanceof SceneryActivity) {
          mSidebar.closeSidebar();
        } else {
          mSidebar.closeSidebar();

          finishQueue.add(new Runnable() {

            @Override
            public void run() {
              Intent messageIntent = new Intent();
              messageIntent.setClass(getBaseContext(), SceneryActivity.class);
              startActivity(messageIntent);
              finish();
            }
          });

        }
        break;
      default:
        break;
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean consumed = gestureDetector.onTouchEvent(ev);
    if (!consumed) {
      return super.dispatchTouchEvent(ev);
    } else {
      return true;
    }
  }

}
