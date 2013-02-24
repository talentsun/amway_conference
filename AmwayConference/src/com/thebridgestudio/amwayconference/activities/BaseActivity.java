package com.thebridgestudio.amwayconference.activities;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Message;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.views.AnimationLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivity extends FragmentActivity implements
    AnimationLayout.Listener, OnClickListener {
  private final static String TAG = "BaseActivity";
  protected AnimationLayout mSidebar;
  protected ImageView mTag;
  protected DatabaseHelper mDatabaseHelper = null;
  protected Dao<Message, Long> mMessageDao = null;
  protected Dao<Schedule, Long> mScheduleDao = null;

  private NewMessageReceiver mNewMessageReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    initMessageDao();
    initScheduleDao();

    mNewMessageReceiver = new NewMessageReceiver();
    IntentFilter newMessageIntentFilter = new IntentFilter();
    newMessageIntentFilter.addAction(Intents.ACTION_NEW_MESSAGE);
    registerReceiver(mNewMessageReceiver, newMessageIntentFilter);
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
        AccountApis.logout(BaseActivity.this, Config.getAccount(BaseActivity.this),
            new AccountApis.LogoutCallback() {

              @Override
              public void onLogoutOK() {
                Intent welcomeIntent = new Intent(getBaseContext(), WelcomeActivity.class);
                startActivity(welcomeIntent);
                finish();
              }

              @Override
              public void onLogoutFailed() {
                // TODO Auto-generated method stub

              }
            });
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
  public void onSidebarOpened() {}

  @Override
  public void onSidebarClosed() {}

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
          Intent scheduleIntent = new Intent();
          scheduleIntent.setClass(getBaseContext(), ScheduleActivity.class);
          startActivity(scheduleIntent);
        }
        break;
      case R.id.message_item:
      case R.id.entry_message:
        if (this instanceof MessageActivity) {
          mSidebar.closeSidebar();
        } else {
          Intent messageIntent = new Intent();
          messageIntent.setClass(getBaseContext(), MessageActivity.class);
          startActivity(messageIntent);
        }
        break;
      case R.id.survey_item:
      case R.id.entry_survey:
        if (this instanceof SurveyActivity) {
          mSidebar.closeSidebar();
        } else {
          Intent messageIntent = new Intent();
          messageIntent.setClass(getBaseContext(), SurveyActivity.class);
          startActivity(messageIntent);
        }
        break;
      case R.id.map_item:
      case R.id.entry_map:
        if (this instanceof MapActivity) {
          mSidebar.closeSidebar();
        } else {
          Intent messageIntent = new Intent();
          messageIntent.setClass(getBaseContext(), MapActivity.class);
          startActivity(messageIntent);
        }
        break;
      case R.id.scenery_item:
      case R.id.entry_scenery:
        if (this instanceof SceneryActivity) {
          mSidebar.closeSidebar();
        } else {
          Intent messageIntent = new Intent();
          messageIntent.setClass(getBaseContext(), SceneryActivity.class);
          startActivity(messageIntent);
        }
        break;
      default:
        break;
    }
  }
}
