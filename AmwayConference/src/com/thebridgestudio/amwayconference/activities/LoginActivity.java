package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.brixd.amway_meeting.R;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis.LoginCallback;
import com.thebridgestudio.amwayconference.services.DataService;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
  private static final String TAG = "LoginActivity";

  private Button mLogin;
  private EditText mName;
  private EditText mId;

  private Handler mMainThreadHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);

    mMainThreadHandler = new Handler();

    initViews();
    initListener();
  }

  private void initViews() {
    mName = (EditText) findViewById(R.id.input_name);
    mId = (EditText) findViewById(R.id.input_id);
    mLogin = (Button) findViewById(R.id.login);

    mName.setText("郑再添");
    mId.setText("A0001A");
  }

  private void initListener() {

    mLogin.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        AccountApis.loginAsync(LoginActivity.this, mId.getText()
            .toString(), mName.getText().toString(),
            new LoginCallback() {

              @Override
              public void onLoginOK(String account, String name) {
                Config.setAccount(LoginActivity.this, account);
                Config.setName(LoginActivity.this, name);

                Intent scheduleIntent = new Intent(
                    LoginActivity.this,
                    ScheduleActivity.class);
                scheduleIntent.putExtra(Intents.EXTRA_LOGIN, true);
                startActivity(scheduleIntent);

                Intent finishIntent = new Intent();
                finishIntent.setAction(Intents.ACTION_FINISH);
                sendBroadcast(finishIntent);

                LoginActivity.this.finish();

                Intent intent = new Intent();
                intent.setAction(Intents.ACTION_REGISTER_ALARMMANAGER);
                intent.setClass(LoginActivity.this, DataService.class);
                startService(intent);
              }

              @Override
              public void onLoginFailed(String errorMsg) {
                mMainThreadHandler.post(new Runnable() {

                  @Override
                  public void run() {
                    Toast.makeText(LoginActivity.this,
                        R.string.login_failed,
                        Toast.LENGTH_SHORT).show();
                  }
                });
              }
            });
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }

}
