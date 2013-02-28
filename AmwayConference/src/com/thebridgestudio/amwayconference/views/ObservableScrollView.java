package com.thebridgestudio.amwayconference.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {
  private ScrollViewListener mScrollViewListener;

  public ObservableScrollView(Context context) {
    super(context);
  }

  public interface ScrollViewListener {
    public static final int SCROLL_STATE_IDLE = 0;

    void onScroll(int x, int y, int oldx, int oldy);
  }

  public ObservableScrollView(Context context, AttributeSet attrs,
      int defStyle) {
    super(context, attrs, defStyle);
  }

  public ObservableScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setScrollViewListener(ScrollViewListener scrollViewListener) {
    mScrollViewListener = scrollViewListener;
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    if (mScrollViewListener != null) {
      mScrollViewListener.onScroll(l, t, oldl, oldt);
    }
    super.onScrollChanged(l, t, oldl, oldt);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return super.onTouchEvent(ev);
  }
}
