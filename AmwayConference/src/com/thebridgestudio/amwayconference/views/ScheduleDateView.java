package com.thebridgestudio.amwayconference.views;

import java.util.Calendar;

import com.thebridgestudio.amwayconference.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ScheduleDateView extends LinearLayout {
    
    public ScheduleDateView(Context context) {
        super(context);
        initViews(context);
    }

    public ScheduleDateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    public ScheduleDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }
    
    private void initViews(Context context) {
        setLayoutParams(new LayoutParams(getContext().getResources().getDimensionPixelSize(R.dimen.schedule_date_view_width), LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
    }
    
    public void setDates(int[] dates) {
        removeAllViews();
        
        ImageView scheduleDateHead = new ImageView(getContext());
        scheduleDateHead.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        scheduleDateHead.setImageResource(R.drawable.schedule_date_head);
        
        addView(scheduleDateHead);
        
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        for (int i=0;i<dates.length;i++) {
            int date = dates[i];
            
            Button button = new Button(getContext());
            button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, getContext().getResources().getDimensionPixelSize(R.dimen.schedule_date_height)));
            button.setTextSize(14);
            
            if (i != dates.length-1) {
                button.setBackgroundResource(R.drawable.schedule_date);
            } else {
                button.setBackgroundResource(R.drawable.last_schedule_date);
            }
            
            if (date == currentDay) {
                button.setTextColor(Color.WHITE);
                button.setText(R.string.today);
                button.setSelected(true);
            } else {
                button.setTextColor(Color.parseColor("#555555"));
                button.setText(String.valueOf(date));
                button.setSelected(false);
            }
            
            addView(button);
        }
    }

}