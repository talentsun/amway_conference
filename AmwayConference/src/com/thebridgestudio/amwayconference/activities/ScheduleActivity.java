package com.thebridgestudio.amwayconference.activities;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.views.ScheduleDateView;
import com.thebridgestudio.amwayconference.views.ScheduleView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScheduleActivity extends FragmentActivity implements LoaderCallbacks<List<Schedule>> {
    private static final String TAG = "ScheduleActivity";
    
    private ScheduleDateView mScheduleDateView;
    private StickyListHeadersListView mListView;
    private DatabaseHelper mDatabaseHelper;
    private Dao<Schedule, Long> mDao;
    private ScheduleAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);
        
        mListView = (StickyListHeadersListView) findViewById(R.id.list);
        
        initHeaderView();
        
        try {
            mDao = getHelper().getDao(Schedule.class);
            mAdapter = new ScheduleAdapter(this);
            mListView.setAdapter(mAdapter);
            
            getSupportLoaderManager().initLoader(0, null, this);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "load dao failed");
        }
        
        mScheduleDateView = (ScheduleDateView) findViewById(R.id.schedule_date_view);
        List<Integer> dates = getScheduleDates();
        if (dates.size() > 0) {
            mScheduleDateView.setDates(dates);
            mScheduleDateView.setOnDateSelectedListener(new ScheduleDateView.OnDateSelectedListener() {
                
                @Override
                public void onDateSelected(int date) {
                    mListView.smoothScrollBy(computeOffset(mListView.getFirstVisiblePosition(), mAdapter.getPositionByDay(date)), 100);
                }
            });
        } else {
            mScheduleDateView.setVisibility(View.GONE);
        }
    }
    
    private int computeOffset(int fromPosition, int toPosition) {
        int toOffset = 0;
        for (int i = 0; i < toPosition; i++) {
            View listItem = mAdapter.getView(i, null, mListView);
            listItem.measure(0, 0);
            toOffset += listItem.getMeasuredHeight();
        }
        toOffset += getResources().getDimensionPixelSize(R.dimen.schedule_dateline_height) * mAdapter.getHeaderCount(toPosition);
        
        int fromOffset = 0;
        for (int i=0; i < fromPosition; i++) {
            View listItem = mAdapter.getView(i, null, mListView);
            listItem.measure(0, 0);
            fromOffset += listItem.getMeasuredHeight();
        }
        fromOffset += getResources().getDimensionPixelSize(R.dimen.schedule_dateline_height) * mAdapter.getHeaderCount(fromPosition);
        fromOffset -= mListView.getChildAt(0).getTop();
        
        return toOffset - fromOffset;
    }
    
    private List<Integer> getScheduleDates() {
        List<Integer> dates = new ArrayList<Integer>();
        Calendar calendar = Calendar.getInstance();
        
        if (mDao != null) {
            try {
                List<Schedule> schedules = mDao.queryBuilder().selectColumns("date").orderBy("date", true).query();
                for (Schedule schedule : schedules) {
                    calendar.setTimeInMillis(schedule.getDate());
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    if (!dates.contains(dayOfMonth)) {
                        dates.add(dayOfMonth);
                    }
                    if (dates.size() >= 5) {
                        break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return dates;
    }
    
    private void initHeaderView() {
        String account = Config.getAccount(this);
        String name = Config.getAccount(this);
        String startDate = Config.getStartDate(this);
        String endDate = Config.getEndDate(this);
        
        if (!TextUtils.isEmpty(account)) {
            TextView accountView = (TextView) findViewById(R.id.meeting_order);
            accountView.setText(account);
        }
        
        if (!TextUtils.isEmpty(name)) {
            TextView nameView = (TextView) findViewById(R.id.name);
            nameView.setText(name);
        }
        
        if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            String scheduleDate = String.format(getResources().getString(R.string.schedule_date_format), startDate, endDate);
            TextView dateView = (TextView) findViewById(R.id.date);
            dateView.setText(scheduleDate);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        
        return mDatabaseHelper;
    }

    @Override
    protected void onDestroy() {
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
        
        super.onDestroy();
    }

    public class ScheduleAdapter extends BaseAdapter implements StickyListHeadersAdapter {
        private List<Schedule> mData;
        private LayoutInflater mInflater;
        private String[] mDayOfWeeks;
        private Context mContext;
        private HashMap<Integer, Integer> mDay2Position;
        
        public ScheduleAdapter(Context context) {
            super();
            mContext = context;
            mData = new ArrayList<Schedule>();
            mDay2Position = new HashMap<Integer, Integer>();
            mInflater = LayoutInflater.from(context);
            mDayOfWeeks = getResources().getStringArray(R.array.day_of_week);
        }

        public void setData(List<Schedule> data) {
            mData.clear();
            mDay2Position.clear();
            if (data != null) {
                mData.addAll(data);
                buildDay2Position();
            }
            notifyDataSetChanged();
        }
        
        private void buildDay2Position() {
            Calendar calendar = Calendar.getInstance();
            
            long lastDate = 0;
            int position = 0;
            for (Schedule schedule : mData) {
                calendar.setTimeInMillis(schedule.getDate());
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                
                long curDate = schedule.getDate() / (24 * 60 * 60 * 1000);
                if (curDate != lastDate) {
                    mDay2Position.put(day, position);
                    lastDate = curDate;
                }
                
                position++;
            }
        }
        
        public void clear() {
            mData.clear();
            mDay2Position.clear();
            notifyDataSetChanged();
        }
        
        public int getPositionByDay(int day) {
            if (mDay2Position.containsKey(day)) {
                return mDay2Position.get(day);
            }
            
            return -1;
        }
        
        public int getHeaderCount(int position) {
            int count = 0;
            for (int dayPosition : mDay2Position.values()) {
                if (dayPosition <= position) {
                    count++;
                }
            }
            return count;
        }
        
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ((Schedule) getItem(position)).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ScheduleView scheduleView = new ScheduleView(mContext);
                scheduleView.setSchedule((Schedule) getItem(position));
                convertView = scheduleView;
            } else {
                ScheduleView scheduleView = (ScheduleView) convertView;
                scheduleView.setSchedule((Schedule) getItem(position));
            }
            
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            Schedule schedule = (Schedule) getItem(position);
            return schedule.getDate() / (24 * 60 * 60 * 1000);
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = mInflater.inflate(R.layout.dateline, parent, false);
                holder.dateText = (TextView) convertView.findViewById(R.id.date);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }

            Schedule schedule = (Schedule) getItem(position);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(schedule.getDate());
            holder.dateText.setText(String.format("%s %s", new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime()), mDayOfWeeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]));

            return convertView;
        }
        
        class HeaderViewHolder {
            TextView dateText;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }

    public static class ScheduleLoader extends AsyncTaskLoader<List<Schedule>> {
        private Dao<Schedule, Long> mDao;
        
        @Override
        protected void onStartLoading() {
            forceLoad();
            super.onStartLoading();
        }
        
        public ScheduleLoader(Context context, Dao<Schedule, Long> dao) {
            super(context);
            mDao = dao;
        }

        @Override
        public List<Schedule> loadInBackground() {
            List<Schedule> schedules = new ArrayList<Schedule>();
            
            try {
                schedules = mDao.queryBuilder().orderBy("date", true).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return schedules;
        }
        
    }
    

    @Override
    public Loader<List<Schedule>> onCreateLoader(int arg0, Bundle arg1) {
        return new ScheduleLoader(this, mDao);
    }

    @Override
    public void onLoadFinished(Loader<List<Schedule>> arg0, List<Schedule> arg1) {
        mAdapter.setData(arg1);
    }

    @Override
    public void onLoaderReset(Loader<List<Schedule>> arg0) {
        mAdapter.clear();
    }
}
