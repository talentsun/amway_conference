package com.thebridgestudio.amwayconference.activities;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.thebridgestudio.amwayconference.views.LoadingView;
import com.thebridgestudio.amwayconference.views.ScheduleDateView;
import com.thebridgestudio.amwayconference.views.ScheduleView;

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
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScheduleActivity extends BaseActivity implements LoaderCallbacks<List<Schedule>> {
    public class ScheduleAdapter extends BaseAdapter implements StickyListHeadersAdapter {
        class HeaderViewHolder {
            TextView dateText;
        }
        private Context mContext;
        private List<Schedule> mData;
        private HashMap<Integer, Integer> mDay2Position;
        private String[] mDayOfWeeks;
        
        private LayoutInflater mInflater;

        public ScheduleAdapter(Context context) {
            super();
            mContext = context;
            mData = new ArrayList<Schedule>();
            mDay2Position = new HashMap<Integer, Integer>();
            mInflater = LayoutInflater.from(context);
            mDayOfWeeks = getResources().getStringArray(R.array.day_of_week);
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
        
        @Override
        public int getCount() {
            return mData.size();
        }
        
        public int getHeaderCount() {
            return mDay2Position.size();
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

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ((Schedule) getItem(position)).getId();
        }

        public int getPositionByDay(int day) {
            if (mDay2Position.containsKey(day)) {
                return mDay2Position.get(day);
            }
            
            return -1;
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
            
            ViewTreeObserver observer = convertView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                
                @Override
                public void onGlobalLayout() {
                    // TODO Auto-generated method stub
                    
                }
            });

            return convertView;
        }
        
        @Override
        public boolean isEnabled(int position) {
            return false;
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
    
    private StickyListHeadersListView mListView;
    private LoadingView mLoadingView;
    private ImageView mTagView;
    private TextView mNoDataView;

    private ScheduleDateView mScheduleDateView;
    private int computeOffset(int fromPosition, int toPosition) {
        int toOffset = 0;
        for (int i = 0; i < toPosition; i++) {
            View listItem = mAdapter.getView(i, null, mListView);
            listItem.measure(0, 0);
            toOffset += listItem.getMeasuredHeight();
        }
        
        int fromOffset = 0;
        for (int i=0; i < fromPosition; i++) {
            View listItem = mAdapter.getView(i, null, mListView);
            listItem.measure(0, 0);
            fromOffset += listItem.getMeasuredHeight();
        }
        fromOffset -= mListView.getChildAt(0) == null ? 0 : mListView.getChildAt(0).getTop();
        
        int headersHeight = getResources().getDimensionPixelSize(
                R.dimen.schedule_dateline_height)
                * (mAdapter.getHeaderCount(toPosition) - mAdapter.getHeaderCount(fromPosition));
        return toOffset - fromOffset + headersHeight;
    }
    
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
    
    private void initListView() {
        mListView = (StickyListHeadersListView) findViewById(R.id.list);
        LinearLayout emptyView = (LinearLayout) findViewById(android.R.id.empty);
        mListView.setEmptyView(emptyView);
        
        try {
            mDao = getHelper().getDao(Schedule.class);
            mAdapter = new ScheduleAdapter(this);
            mListView.setAdapter(mAdapter);
            mListView.setOnScrollListener(new OnScrollListener() {
                
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                        int visibleItemCount, int totalItemCount) {
                }
                
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (mListView.getFirstVisiblePosition() == 0
                                && mListView.getChildAt(0).getTop() == 0) {
                            if (isFold) {
                                unfoldHeader();
                            }
                        } else {
                            if (!isFold) {
                                foldHeader();
                            }
                        }
                    }
                }
            });
            
            initListViewData();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "load dao failed");
        }
    }

    private void initListViewData() {
        showLoading();
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void initScheduleDateView() {
        mScheduleDateView = (ScheduleDateView) findViewById(R.id.schedule_date_view);
        List<Integer> dates = getScheduleDates();
        if (dates.size() > 0) {
            mScheduleDateView.setDates(dates);
            mScheduleDateView.setOnDateSelectedListener(new ScheduleDateView.OnDateSelectedListener() {
                
                @Override
                public void onDateSelected(int date) {
                    final int day = date;
                    mListView.smoothScrollBy(computeOffset(mListView.getFirstVisiblePosition(), mAdapter.getPositionByDay(day)), 50);
                    mListView.postDelayed(new Runnable() {
                        
                        @Override
                        public void run() {
                            mListView.smoothScrollBy(computeOffset(mListView.getFirstVisiblePosition(), mAdapter.getPositionByDay(day)), 50);
                        }
                    }, 50);
                }
            });
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
                initListViewData();
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
        
        initAccount();
        initScheduleDate();
        initListView();
        initScheduleDateView();
        initDataObserver();
        initSidebar();
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
        mScheduleDateView.setVisibility(View.GONE);
        showNoData();
    }

    @Override
    public void onLoadFinished(Loader<List<Schedule>> arg0, List<Schedule> arg1) {
        mAdapter.setData(arg1);
        if (!mAdapter.isEmpty()) {
            mScheduleDateView.setVisibility(View.VISIBLE);
        }
        showNoData();
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
        mLoadingView.setVisibility(View.VISIBLE);
        mNoDataView.setVisibility(View.GONE);
    }
    
    private void showNoData() {
        mLoadingView.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.VISIBLE);
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
