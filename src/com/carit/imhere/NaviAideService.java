
package com.carit.imhere;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.carit.imhere.MyLocationManager.LocationCallBack;
import com.carit.imhere.provider.LocationTable;
import com.carit.imhere.test.MockProvider;
import com.map.projection.Projection;;;

public class NaviAideService extends Service implements LocationCallBack {

    private static final String TAG = "NaviAideService";

    private MyBinder mBinder = new MyBinder();

    private ServiceCallBack mCallBack;

    private MyLocationManager mMyLocationManager;

    @Override
    public void onStart(Intent intent, int startId) {
        MyLocationManager.init(NaviAideService.this.getApplicationContext(), NaviAideService.this);
        mMyLocationManager = MyLocationManager.getInstance();
        projectionThread.start();
       
        //super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mMyLocationManager.destoryLocationManager();
        super.onDestroy();
    }

    public ServiceCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(ServiceCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public class MyBinder extends Binder {
        NaviAideService getService() {
            return NaviAideService.this;
        }
    }

    Thread projectionThread = new Thread() {

        @Override
        public void run() {
            Projection.init();
            super.run();
        }

    };

    public interface ServiceCallBack {
        /**
         * 当前位置
         * 
         * @param location
         */
        void onLocationChange(Location location);
    }

    @Override
    public void onCurrentLocation(Location location) {
        if (Projection.isInit()) {
            double [] latlng =Projection.adjustLatLng(location.getLatitude(), location.getLongitude());
            Location loc = new Location(MockProvider.MODK_PROVIDER);
            loc.setLatitude(latlng[0]);
            loc.setLongitude(latlng[1]);
            loc.setTime(System.currentTimeMillis());
            loc.setAltitude(100);
            MockProvider.getInstance().setLocation(loc);
            if (mCallBack != null)
                mCallBack.onLocationChange(loc);
            Log.e(TAG, "not set");
            
            saveLocation(loc);
        }

    }

    private void saveLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(LocationTable.TIME, location.getTime());
        values.put(LocationTable.LAT, location.getLatitude());
        values.put(LocationTable.LNG, location.getLongitude());
        Uri uri = this.getApplicationContext().getContentResolver()
                .insert(LocationTable.CONTENT_URI, values);
    }

}
