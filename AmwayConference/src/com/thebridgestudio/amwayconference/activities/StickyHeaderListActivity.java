package com.thebridgestudio.amwayconference.activities;

import static android.widget.Toast.LENGTH_SHORT;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Toast;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;
import com.thebridgestudio.amwayconference.R;

public class StickyHeaderListActivity extends Activity implements OnScrollListener,
        AdapterView.OnItemClickListener, OnHeaderClickListener {

    private static final String KEY_LIST_POSITION = "KEY_LIST_POSITION";
    private int firstVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticky_list);
        StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.list);
        stickyList.setOnScrollListener(this);
        stickyList.setOnItemClickListener(this);
        stickyList.setOnHeaderClickListener(this);

        if (savedInstanceState != null) {
            firstVisible = savedInstanceState.getInt(KEY_LIST_POSITION);
        }

        stickyList.addHeaderView(getLayoutInflater().inflate(
                R.layout.sticky_list_header, null));
        stickyList.addFooterView(getLayoutInflater().inflate(
                R.layout.sticky_list_footer, null));
        stickyList.setAdapter(new StickyHeaderListAdapter(this));
        stickyList.setSelection(firstVisible);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LIST_POSITION, firstVisible);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        this.firstVisible = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Toast.makeText(this, "Item " + position + " clicked!", LENGTH_SHORT)
                .show();
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header,
            int itemPosition, long headerId, boolean currentlySticky) {
        Toast.makeText(this, "header " + headerId, Toast.LENGTH_SHORT).show();
    }

}