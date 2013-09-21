package org.mhacks.sleepdetector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FullscreenActivity extends Activity {

    private RelativeLayout mBackgroundLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        Log.d("SleepDetector", "Start");



        TextView tv = (TextView)findViewById(R.id.speedTextView);
        tv.setText("45");
        HUDService HUD = new HUDService();
        HUD.setSpeedLimitView(tv);

        mBackgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);

//        startService(new Intent(this, ProxService.class));
        startService(new Intent(this, HUDService.class));
//        startService(new Intent(this, HUDService.class));
        startService(new Intent(this, AccelerometerService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction(AccelerometerService.INTENT_WAKE_UP);
        filter.addAction(AccelerometerService.INTENT_WOKE);
        filter.addAction(AccelerometerService.INTENT_CRASHED);
        filter.addAction(DonReceiver.INTENT_DON);
        filter.addAction(DonReceiver.INTENT_UNDON);
        registerReceiver(new EventReceiver(), filter);
    }

    private void startRecording() {
        startActivity(new Intent(this, RecordActivity.class));
    }

    private class EventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // We need to wake the user up
            if(AccelerometerService.INTENT_WAKE_UP.equals(intent.getAction())) {
                mBackgroundLayout.setBackgroundColor(getResources().getColor(R.color.red));
            }
            //
            else if(AccelerometerService.INTENT_WOKE.equals(intent.getAction())) {
                Log.d("FullscreenActivity", "You woke…");
                mBackgroundLayout.setBackgroundColor(getResources().getColor(R.color.black));
            }
            else if(AccelerometerService.INTENT_CRASHED.equals(intent.getAction())) {
                Log.d("FullscreenActivity", "Detected a crash!!!");
                startRecording();
            }
        }
    }




}
