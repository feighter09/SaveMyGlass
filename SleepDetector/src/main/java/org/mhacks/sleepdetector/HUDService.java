package org.mhacks.sleepdetector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by Hephaestus on 9/21/13.
 */
public class HUDService extends Service {

    public static final String INTENT_SPEED_CHANGED = "org.mhacks.sleepdetector.INTENT_SPEED_CHANGED";

    Context ctx;
    MyLocationListener mlocListener = new MyLocationListener();
    LocationManager mlocManager;
    TextView speedView, speedLimitView;
    Float curSpeed = 0.0f;
    Location lastLoc;
    Long lastTime;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc == null) return;

            if (loc.hasSpeed())
                curSpeed = loc.getSpeed() * 2.23694f;
            else
                if (lastLoc != null)
                    curSpeed = lastLoc.distanceTo(loc) * 223.694f / (loc.getTime() - lastTime) ;

            lastLoc = loc;
            lastTime = loc.getTime();
            updateSpeedLimit(new Coordinates(loc.getLatitude(), loc.getLongitude()));
            Intent intent = new Intent(INTENT_SPEED_CHANGED);
            intent.putExtra("Speed", curSpeed);
            sendBroadcast(intent);

            Log.d("HUD", "Lat: " + Double.toString(loc.getLatitude()) + ", long: " + Double.toString(loc.getLongitude()));
            Log.d("HUD", "Speed: " + Float.toString(curSpeed));
            getLocation();

            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0)
                Log.d("HUD", "Address grabbed");
            else
                getDefaultWeather();

//            List<Address> list = geoCoder.getFromLocation(location
//                    .getLatitude(), location.getLongitude(), 1);
//            if (list != null & list.size() > 0) {
//                Address address = list.get(0);
//                result = address.getLocality();
//                return result;
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
                // to do finish this
            }

            Intent intent = new Intent(INTENT_SPEED_CHANGED);
            int speedLimit = result.maxSpeedKph;
            intent.putExtra("Speed", curSpeed);
            if (speedLimit == 0)
                speedLimit = 20;
            intent.putExtra("SpeedLimit", speedLimit);
            sendBroadcast(intent);

//            getLocation();
        }
    }

    public void setSpeedLimitView(TextView speedLimitView) {
        this.speedLimitView = speedLimitView;
    }

    public void setSpeedView(TextView speedView) {
        this.speedView = speedView;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private void updateSpeedLimit(Coordinates coords) {
        //doesn't work cuz tomtom isn't nearly as cool as I though
    }

    public void getLocation() {
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, mlocListener);
//        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, mlocListener);
    }

    public void getDefaultWeather() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://api.wunderground.com/api/508a609899cd13c9/alerts/q/MI/Ann_Arbor.json\n");

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = httpResponse.getEntity();
        String output = null;
        try {
            output = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("HUD", output);
    }

    @Override
    public void onCreate() {
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        SDKContext.setDeveloperKey("cheed25ehrx9k2uh3wvpzk67");
        getLocation();
    }
}
