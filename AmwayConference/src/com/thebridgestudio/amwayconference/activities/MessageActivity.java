package com.thebridgestudio.amwayconference.activities;

import org.apache.commons.codec.binary.StringUtils;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.android.support.extras.OrmliteCursorAdapter;
import com.j256.ormlite.stmt.PreparedQuery;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.models.Message;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MessageActivity extends ListActivity {
    private MessageCursorAdapter mAdapter;
    private OrmLiteSqliteOpenHelper mHelper;
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
        
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
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
    
}
