
package com.carit.imhere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.carit.imhere.MyLocationManager.LocationCallBack;
import com.carit.imhere.NaviAideServiceAidl.Stub;
import com.carit.imhere.provider.LocationTable;
import com.carit.imhere.test.MockProvider;
import com.map.projection.Projection;

public class NaviAideService extends Service implements LocationCallBack {

    private static final String TAG = "NaviAideService";

    private MyBinder mBinder = new MyBinder();

    private ServiceCallBack mCallBack;

    private MyLocationManager mMyLocationManager;

    private Location mLastLocation;

    private boolean isStart;

    private boolean isTrack;

    public static final int START_TRACK = 0;

    public static final int STOP_TRACK = 1;

    public static final int BOOT_COMPLETED = 2;

    @Override
    public void onStart(Intent intent, int startId) {
        if (!isStart) {
            Log.e(TAG, "start first");
            MyLocationManager.init(NaviAideService.this.getApplicationContext(),
                    NaviAideService.this);
            mMyLocationManager = MyLocationManager.getInstance();
            projectionThread.start();
            isStart = true;
        }
        if (mLastLocation != null)
            MockProvider.getInstance().setLocation(mLastLocation);
        int from = intent.getIntExtra("from", -1);
        Log.e(TAG, "start service from " + from);
        switch (from) {
            case START_TRACK:
                isTrack = true;
                break;
            case STOP_TRACK:
                isTrack = false;
                break;
            case BOOT_COMPLETED:
                SharedPreferences user = getSharedPreferences("LocationTrack", 0);
                isTrack = user.getBoolean("reboot_track", false);
                // = user.getString("",””);
                // strPassword = user getString(“PASSWORD”,””);
                break;

        }
        // NotificationManager nm =
        // (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (isTrack) {
            Notification n = new Notification(R.drawable.icon_notification, "", System.currentTimeMillis());
            n.flags = Notification.FLAG_FOREGROUND_SERVICE;
            Intent i = new Intent(getBaseContext(), RouteActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // PendingIntent
            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                    R.string.route_logger, i, PendingIntent.FLAG_UPDATE_CURRENT);

            n.setLatestEventInfo(getBaseContext(), getString(R.string.route_logger),
                    getString(R.string.logging), contentIntent);
            // nm.notify(R.string.route_logger, n);
            startForeground(R.string.route_logger, n);
        } else {
            stopForeground(true);
            // nm.cancel(R.string.route_logger);
        }
        // super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onUnbind");
        if (intent.getBooleanExtra("remote", false))
            return mAidlBinder;
        else
            return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (mMyLocationManager != null)
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

    private NaviAideServiceAidl.Stub mAidlBinder = new Stub() {

        @Override
        public void setTrack(boolean isTrack) throws RemoteException {
            NaviAideService.this.isTrack = isTrack;
        }

        @Override
        public boolean isTrack() throws RemoteException {
            return NaviAideService.this.isTrack;
        }
    };

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
            double[] latlng = Projection.adjustLatLng(location.getLatitude(),
                    location.getLongitude());
            Location loc = new Location(MockProvider.MODK_PROVIDER);
            loc.setLatitude(latlng[0]);
            loc.setLongitude(latlng[1]);
            loc.setTime(System.currentTimeMillis());
            loc.setAltitude(100);
            mLastLocation = loc;
            MockProvider.getInstance().setLocation(loc);
            if (mCallBack != null)
                mCallBack.onLocationChange(loc);
            if (isTrack) {
                Log.e(TAG, "save track");
                saveLocation(loc);
            }
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

    public Location getLastLocation() {
        return mLastLocation;
    }

    public boolean isTrack() {
        return isTrack;
    }

    public void setTrack(boolean isTrack) {
        this.isTrack = isTrack;
    }

}
