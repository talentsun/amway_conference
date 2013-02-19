package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis.LoginCallback;
import com.thebridgestudio.amwayconference.services.DataService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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
                                startActivity(scheduleIntent);

                                Intent syncIntent = new Intent(
                                        LoginActivity.this, DataService.class);
                                syncIntent.setAction(Intents.ACTION_SYNC_ALL);
                                startService(syncIntent);
                            }

                            @Override
                            public void onLoginFailed(String errorMsg) {
                                Toast.makeText(LoginActivity.this,
                                        R.string.login_failed,
                                        Toast.LENGTH_SHORT).show();
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
