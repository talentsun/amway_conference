package com.thebridgestudio.amwayconference.views;

import java.util.Calendar;
import java.util.List;

import com.brixd.amway_meeting.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ScheduleDateView extends LinearLayout {
  private OnDateSelectedListener mDateSelectedListener;

  public ScheduleDateView(Context context) {
    super(context);
    initViews(context);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public ScheduleDateView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initViews(context);
  }

  public ScheduleDateView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViews(context);
  }

  public void setOnDateSelectedListener(OnDateSelectedListener dateSelectedListener) {
    mDateSelectedListener = dateSelectedListener;
  }

  private void initViews(Context context) {
    setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
    setOrientation(LinearLayout.VERTICAL);
    setGravity(Gravity.CENTER_HORIZONTAL);
  }

  public void setDates(List<Integer> dates) {
    removeAllViews();

    ImageView scheduleDateHead = new ImageView(getContext());
    scheduleDateHead.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT));
    scheduleDateHead.setImageResource(R.drawable.schedule_date_head);

    addView(scheduleDateHead);

    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    for (int i = 0; i < dates.size(); i++) {
      int date = dates.get(i);

      Button button = new Button(getContext());
      button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      button.setTextSize(getResources().getInteger(R.integer.schedule_text_size));
      button.setGravity(Gravity.CENTER);

      if (i != dates.size() - 1) {
        button.setBackgroundResource(R.drawable.schedule_date);
      } else {
        button.setBackgroundResource(R.drawable.last_schedule_date);
      }

      if (date == currentDay) {
        button.setTextColor(Color.WHITE);
        button.setText(R.string.today);
        button.setShadowLayer(1, 0, 1, Color.parseColor("#33000000"));
        button.setSelected(true);
        button.measure(0, 0);
      } else {
        button.setTextColor(Color.parseColor("#707070"));
        button.setShadowLayer(1, 0, 1, Color.parseColor("#A0FFFFFF"));
        button.setText(String.valueOf(date));
        button.setSelected(false);
        button.measure(0, 0);
      }

      final int buttonDate = date;
      button.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          if (mDateSelectedListener != null) {
            mDateSelectedListener.onDateSelected(buttonDate);
          }
        }
      });

      addView(button);
    }
  }

  public interface OnDateSelectedListener {
    void onDateSelected(int date);
  }
}
