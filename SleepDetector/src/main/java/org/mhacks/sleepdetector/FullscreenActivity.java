package org.mhacks.sleepdetector;

import org.mhacks.sleepdetector.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class FullscreenActivity extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        Log.d("SleepDetector", "Start");

        TextView tv = (TextView)findViewById(R.id.speedTextView);
        tv.setText("45 mph");
        HUDService HUD = new HUDService();
        HUD.setSpeedLimitView(tv);
//        startService(new Intent(this, ProxService.class));
//        startService(new Intent(this, HUDService.class));
        startService(new Intent(this, HUDService.class));
//        startService(new Intent(this, AccelerometerService.class));
    }

}
