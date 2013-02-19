package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class IndexActivity extends BaseActivity {
    private ImageView mTagIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        initSidebar();

        mTagIndex = (ImageView) findViewById(R.id.tag_index);
        mTagIndex.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSidebar.toggleSidebar();
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
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mSidebar.isOpening()) {
            mSidebar.closeSidebar();
        } else {
            finish();
        }
    }

}
