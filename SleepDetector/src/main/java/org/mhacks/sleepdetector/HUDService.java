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
import android.widget.Toast;

import com.tomtom.lbs.sdk.geolocation.ReverseGeocodeData;
import com.tomtom.lbs.sdk.geolocation.ReverseGeocodeListener;
import com.tomtom.lbs.sdk.geolocation.ReverseGeocodeOptionalParameters;
import com.tomtom.lbs.sdk.geolocation.ReverseGeocoder;
import com.tomtom.lbs.sdk.util.Coordinates;
import com.tomtom.lbs.sdk.util.SDKContext;

import java.util.Vector;

/**
 * Created by Hephaestus on 9/21/13.
 */
public class HUDService extends Service {

    MyLocationListener mlocListener = new MyLocationListener();
    LocationManager mlocManager;
    TextView speedLimitView;
    Double curSpeed = 0.0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            String text = "My current location is: " + "Latitude = "
                    + loc.getLatitude() + "Longitude = " + loc.getLongitude();

            Log.d("SleepDetector", "Getting Location...");
            Log.d("SleepDetector", text);

            curSpeed = loc.getSpeed() * 2.23694;
            Log.d("SleepDetector", "CurSpeed: " + Double.toString(curSpeed));
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
            }
            getLocation();
        }
    }

    public void setSpeedLimitView(TextView speedLimitView) {
        this.speedLimitView = speedLimitView;
    }

    private void updateSpeedLimit(Coordinates coords) {
        MyGeocodeListener listener = new MyGeocodeListener();
        ReverseGeocodeOptionalParameters params = new ReverseGeocodeOptionalParameters();
        params.type = ReverseGeocodeOptionalParameters.REVERSE_TYPE_ALL;
        ReverseGeocoder.reverseGeocode(coords, params, listener, null);
    }

    public void getLocation() {
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
    }

    @Override
    public void onCreate() {
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        SDKContext.setDeveloperKey("864stzx5n9senu3tbgb7ttqq");
        getLocation();
    }


}
