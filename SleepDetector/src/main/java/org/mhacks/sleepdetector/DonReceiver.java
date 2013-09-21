package org.mhacks.sleepdetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mike on 9/21/13.
 */
public class DonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DonReceiver", "Got don event!");
        if(intent.getBooleanExtra("is_donned", false)) {
            context.startActivity(new Intent(context, FullscreenActivity.class));
        }
        else {
            context.stopService(new Intent(context, ProxService.class));
            context.stopService(new Intent(context, AccelerometerService.class));
        }
    }
}
