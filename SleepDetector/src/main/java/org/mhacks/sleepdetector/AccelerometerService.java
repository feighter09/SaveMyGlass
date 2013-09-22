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
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int mCt;
    private float[] mRotationMatrix=new float[16];

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

            // assign directions
            float x=sensorEvent.values[0];
            float y=sensorEvent.values[1];
            float z=sensorEvent.values[2];

            Log.d("Accelerometer", x + " " + y + " "+z);

            if(Math.abs(z) > 4) {
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

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            SensorManager.getRotationMatrixFromVector(mRotationMatrix,sensorEvent.values);

//            Log.d("AccelerometerService", mRotationMatrix[0]+" "+mRotationMatrix[1]+" "+mRotationMatrix[2]);


        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float absAccelerationG = 0;
            for(float f : sensorEvent.values) {
                absAccelerationG += Math.pow(f, 2);
            }
            absAccelerationG = ((float) Math.sqrt(absAccelerationG));
            absAccelerationG /= 9.81;
            if(absAccelerationG > CRASH_GS) {
                sendBroadcast(new Intent(INTENT_CRASHED));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}