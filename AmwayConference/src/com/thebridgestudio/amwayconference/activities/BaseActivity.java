package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.views.AnimationLayout;
import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity implements AnimationLayout.Listener {
    protected AnimationLayout mSidebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initSidebar() {
        mSidebar = (AnimationLayout) findViewById(R.id.animation_layout);
        mSidebar.setListener(this);
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
}
