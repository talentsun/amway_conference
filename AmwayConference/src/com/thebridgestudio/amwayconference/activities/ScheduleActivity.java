package com.thebridgestudio.amwayconference.activities;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        
        mScheduleDateView = (ScheduleDateView) findViewById(R.id.schedule_date_view);
        mScheduleDateView.setDates(new int[] {6,7,8,9,10});
        
        mListView = (StickyListHeadersListView) findViewById(R.id.list);
        
        try {
            mDao = getHelper().getDao(Schedule.class);
            mAdapter = new ScheduleAdapter(this);
            mListView.setAdapter(mAdapter);
            
            getSupportLoaderManager().initLoader(0, null, this);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "load dao failed");
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
        
        public ScheduleAdapter(Context context) {
            super();
            mContext = context;
            mData = new ArrayList<Schedule>();
            mInflater = LayoutInflater.from(context);
            mDayOfWeeks = getResources().getStringArray(R.array.day_of_week);
        }

        public void setData(List<Schedule> data) {
            mData.clear();
            if (data != null) {
                mData.addAll(data);
            }
            notifyDataSetChanged();
        }
        
        public void clear() {
            mData.clear();
            notifyDataSetChanged();
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
        public long getHeaderId(int arg0) {
            Schedule schedule = (Schedule) getItem(arg0);
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
