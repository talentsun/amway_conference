package com.thebridgestudio.amwayconference.activities;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Schedule;
import com.thebridgestudio.amwayconference.views.LoadingView;
import com.thebridgestudio.amwayconference.views.ObservableScrollView;
import com.thebridgestudio.amwayconference.views.ObservableScrollView.ScrollViewListener;
import com.thebridgestudio.amwayconference.views.ScheduleDateView;
import com.thebridgestudio.amwayconference.views.ScheduleView;

public class ScheduleActivity extends BaseActivity implements LoaderCallbacks<List<Schedule>> {
    public class ScheduleAdapter extends BaseAdapter implements StickyListHeadersAdapter {
        class HeaderViewHolder {
            TextView dateText;
        }
        private Context mContext;
        private List<Schedule> mData;
        private HashMap<Integer, View> mDay2HeaderView;
        private String[] mDayOfWeeks;
        
        private LayoutInflater mInflater;

        public ScheduleAdapter(Context context) {
            super();
            mContext = context;
            mData = new ArrayList<Schedule>();
            mDay2HeaderView = new HashMap<Integer, View>();
            mInflater = LayoutInflater.from(context);
            mDayOfWeeks = getResources().getStringArray(R.array.day_of_week);
        }

        public void clear() {
            mData.clear();
            mDay2HeaderView.clear();
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount() {
            return mData.size();
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

            mDay2HeaderView.put(calendar.get(Calendar.DAY_OF_MONTH), convertView);
            return convertView;
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
        public boolean isEnabled(int position) {
            return false;
        }

        public void setData(List<Schedule> data) {
            mData.clear();
            mDay2HeaderView.clear();
            if (data != null) {
                mData.addAll(data);
            }
            notifyDataSetChanged();
        }
    
        public View getHeaderViewByDay(int day) {
            return mDay2HeaderView.get(day);
        }
    }
    
    public static class ScheduleLoader extends AsyncTaskLoader<List<Schedule>> {
        private Dao<Schedule, Long> mDao;
        
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

        @Override
        protected void onStartLoading() {
            forceLoad();
            super.onStartLoading();
        }
        
    }
    private static final String TAG = "ScheduleActivity";
    private boolean isFold = false;
    private ScheduleAdapter mAdapter;
    private Dao<Schedule, Long> mDao;
    
    private DatabaseHelper mDatabaseHelper;
    private int mHeaderFoldOffset = 0;
    private RelativeLayout mHeaderView;
    
    private LinearLayout mListView;
    private ObservableScrollView mScrollView;
    private LoadingView mLoadingView;
    private ImageView mTagView;
    private TextView mNoDataView;
    private LinearLayout mEmptyView;
    private ScheduleDateView mScheduleDateView;
    
    
    private void foldHeader() {
        LayoutParams layoutParams = (LayoutParams) mHeaderView.getLayoutParams();
        layoutParams.topMargin = -(mHeaderFoldOffset);
        mHeaderView.setLayoutParams(layoutParams);
        
        RelativeLayout.LayoutParams tagLayoutParams = (RelativeLayout.LayoutParams) mTagView.getLayoutParams();
        tagLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.small_margin) + mHeaderFoldOffset;
        mTagView.setLayoutParams(tagLayoutParams);
        
        isFold = true;
    }
    
    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        
        return mDatabaseHelper;
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
    
    private void initAccount() {
        String account = Config.getAccount(this);
        String name = Config.getName(this);
        
        if (!TextUtils.isEmpty(account)) {
            TextView accountView = (TextView) findViewById(R.id.meeting_order);
            accountView.setText(account);
        }
        
        if (!TextUtils.isEmpty(name)) {
            TextView nameView = (TextView) findViewById(R.id.name);
            nameView.setText(name);
        }
    }
    
    private void initScheduleDate() {
        String startDate = Config.getStartDate(this);
        String endDate = Config.getEndDate(this);
        
        if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
            String scheduleDate = String.format(getResources().getString(R.string.schedule_date_format), startDate, endDate);
            TextView dateView = (TextView) findViewById(R.id.date);
            dateView.setText(scheduleDate);
        }
    }

