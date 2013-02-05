package com.thebridgestudio.amwayconference.activities;

import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.views.ScheduleDateView;

import android.app.Activity;
import android.os.Bundle;

public class ScheduleActivity extends Activity {
    private ScheduleDateView mScheduleDateView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        
        mScheduleDateView = (ScheduleDateView) findViewById(R.id.schedule_date_view);
        mScheduleDateView.setDates(new int[] {6,7,8,9,10});
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

}
