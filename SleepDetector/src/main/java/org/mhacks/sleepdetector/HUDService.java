package org.mhacks.sleepdetector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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

    public static final String INTENT_SPEED_CHANGED = "org.mhacks.sleepdetector.INTENT_SPEED_CHANGED";

    Context ctx;
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
            if (loc == null) return;

            if (loc.hasSpeed()) {
                String text = "Latitude = " + loc.getLatitude()
                                + "Longitude = " + loc.getLongitude();
                Log.d("SleepDetector", text);

                curSpeed = loc.getSpeed() * 2.23694;
                Log.d("SleepDetector", "CurSpeed: " + Double.toString(curSpeed));
            } else {
                Log.d("HUD", "Location returned without speed");
            }
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

            ReverseGeocodeData result = new ReverseGeocodeData();
            if (data != null && data.size()>0 ) {
                result = data.elementAt(0);
                Log.d("SleepDetector", result.street);
                Log.d("SleepDetector", Double.toString((double) result.maxSpeedKph * 0.621371));
                Log.d("SleepDetector", Double.toString((double)result.averageSpeedKph * 0.621371));

//                speedLimitView.setText(Double.toString((double) result.maxSpeedKph * 0.621371));
            } else
                result.maxSpeedKph = (int)(20 / 0.621371);

            Intent intent = new Intent(INTENT_SPEED_CHANGED);
            int speedLimit = (int)(result.maxSpeedKph * 0.621371);
            intent.putExtra("Speed", curSpeed);
            intent.putExtra("SpeedLimit", speedLimit);
            sendBroadcast(new Intent(INTENT_SPEED_CHANGED));

            getLocation();
        }
    }

    public void setSpeedLimitView(TextView speedLimitView) {
        this.speedLimitView = speedLimitView;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private void updateSpeedLimit(Coordinates coords) {
        MyGeocodeListener listener = new MyGeocodeListener();
        ReverseGeocodeOptionalParameters params = new ReverseGeocodeOptionalParameters();
        params.type = ReverseGeocodeOptionalParameters.REVERSE_TYPE_NATIONAL;
        ReverseGeocoder.reverseGeocode(coords, params, listener, null);
        Log.d("HUD", "Updating things");
    }

    public void getLocation() {
        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(true);
        String provider = mlocManager.getBestProvider(criteria, true);
        Log.d("HUD", LocationManager.GPS_PROVIDER);
        Log.d("HUD", Double.toString(mlocManager.getProvider(provider).getAccuracy()));
        Log.d("HUD", Double.toString(mlocManager.getProvider(LocationManager.NETWORK_PROVIDER).getAccuracy()));
//        mlocManager.requestLocationUpdates(provider, 200, 0, mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, mlocListener);
    }

    @Override
    public void onCreate() {
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        SDKContext.setDeveloperKey("864stzx5n9senu3tbgb7ttqq");
        getLocation();
    }
}
