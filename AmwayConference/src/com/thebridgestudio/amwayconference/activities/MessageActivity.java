package com.thebridgestudio.amwayconference.activities;

import java.sql.SQLException;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.support.extras.AndroidBaseDaoImpl;
import com.j256.ormlite.android.support.extras.OrmliteCursorAdapter;
import com.j256.ormlite.android.support.extras.OrmliteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.daos.DatabaseHelper;
import com.thebridgestudio.amwayconference.models.Message;

public class MessageActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
    private MessageCursorAdapter mAdapter;
    private DatabaseHelper mDatabaseHelper = null;
    private Dao<Message, Long> mDao = null;
    private PreparedQuery<Message> mPrepareQuery = null;
    private ListView mListView = null;
    
    @Override
    protected void onDestroy() {
        if (mDatabaseHelper != null) {
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
        
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.message);
        mListView = (ListView) findViewById(R.id.list);
        
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        mListView.setEmptyView(progressBar);
        
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                try {
                    UpdateBuilder<Message, Long> updateBuilder = mDao.updateBuilder();
                    updateBuilder.updateColumnValue("read", true).where().idEq(arg3);
                    updateBuilder.update();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(MessageActivity.this, R.string.mark_message_read_failed, Toast.LENGTH_SHORT).show();
                }
            }
            
        });
        
        try {
            mDao = getHelper().getDao(Message.class);
            QueryBuilder<Message, Long> queryBuilder = mDao.queryBuilder();
            
            mPrepareQuery = queryBuilder.prepare();
            
            mAdapter = new MessageCursorAdapter(this, null, mPrepareQuery);
            mListView.setAdapter(mAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (mAdapter != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int arg0,
            Bundle arg1) {
        OrmliteCursorLoader<Message> loader = null;
        
        try {
            loader = new OrmliteCursorLoader<Message>(this, new MessageDao(getHelper().getConnectionSource(), Message.class), mPrepareQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0,
            Cursor arg1) {
        mAdapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }
    
    private DatabaseHelper getHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        
        return mDatabaseHelper;
    }
    
    public class MessageCursorAdapter extends OrmliteCursorAdapter<Message> {

        public MessageCursorAdapter(Context context, Cursor c,
                PreparedQuery<Message> query) {
            super(context, c, query);
        }

        @Override
        public void bindView(View itemView, Context context, Message item) {
            ViewHolder viewHolder = (ViewHolder) itemView.getTag();
            
            if (item.isRead()) {
                //TODO set image to read
            } else {
                //TODO set image to unread
            }
            
            if (!TextUtils.isEmpty(item.getContent())) {
                viewHolder.message.setText(item.getContent());
            } else {
                viewHolder.message.setText(R.string.default_message);
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout item = (LinearLayout) layoutInflater.inflate(R.layout.message_item, parent, false);
            
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) item.findViewById(R.id.image);
            viewHolder.message = (TextView) item.findViewById(R.id.message);
            
            item.setTag(viewHolder);
            return item;
        }
        
        class ViewHolder {
            public ImageView image;
            public TextView message;
        }
    }
    
    public class MessageDao extends AndroidBaseDaoImpl<Message, Integer> {

        public MessageDao(ConnectionSource connectionSource,
                Class<Message> dataClass) throws SQLException {
            super(connectionSource, dataClass);
        }
        
    }
}
