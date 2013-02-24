package com.thebridgestudio.amwayconference.activities;

import java.io.IOException;
import java.util.Vector;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.thebridgestudio.amwayconference.Config;
import com.thebridgestudio.amwayconference.Intents;
import com.thebridgestudio.amwayconference.R;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis;
import com.thebridgestudio.amwayconference.cloudapis.AccountApis.LoginCallback;
import com.thebridgestudio.amwayconference.qr.CaptureActivityHandler;
import com.thebridgestudio.amwayconference.qr.InactivityTimer;
import com.thebridgestudio.amwayconference.qr.ViewfinderView;
import com.thebridgestudio.amwayconference.qr.camera.CameraManager;
import com.thebridgestudio.amwayconference.services.DataService;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

  private CaptureActivityHandler handler;
  private ViewfinderView viewfinderView;
  private CameraManager cameraManager;
  private boolean hasSurface;
  private Vector<BarcodeFormat> decodeFormats;
  private String characterSet;
  private InactivityTimer inactivityTimer;
  private MediaPlayer mediaPlayer;
  private boolean playBeep;
  private static final float BEEP_VOLUME = 0.10f;
  private boolean vibrate;
  private TextView scanText;
  private ImageView back;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.capture);

    cameraManager = new CameraManager(getApplicationContext());
    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    scanText = (TextView) findViewById(R.id.scan_result);
    back = (ImageView) findViewById(R.id.back);

    viewfinderView.setCameraManager(cameraManager);
    hasSurface = false;
    inactivityTimer = new InactivityTimer(this);

    back.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });

  }

  @Override
  protected void onResume() {
    super.onResume();
    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      initCamera(surfaceHolder);
    } else {
      surfaceHolder.addCallback(this);
      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    decodeFormats = null;
    characterSet = null;

    playBeep = true;
    AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
    if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
      playBeep = false;
    }
    initBeepSound();
    vibrate = true;
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    cameraManager.closeDriver();
  }

  @Override
  protected void onDestroy() {
    inactivityTimer.shutdown();
    super.onDestroy();
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    try {
      cameraManager.openDriver(surfaceHolder);
    } catch (IOException ioe) {
      return;
    } catch (RuntimeException e) {
      return;
    }
    if (handler == null) {
      handler = new CaptureActivityHandler(this, decodeFormats,
          characterSet, cameraManager);
    }
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;

      default:
        break;
    }
    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width,
      int height) {

  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    if (!hasSurface) {
      hasSurface = true;
      initCamera(holder);
    }

  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    hasSurface = false;

  }

  public ViewfinderView getViewfinderView() {
    return viewfinderView;
  }

  public Handler getHandler() {
    return handler;
  }

  public void drawViewfinder() {
    viewfinderView.drawViewfinder();

  }

  public CameraManager getCameraManager() {
    return cameraManager;
  }

  public void handleDecode(Result obj, Bitmap barcode) {
    inactivityTimer.onActivity();
    viewfinderView.drawResultBitmap(barcode);
    playBeepSoundAndVibrate();

    if (obj != null) {
      String scanResult = obj.getText();
      if (TextUtils.isEmpty(scanResult)) {
        Toast.makeText(this, R.string.scan_error, Toast.LENGTH_SHORT)
            .show();
        finish();
        return;
      } else {
        AccountApis.loginAsync(CaptureActivity.this, scanResult
            .toString(), "",
            new LoginCallback() {

              @Override
              public void onLoginOK(String account, String name) {
                Config.setAccount(CaptureActivity.this, account);
                Config.setName(CaptureActivity.this, name);

                Intent scheduleIntent = new Intent(
                    CaptureActivity.this,
                    ScheduleActivity.class);
                startActivity(scheduleIntent);

                Intent finishIntent = new Intent();
                finishIntent.setAction(Intents.ACTION_FINISH);
                sendBroadcast(finishIntent);
                CaptureActivity.this.finish();

                Intent syncIntent = new Intent(
                    CaptureActivity.this, DataService.class);
                syncIntent.setAction(Intents.ACTION_SYNC_ALL);
                startService(syncIntent);
              }

              @Override
              public void onLoginFailed(String errorMsg) {
                Toast.makeText(CaptureActivity.this,
                    R.string.login_failed,
                    Toast.LENGTH_SHORT).show();
              }
            });
      }

    }

  }

  private void initBeepSound() {
    if (playBeep && mediaPlayer == null) {
      // The volume on STREAM_SYSTEM is not adjustable, and users found it
      // too loud,
      // so we now play on the music stream.
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
      mediaPlayer = new MediaPlayer();
      mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
      mediaPlayer.setOnCompletionListener(beepListener);

      AssetFileDescriptor file = getResources().openRawResourceFd(
          R.raw.beep);
      try {
        mediaPlayer.setDataSource(file.getFileDescriptor(), file
            .getStartOffset(), file.getLength());
        file.close();
        mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
        mediaPlayer.prepare();
      } catch (IOException e) {
        mediaPlayer = null;
      }
    }
  }

  private static final long VIBRATE_DURATION = 200L;

  private void playBeepSoundAndVibrate() {
    if (playBeep && mediaPlayer != null) {
      mediaPlayer.start();
    }
    if (vibrate) {
      Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
      vibrator.vibrate(VIBRATE_DURATION);
    }
  }

  /**
   * When the beep has finished playing, rewind to queue up another one.
   */
  private final OnCompletionListener beepListener = new OnCompletionListener() {
    public void onCompletion(MediaPlayer mediaPlayer) {
      mediaPlayer.seekTo(0);
    }
  };
}
