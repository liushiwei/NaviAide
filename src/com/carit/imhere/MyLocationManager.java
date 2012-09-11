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

import com.carit.imhere.provider.LocationTable;

/**
 * @author why
 */
public class MyLocationManager {
	private final String TAG = "MyLocationManager";
	private static Context mContext;
	private LocationManager gpsLocationManager;
	private LocationManager networkLocationManager;
	private static final int MINTIME = 5000;
	private static final int MININSTANCE = 0;
	private static MyLocationManager instance;
	private Location lastLocation = null;
	private static LocationCallBack mCallback;
	
	public static void init(Context c , LocationCallBack callback) {
		mContext = c;
		mCallback = callback;
	}

	
	private MyLocationManager() {
		// Gps 定位
		gpsLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		
		
		Location gpsLocation = gpsLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINTIME, MININSTANCE, locationListener);
        // 基站定位
		networkLocationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		Location networkLocation = gpsLocationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		networkLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, MINTIME, MININSTANCE,
				locationListener);
		
		String mocLocationProvider = LocationManager.NETWORK_PROVIDER;
		gpsLocationManager.addTestProvider(mocLocationProvider, false, false,
		false, false, true, true, true, 0, 5);
		gpsLocationManager.setTestProviderEnabled(mocLocationProvider, true);
		gpsLocationManager.requestLocationUpdates(mocLocationProvider, 0, 0, locationListener);
		Log.e(TAG, "network: "+networkLocation);
		if(gpsLocation!=null)
		    lastLocation = gpsLocation;
		else
		    lastLocation = networkLocation;
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
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "onLocationChanged");
			updateLocation(location);
			saveLocation(location);
		}
	};

	public Location getMyLocation() {
		return lastLocation;
	}
	
    private static int ENOUGH_LONG = 1000 * 60;	 
	
	public interface LocationCallBack{
		/**
		 * 当前位置
		 * @param location 
		 */
		void onCurrentLocation(Location location);
	}
	
	
	public void destoryLocationManager(){
		Log.d(TAG, "destoryLocationManager");
		gpsLocationManager.removeUpdates(locationListener);
		networkLocationManager.removeUpdates(locationListener);
	}
	
	private void saveLocation(Location location){
	    ContentValues values = new ContentValues();
	    values.put(LocationTable.TIME, location.getTime());
	    values.put(LocationTable.LAT, location.getLatitude());
	    values.put(LocationTable.LNG, location.getLongitude());
	    Uri uri = mContext.getContentResolver().insert(LocationTable.CONTENT_URI, values);
	}
}
