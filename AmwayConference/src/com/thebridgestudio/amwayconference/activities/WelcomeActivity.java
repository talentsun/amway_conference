package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.services.DataService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {
    // Views
    private Button mScanEntry;
    private Button mMessageCenter;

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
