
package com.carit.imhere;

/**
 * @author rongfzh
 * @version 1.0.0  
 */
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.carit.imhere.provider.LocationTable;
import com.carit.imhere.test.MockProvider;

/**
 * @author why
 */
public class MyLocationManager {
    private final String TAG = "MyLocationManager";

    private static Context mContext;

    private LocationManager mLocationManager;

    private static final int MINTIME = 2000;

    private static final int MININSTANCE = 1;

    private static MyLocationManager instance;

    private Location lastLocation = null;

    private static LocationCallBack mCallback;

    public static void init(Context c, LocationCallBack callback) {
        mContext = c;
        mCallback = callback;
    }

    private MyLocationManager() {
		// Gps 定位
		mLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		
		Location gpsLocation = mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINTIME, MININSTANCE, locationListener);
//        // 基站定位
//		if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//		    Toast.makeText(mContext, "NETWORK is enable", Toast.LENGTH_LONG).show();
//		}
		
		Location networkLocation = mLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MINTIME, MININSTANCE,
				locationListener);
		
		Log.e(TAG, "network: "+networkLocation);
		
//		if(gpsLocation!=null)
//		    lastLocation = gpsLocation;
//		else
//		    lastLocation = networkLocation;
		
		 MockProvider.getInstance().init(
	                (LocationManager) mContext.getSystemService(
	                        Context.LOCATION_SERVICE),locationListener);
	}

    public static MyLocationManager getInstance() {
        if (null == instance) {
            instance = new MyLocationManager();
        }
        return instance;
    }

    private void updateLocation(Location location) {
        lastLocation = location;
        mCallback.onCurrentLocation(location);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged provider =" + provider + " status=" + status);
        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.d(TAG, "onProviderEnabled provider =" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled provider =" + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            if(LocationManager.GPS_PROVIDER.equals(location.getProvider())){
                
                mCallback.onCurrentLocation(location);
            }

        }
    };

    public Location getMyLocation() {
        return lastLocation;
    }

    private static int ENOUGH_LONG = 1000 * 60;

    public interface LocationCallBack {
        /**
         * 当前位置
         * 
         * @param location
         */
        void onCurrentLocation(Location location);
    }

    public void destoryLocationManager() {
        Log.d(TAG, "destoryLocationManager");
        mLocationManager.removeUpdates(locationListener);
    }

  
}