    private void initListViewData(boolean restart) {
        Log.i(TAG, "show list view data");
        showLoading();
        
        if (restart) {
            getSupportLoaderManager().restartLoader(0, null, this);
        } else {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    private void initScheduleDateView() {
        mScheduleDateView = (ScheduleDateView) findViewById(R.id.schedule_date_view);
        mScheduleDateView.setOnDateSelectedListener(new ScheduleDateView.OnDateSelectedListener() {
            
            @Override
            public void onDateSelected(int day) {
                View headerView = mAdapter.getHeaderViewByDay(day);
                int headerViewTop = headerView.getTop();
                
                mScrollView.smoothScrollTo(0, headerViewTop);
                
                if (headerViewTop == 0) {
                    if (isFold) {
                        unfoldHeader();
                    }
                } else {
                    if (!isFold) {
                        foldHeader();
                    }
                }
            }
        });
        
        initScheduleDateViewData();
    }

    private void initScheduleDateViewData() {
        List<Integer> dates = getScheduleDates();
        Log.i(TAG, "schedule date view: " + dates.size());
        if (dates.size() > 0) {
            mScheduleDateView.setDates(dates);
        } else {
            mScheduleDateView.setVisibility(View.GONE);
        }
    }
    
    private SharedPreferences.OnSharedPreferenceChangeListener mConfigChangeListener = new OnSharedPreferenceChangeListener() {
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                String key) {
            if (key == Config.KEY_ACCOUNT || key == Config.KEY_NAME) {
                initAccount();
            }
            
            if (key == Config.KEY_START_DATE || key == Config.KEY_END_DATE) {
                initScheduleDate();
            }
            
            if (key == Config.KEY_LAST_SYNC_SCHEDULE_TIME) {
                initScheduleDateViewData();
                initListViewData(true);
            }
        }
    };
    
    private void initDataObserver() {
        SharedPreferences config = Config.getConfigs(this);
        config.registerOnSharedPreferenceChangeListener(mConfigChangeListener);
    }
    
    private void releaseDataObserver() {
        SharedPreferences config = Config.getConfigs(this);
        config.unregisterOnSharedPreferenceChangeListener(mConfigChangeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        mHeaderView = (RelativeLayout) findViewById(R.id.header);
        mHeaderFoldOffset = getResources().getDimensionPixelSize(R.dimen.schedule_header_fold_offset);
        
        mLoadingView = (LoadingView) findViewById(R.id.loading);
        mNoDataView = (TextView) findViewById(R.id.no_data);
        mTagView = (ImageView) findViewById(R.id.tag);
        
        try {
            mDao = getHelper().getDao(Schedule.class);
            mAdapter = new ScheduleAdapter(this);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "load dao failed");
        }
          
        initAccount();
        initScheduleDate();
        initSidebar();
        initScheduleDateView();
        initListView();
        initDataObserver();
    }

    private void initListView() {
        mScrollView = (ObservableScrollView) findViewById(R.id.list_wrapper);
        mListView = (LinearLayout) findViewById(R.id.list);
        mEmptyView = (LinearLayout) findViewById(android.R.id.empty);
        
        mScrollView.setScrollViewListener(new ScrollViewListener() {
            
            @Override
            public void onScrollStateIdle(ObservableScrollView scrollView) {
                if (scrollView.getOffsetY() <= 0) {
                    if (isFold) {
                        unfoldHeader();
                    }
                } else {
                    if (!isFold) {
                        foldHeader();
                    }
                }
                
            }
        });
        
        initListViewData(false);
    }

    @Override
    public Loader<List<Schedule>> onCreateLoader(int arg0, Bundle arg1) {
        return new ScheduleLoader(this, mDao);
    }

    @Override
    protected void onDestroy() {
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
        
        releaseDataObserver();
        
        super.onDestroy();
    }

    @Override
    public void onLoaderReset(Loader<List<Schedule>> arg0) {
        mAdapter.clear();
        mListView.removeAllViews();
        showNoData();
    }

    @Override
    public void onLoadFinished(Loader<List<Schedule>> arg0, List<Schedule> arg1) {
        mListView.removeAllViews();
        mAdapter.setData(arg1);
        
        if (!mAdapter.isEmpty()) {
            long lastHeaderId = 0;
            for (int position=0; position<mAdapter.getCount(); position++) {
                long currentHeaderId = mAdapter.getHeaderId(position);
                
                if (lastHeaderId != currentHeaderId) {
                    View headerView = mAdapter.getHeaderView(position, null, null);
                    mListView.addView(headerView);
                    lastHeaderId = currentHeaderId;
                }
                
                View view = mAdapter.getView(position, null, null);
                mListView.addView(view);
            }
            
            showData();
        } else {
            showNoData();
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

    private void showLoading() {
        mScrollView.setVisibility(View.GONE);
        mScheduleDateView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        mNoDataView.setVisibility(View.GONE);
    }
    
    private void showNoData() {
        mScrollView.setVisibility(View.GONE);
        mScheduleDateView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.VISIBLE);
    }
    
    private void showData() {
        mEmptyView.setVisibility(View.GONE);
        mScheduleDateView.setVisibility(View.VISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
    }
    
    private void unfoldHeader() {
        LayoutParams layoutParams = (LayoutParams) mHeaderView.getLayoutParams();
        layoutParams.topMargin = 0;
        mHeaderView.setLayoutParams(layoutParams);
        
        RelativeLayout.LayoutParams tagLayoutParams = (RelativeLayout.LayoutParams) mTagView.getLayoutParams();
        tagLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.small_margin);
        mTagView.setLayoutParams(tagLayoutParams);
        
        isFold = false;
    }
}
