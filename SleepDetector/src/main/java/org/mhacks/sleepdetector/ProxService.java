package org.mhacks.sleepdetector;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.io.File;

public class ProxService extends Service {

    private Handler refreshHandler;
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {


        }
    };
    private File sensorFile;
    public static final String SENSOR_PATH
            = "/sys/devices/platform/omap/omap_i2c.4/i2c-4/4-0035/proxraw";

    @Override
    public void onCreate() {
        sensorFile = new File(SENSOR_PATH);
    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
