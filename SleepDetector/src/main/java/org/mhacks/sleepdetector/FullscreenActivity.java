package org.mhacks.sleepdetector;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class FullscreenActivity extends Activity {

    private RelativeLayout mBackgroundLayout;

    MediaPlayer player;
    private boolean canBeep = true;

    @Override
    protected void onPause() {
        super.onPause();
        canBeep = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        canBeep = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen);

        Log.d("SleepDetector", "Start");

        player = MediaPlayer.create(this, R.raw.beep_9);

        Typeface robotoLight = Typeface.createFromAsset(getAssets(),
                "Roboto-Light.ttf");

        TextView tv = (TextView)findViewById(R.id.speedTextView);
        tv.setTypeface(robotoLight);

        mBackgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);

//        startService(new Intent(this, ProxService.class));
        startService(new Intent(this, HUDService.class));
        startService(new Intent(this, AccelerometerService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction(AccelerometerService.INTENT_WAKE_UP);
        filter.addAction(AccelerometerService.INTENT_WOKE);
        filter.addAction(AccelerometerService.INTENT_CRASHED);
        filter.addAction(HUDService.INTENT_SPEED_CHANGED);
        filter.addAction(DonReceiver.INTENT_DON);
        filter.addAction(DonReceiver.INTENT_UNDON);
        registerReceiver(new EventReceiver(), filter);
    }

    private void startRecording() {
        startActivityForResult(new Intent(this, RecordActivity.class), 5);
    }

    private boolean recording = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 5)
            recording = false;
    }

    private class EventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // We need to wake the user up
            if(AccelerometerService.INTENT_WAKE_UP.equals(intent.getAction())) {
                mBackgroundLayout.setBackgroundColor(getResources().getColor(R.color.red));
                    player.start();
            }
            else if(AccelerometerService.INTENT_WOKE.equals(intent.getAction())) {
                mBackgroundLayout.setBackgroundColor(getResources().getColor(R.color.black));
                    player.pause();

            }
            else if(AccelerometerService.INTENT_CRASHED.equals(intent.getAction())) {
                if(!recording) {
                    Intent i = new Intent("com.google.glass.action.MESSAGE");
                    i.putExtra("MESSAGE", "EMERGENCY!!!");
                    recording = true;
                    startRecording();
                }
            }
            else if(HUDService.INTENT_SPEED_CHANGED.equals(intent.getAction())) {
                TextView speedView = (TextView)findViewById(R.id.speedTextView);
                speedView.setText(Integer.toString((int)intent.getFloatExtra("Speed", 0)));
                TextView speedLimitTextView = (TextView)findViewById(R.id.speedLimitTextView);
                speedLimitTextView.setText(Integer.toString(intent.getIntExtra("SpeedLimit", 20)));
            }

            int rand = (int)(Math.random() * 600);
            int height = findViewById(R.id.gradientCoverLayout).getLayoutParams().height;
            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(rand, height);
            newParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.gradientLayout);
            newParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            findViewById(R.id.gradientCoverLayout).setLayoutParams(newParams);
//            TextView speedTextView = (TextView)findViewById(R.id.speedTextView);
//            speedTextView.setText(Integer.toString((600 - rand) / 8));
        }
    }
}
