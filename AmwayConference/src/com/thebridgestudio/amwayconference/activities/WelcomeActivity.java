package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {
    // Views
    private Button scanEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        initViews();
        initListener();
    }

    private void initViews() {
        scanEntry = (Button) findViewById(R.id.scan_entry);
    }

    private void initListener() {
        scanEntry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(getApplicationContext(),
                        CaptureActivity.class);
                startActivity(scanIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        Intent intent = new Intent();
        intent.setAction(Intents.ACTION_SYNC_MESSAGE);
        startService(intent);

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
