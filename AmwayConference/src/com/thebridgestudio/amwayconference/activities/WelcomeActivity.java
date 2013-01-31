package com.thebridgestudio.amwayconference.activities;

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

public class WelcomeActivity extends Activity {
    private static final String TAG = "WelcomeActivity";
    
    // Views
    private Button mScanEntry;
    private Button mMessageCenter;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        initViews();
        initListener();
    }

    private void initViews() {
        mScanEntry = (Button) findViewById(R.id.scan_entry);
        mMessageCenter = (Button) findViewById(R.id.message_center);
        mLogin = (Button) findViewById(R.id.login);
    }

    private void initListener() {
        mScanEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(getApplicationContext(),
                        CaptureActivity.class);
                startActivity(scanIntent);
            }
        });
        
        mMessageCenter.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(WelcomeActivity.this, MessageActivity.class);
//                WelcomeActivity.this.startActivity(intent);
                
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, DataService.class);
                intent.setAction(Intents.ACTION_SYNC_SCHEDULE);
                WelcomeActivity.this.startService(intent);
            }
        });
        
        mLogin.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AccountApis.loginAsync(WelcomeActivity.this, "1", "test", new LoginCallback() {
                    
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
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

}
