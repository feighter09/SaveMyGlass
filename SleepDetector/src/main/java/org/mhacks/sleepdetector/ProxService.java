package org.mhacks.sleepdetector;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

public class ProxService extends Service {

    private float runningAverage;
    private Queue<Integer> last20Secs;

    private Handler refreshHandler = new Handler();
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            new RefreshTask().execute();
        }
    };
    private File sensorFile;
    public static final String SENSOR_PATH
            = "/sys/devices/platform/omap/omap_i2c.4/i2c-4/4-0035/proxraw";

    private class RefreshTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String val = Utils.readFile(SENSOR_PATH);

            while (last20Secs.size() < 400) {
                last20Secs.add(Integer.parseInt(val));
                try {
                    wait(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                val = Utils.readFile(SENSOR_PATH);
            }

            Log.d("SleepDetector", "Reading value...");
            Log.d("SleepDetector", val);

            decideWhatToDo(Integer.parseInt(val));

            return Utils.readFile(SENSOR_PATH);
        }

        @Override
        protected void onPostExecute(String result) {
            refreshHandler.postDelayed(refreshRunnable, 50);

        }

        protected void decideWhatToDo(int val) {
            int avg = queueAvg();

        }

        protected int queueAvg() {
            int sum = 0;
            for (int i : last20Secs)
                sum += i;

            return sum / last20Secs.size();
        }
    }


    @Override
    public void onCreate() {
        sensorFile = new File(SENSOR_PATH);
        refreshHandler.post(refreshRunnable);
    }




    @Override
    public IBinder onBind(Intent intent) {



        return null;
    }
}
