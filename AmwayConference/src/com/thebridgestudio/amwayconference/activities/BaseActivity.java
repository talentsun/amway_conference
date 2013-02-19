package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.views.AnimationLayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BaseActivity extends FragmentActivity implements
        AnimationLayout.Listener, OnClickListener {
    protected AnimationLayout mSidebar;
    protected ImageView mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initSidebar() {
        mSidebar = (AnimationLayout) findViewById(R.id.animation_layout);
        mSidebar.setListener(this);

        findViewById(R.id.schedule_item).setOnClickListener(this);
        findViewById(R.id.message_item).setOnClickListener(this);
        findViewById(R.id.survey_item).setOnClickListener(this);
        findViewById(R.id.map_item).setOnClickListener(this);
        findViewById(R.id.scenery_item).setOnClickListener(this);

        mTag = (ImageView) findViewById(R.id.tag);
        mTag.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mSidebar.toggleSidebar();
            }
        });
    }

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
            Intent scheduleIntent = new Intent();
            scheduleIntent.setClass(getBaseContext(), ScheduleActivity.class);
            startActivity(scheduleIntent);
            break;
        case R.id.message_item:
            Intent messageIntent = new Intent();
            messageIntent.setClass(getBaseContext(), MessageActivity.class);
            startActivity(messageIntent);
            break;
        case R.id.survey_item:
            break;
        case R.id.map_item:
            break;
        case R.id.scenery_item:
            break;
        default:
            break;
        }
    }
}
