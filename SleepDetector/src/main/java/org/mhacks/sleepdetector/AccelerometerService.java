package org.mhacks.sleepdetector;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class AccelerometerService extends Service implements SensorEventListener {

    public static final float LOW_THRESH = 0.55f;
    public static final float HIGH_THRESH = 0.8f;
    public static final float CRASH_GS = 1.5f;

    public static final String INTENT_WAKE_UP = "org.mhacks.sleepdetector.INTENT_WAKE_UP";
    public static final String INTENT_WOKE = "org.mhacks.sleepdetector.INTENT_WOKE";
    public static final String INTENT_CRASHED = "org.mhacks.sleepdetector.INTENT_CRASHED";

    private SensorManager mSensorManager;
    private  Sensor mRotation, mAccel;

    public AccelerometerService() {

    }

    @Override
    public void onCreate() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int mCt;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
        if(sensorEvent.values[0] > HIGH_THRESH) {
            mCt++;
        }
        else if(sensorEvent.values[0] < LOW_THRESH) {
            mCt++;
        }
        else {
            if(mCt > 250) {
                sendBroadcast(new Intent(INTENT_WOKE));
            }
            mCt = 0;
        }
        if(mCt > 250) {
            sendBroadcast(new Intent(INTENT_WAKE_UP));
        }
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float absAccelerationG = 0;
            for(float f : sensorEvent.values) {
                absAccelerationG += Math.pow(f, 2);
            }
            absAccelerationG = ((float) Math.sqrt(absAccelerationG));
            absAccelerationG /= 9.81;
            Log.d("AccelerometerService", ""+absAccelerationG);
            if(absAccelerationG > CRASH_GS) {
                sendBroadcast(new Intent(INTENT_CRASHED));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
