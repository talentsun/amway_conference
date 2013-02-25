package com.thebridgestudio.amwayconference.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class ImageUtils {
  public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable == null || drawable.getIntrinsicWidth() == 0
            || drawable.getIntrinsicHeight() == 0) {
        return null;
    }
    Bitmap bitmap = Bitmap
            .createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
            .getIntrinsicHeight());
    drawable.draw(canvas);
    return bitmap;
  }
}
