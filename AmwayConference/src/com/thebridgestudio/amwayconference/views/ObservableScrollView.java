package com.thebridgestudio.amwayconference.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {
  private int mOffsetY;
  private ScrollViewListener mScrollViewListener;

  public ObservableScrollView(Context context) {
    super(context);
  }

  public interface ScrollViewListener {
    public static final int SCROLL_STATE_IDLE = 0;

    void onScrollStateIdle(ObservableScrollView scrollView);
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

  public int getOffsetY() {
    return mOffsetY;
  }



  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    mOffsetY = t;
    super.onScrollChanged(l, t, oldl, oldt);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_UP) {
      if (mScrollViewListener != null) {
        mScrollViewListener.onScrollStateIdle(this);
      }
    }

    return super.onTouchEvent(ev);
  }
}
