package org.mhacks.sleepdetector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.tomtom.lbs.sdk.geolocation.ReverseGeocodeData;
import com.tomtom.lbs.sdk.geolocation.ReverseGeocodeListener;
import com.tomtom.lbs.sdk.geolocation.ReverseGeocoder;
import com.tomtom.lbs.sdk.util.Coordinates;

import java.util.Vector;

/**
 * Created by Hephaestus on 9/21/13.
 */
public class HUDService extends Service {

    MyLocationListener mlocListener = new MyLocationListener();
    LocationManager mlocManager;
    TextView speedLimitView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Log.e("SleepDetector", "come on");
            String text = "My current location is: " + "Latitude = "
                    + loc.getLatitude() + "Longitude = " + loc.getLongitude();

            Log.d("SleepDetector", "Getting Location...");
            Log.d("SleepDetector", text);

            updateSpeedLimit(new Coordinates(loc.getLatitude(), loc.getLongitude()));
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override
        public void onProviderEnabled(String s) {}
        @Override
        public void onProviderDisabled(String s) {}
    }

    private class MyGeocodeListener implements ReverseGeocodeListener {

        @Override
        public void handleReverseGeocode(Vector<ReverseGeocodeData> data, Object payload) {
            if (data != null && data.size()>0 ) {
                ReverseGeocodeData result = data.elementAt(0);
                Log.d("SleepDetector", result.street);
                Log.d("SleepDetector", Double.toString((double) result.maxSpeedKph * 0.621371));
                Log.d("SleepDetector", Double.toString((double)result.averageSpeedKph * 0.621371));

                speedLimitView.setText(Double.toString((double) result.maxSpeedKph * 0.621371));
                getLocation();
            } else {
                Log.e("SleepDetector", "this happened");
            }
        }
    }

    public void setSpeedLimitView(TextView speedLimitView) {
        this.speedLimitView = speedLimitView;
    }

    private void updateSpeedLimit(Coordinates coords) {
        Log.e("SleepDetector", "come onnn");
        Log.d("SleepDetector", Double.toString(coords.getLat()));
        MyGeocodeListener listener = new MyGeocodeListener();
        ReverseGeocoder.reverseGeocode(coords, null, listener, null);
    }

    public void getLocation() {
        Log.e("SleepDetector", "WE'RE HERE!");
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    @Override
    public void onCreate() {
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Log.e("SleepDetector", "WE'RE GERE!");
        getLocation();
    }
}
