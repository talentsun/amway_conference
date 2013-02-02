package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis.LoginCallback;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                                if (!TextUtils.isEmpty(account)) {
                                    Log.i(TAG, "account: " + account);
                                }

                                if (!TextUtils.isEmpty(name)) {
                                    Log.i(TAG, "name: " + name);
                                }
                            }

                            @Override
                            public void onLoginFailed(String errorMsg) {
                                Log.i(TAG, "error: " + errorMsg);
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
