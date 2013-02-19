package com.thebridgestudio.amwayconference.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.models.ScheduleDetail;

public class ScheduleView extends LinearLayout {
    private static final String TAG = "ScheduleView";
    private static final int[] ICONS = new int[] {
        0,
        R.drawable.icon_flight,
        R.drawable.icon_ontheroad,
        R.drawable.icon_bike,
        R.drawable.icon_meal,
        R.drawable.icon_dinner_party,
        R.drawable.icon_hotel,
        R.drawable.icon_play,
        R.drawable.icon_shopping
    };
    
    private LayoutInflater mInflater;
    
    public ScheduleView(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setSchedule(Schedule schedule) {
        removeAllViews();
        
        mInflater.inflate(R.layout.schedule_item, this);
        
        TextView scheduleTextView = (TextView) findViewById(R.id.schedule_content);
        if (!TextUtils.isEmpty(schedule.getContent())) {
            scheduleTextView.setText(schedule.getContent());
        }
        
        if (!TextUtils.isEmpty(schedule.getTips())) {
            LinearLayout warmtipsView = (LinearLayout) findViewById(R.id.schedule_tips);
            warmtipsView.setVisibility(VISIBLE);
            
            String[] tips = schedule.getTips().split("\\|");
            for (String tip : tips) {
                warmtipsView.addView(genTip(tip));
            }
        }
        
        LinearLayout detailsView = (LinearLayout) findViewById(R.id.schedule_details);
        detailsView.removeAllViews();
        for (ScheduleDetail detail : schedule.getScheduleDetails()) {
            if (detail.getType() != ScheduleDetail.FLIGHT) {
                LinearLayout featureView = (LinearLayout) mInflater.inflate(R.layout.schedule_item_feature, null);
                
                ImageView typeImageView = (ImageView) featureView.findViewById(R.id.schedule_detail_type);
                typeImageView.setImageResource(ICONS[detail.getType()]);
                
                TextView timeTextView = (TextView) featureView.findViewById(R.id.schedule_detail_time);
                timeTextView.setText(detail.getTime());
                
                TextView contentTextView = (TextView) featureView.findViewById(R.id.schedule_detail_content);
                contentTextView.setText(detail.getContent());

                TextView featureTextView = (TextView) featureView.findViewById(R.id.schedule_detail_feature);
                if (!TextUtils.isEmpty(detail.getFeature())) {
                    featureTextView.setText(detail.getFeature());
                } else {
                    featureTextView.setVisibility(View.GONE);
                }
                
                detailsView.addView(featureView);
            } else {
                LinearLayout flightView = (LinearLayout) mInflater.inflate(R.layout.schedule_item_flight, null);
                
                TextView timeTextView = (TextView) flightView.findViewById(R.id.schedule_flight_time);
                timeTextView.setText(detail.getTime());
                
                TextView contentTextView = (TextView) flightView.findViewById(R.id.schedule_flight_content);
                contentTextView.setText(detail.getContent());

                TextView featureTextView = (TextView) flightView.findViewById(R.id.schedule_flight_feature);
                if (!TextUtils.isEmpty(detail.getFeature())) {
                    featureTextView.setText(detail.getFeature());
                } else {
                    featureTextView.setVisibility(View.GONE);
                }
                
                detailsView.addView(flightView);
            }
        }
    }
    
    public LinearLayout genTip(String tip) {
        LinearLayout tipWrapper = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = getContext().getResources().getDimensionPixelSize(R.dimen.tip_margin_left);
        tipWrapper.setLayoutParams(layoutParams);
        tipWrapper.setOrientation(LinearLayout.HORIZONTAL);
        
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(layoutParams1);
        imageView.setImageResource(R.drawable.point);
        tipWrapper.addView(imageView);
        
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams2.leftMargin = getContext().getResources().getDimensionPixelSize(R.dimen.small_margin);
        textView.setLayoutParams(layoutParams2);
        textView.setText(tip);
        tipWrapper.addView(textView);
        
        return tipWrapper;
    }
}
