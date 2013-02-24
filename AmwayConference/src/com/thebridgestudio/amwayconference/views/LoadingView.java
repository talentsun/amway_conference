package com.thebridgestudio.amwayconference.views;

import com.thebridgestudio.amwayconference.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LoadingView extends ImageView {

  public LoadingView(Context context) {
    super(context);
    initView();
  }

  public LoadingView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initView();
  }



  public LoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView();
  }



  private void initView() {
    setBackgroundResource(R.drawable.loading);
    AnimationDrawable background = (AnimationDrawable) getBackground();
    background.start();
  }

}
