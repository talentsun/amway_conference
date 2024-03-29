package com.thebridgestudio.amwayconference.activities;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.brixd.amway_meeting.R;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.models.Message;
import com.thebridgestudio.amwayconference.views.LoadingView;

public class MessageActivity extends BaseActivity implements
    LoaderCallbacks<List<Message>>, OnScrollListener {
  private static final String TAG = "MessageActivity";
  private static final String KEY_MESSAGE_LIST_POSITION = "KEY_MESSAGE_LIST_POSITION";
  private int mFirstVisible;

  private MessageAdapter mAdapter;
  private StickyListHeadersListView mListView = null;

  private LoadingView mLoadingView;
  private TextView mNoDataView;

  private Handler mMainThreadHandler;
  private SharedPreferences.OnSharedPreferenceChangeListener mConfigChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      final String changedKey = key;
      mMainThreadHandler.post(new Runnable() {
        
        @Override
        public void run() {
          if (changedKey == Config.KEY_LAST_SYNC_MESSAGE_TIME) {
            initMessageData(true);
          }
        }
      });
    }
  };
  
  private void initDataObserver() {
    SharedPreferences config = Config.getConfigs(this);
    config.registerOnSharedPreferenceChangeListener(mConfigChangedListener);
  }

  private void releaseDataObserver() {
    SharedPreferences config = Config.getConfigs(this);
    config.unregisterOnSharedPreferenceChangeListener(mConfigChangedListener);
  }
  
  @Override
  protected void onDestroy() {
    releaseDataObserver();
    super.onDestroy();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.message);

    mLoadingView = (LoadingView) findViewById(R.id.loading);
    mNoDataView = (TextView) findViewById(R.id.no_data);
    
    mMainThreadHandler = new Handler();

    initListView(savedInstanceState);
    initSidebar();
    initDataObserver();
  }

  private void initListView(Bundle savedInstanceState) {
    mListView = (StickyListHeadersListView) findViewById(R.id.list);
    mListView.setOnScrollListener(this);

    LinearLayout emptyView = (LinearLayout) findViewById(android.R.id.empty);
    mListView.setEmptyView(emptyView);

    if (savedInstanceState != null) {
      mFirstVisible = savedInstanceState.getInt(KEY_MESSAGE_LIST_POSITION);
    }
    mListView.setSelection(mFirstVisible);
    mAdapter = new MessageAdapter(this);
    mListView.setAdapter(mAdapter);
    
    mListView.setOnTouchListener(new OnTouchListener() {
      
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        onTouchEvent(event);
        return false;
      }
    });

    initMessageData(false);
  }

  private void initMessageData(boolean restart) {
    showLoading();
    if (restart) {
      getSupportLoaderManager().restartLoader(0, null, this);
    } else {
      getSupportLoaderManager().initLoader(0, null, this);
    }
  }

  @Override
  protected void onStart() {
    try {
      UpdateBuilder<Message, Long> updateBuilder = mMessageDao.updateBuilder();
      updateBuilder.updateColumnValue("read", true);
      updateBuilder.update();
    } catch (SQLException e) {
      e.printStackTrace();
      Toast.makeText(MessageActivity.this,
          R.string.mark_message_read_failed, Toast.LENGTH_SHORT)
          .show();
    }

    super.onStart();
  }

  public class MessageAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private LayoutInflater mInflater;
    private List<Message> mData;
    private String[] mDayOfWeeks;

    public MessageAdapter(Context context) {
      mInflater = LayoutInflater.from(context);
      mDayOfWeeks = getResources().getStringArray(R.array.day_of_week);
      mData = new ArrayList<Message>();
    }

    @Override
    public boolean isEnabled(int position) {
      return false;
    }

    public void setData(List<Message> data) {
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
      if (mData.size() == 0) {
        return 0;
      }

      return ((Message) mData.get(position)).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder viewHolder = null;
      if (convertView == null) {
        viewHolder = new ViewHolder();

        convertView = mInflater.inflate(R.layout.message_item, parent, false);
        viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
        viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.message);
        viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.time);
        convertView.setTag(viewHolder);
      } else {
        viewHolder = (ViewHolder) convertView.getTag();
      }

      Message message = (Message) getItem(position);
      if (!TextUtils.isEmpty(message.getContent())) {
        viewHolder.messageTextView.setText(message.getContent());
      } else {
        viewHolder.messageTextView.setText(R.string.default_message);
      }

      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(message.getDate());
      viewHolder.timeTextView.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));

      return convertView;
    }

    class ViewHolder {
      public ImageView imageView;
      public TextView messageTextView;
      public TextView timeTextView;
    }

    @Override
    public long getHeaderId(int position) {
      Message message = (Message) getItem(position);
      return message.getDate() / (24 * 60 * 60 * 1000);
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

      Message message = (Message) getItem(position);
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(message.getDate());
      holder.dateText.setText(String.format("%s    %s",
          new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime()),
          mDayOfWeeks[calendar.get(Calendar.DAY_OF_WEEK) - 1]));

      return convertView;
    }

    class HeaderViewHolder {
      TextView dateText;
    }
  }

  public static class MessageLoader extends AsyncTaskLoader<List<Message>> {
    private Dao<Message, Long> mDao;

    public MessageLoader(Context context, Dao<Message, Long> dao) {
      super(context);
      Log.i(TAG, "create message loader");
      mDao = dao;
    }

    @Override
    protected void onStartLoading() {
      forceLoad();
      super.onStartLoading();
    }

    @Override
    public List<Message> loadInBackground() {
      List<Message> messages = new ArrayList<Message>();

      try {
        messages = mDao.queryBuilder().orderBy("date", false).query();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return messages;
    }

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_MESSAGE_LIST_POSITION, mFirstVisible);
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem,
      int visibleItemCount, int totalItemCount) {
    mFirstVisible = firstVisibleItem;
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
    // TODO Auto-generated method stub

  }

  @Override
  public Loader<List<Message>> onCreateLoader(int arg0, Bundle arg1) {
    return new MessageLoader(this, mMessageDao);
  }

  @Override
  public void onLoadFinished(Loader<List<Message>> arg0, List<Message> arg1) {
    mAdapter.setData(arg1);
    showNoData();
  }

  @Override
  public void onLoaderReset(Loader<List<Message>> arg0) {
    mAdapter.clear();
    showNoData();
  }

  private void showLoading() {
    mLoadingView.setVisibility(View.VISIBLE);
    mNoDataView.setVisibility(View.GONE);
  }

  private void showNoData() {
    mLoadingView.setVisibility(View.GONE);
    mNoDataView.setVisibility(View.VISIBLE);
  }
}
