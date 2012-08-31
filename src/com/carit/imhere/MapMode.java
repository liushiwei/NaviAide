
package com.carit.imhere;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.carit.imhere.MyLocationManager.LocationCallBack;
import com.carit.imhere.obj.Directions;
import com.carit.imhere.obj.Place;
import com.carit.imhere.obj.PlaceSearchResult;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;

public class MapMode extends MapActivity implements OnClickListener,LocationCallBack {
    private MapView mMapView;

    private MapController mMapController;

    private GeoPoint mGeoPoint;

    private LocationManager mLocationManager;
    
    private MyLocationManager mMyLocationManager;

    public static final int PARKING = 0x01;

    public static final int GAS_STATION = 0x02;

    public static final int FOOD = 0x03;

    public static final int CAR_REPAIR = 0x04;

    public static final int CAR_WASH = 0x05;

    public static final int BANK = 0x06;

    public static final int ATM = 0x07;

    public static final int HTTP_HTREAD_START = 0x101;

    public static final int HTTP_HTREAD_COMPLETE = 0x102;

    public static final int GET_PATH = 0x201;

    private int mRadius = 1000;

    private PlaceSearchResult mPlaces;

    private ParkingAdapter mAdapter;

    private List<GeoPoint> mPoints;
    
    private Location mOrigin;

    /**
     * 弹出的气泡View
     */
    private View mPopView;

    private ParkItemizedOverlay mOverlay;

    private PathOverlay mPathOverlay;

    private Drawable mPinDrawable;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            View view = null;
            switch (msg.what) {
                case HTTP_HTREAD_START:
                    view = findViewById(R.id.progress_loc);
                    view.setVisibility(View.VISIBLE);
                    break;
                case HTTP_HTREAD_COMPLETE:
                    view = findViewById(R.id.progress_loc);
                    view.setVisibility(View.GONE);
                    if (msg.arg1 != GET_PATH) {
                        View list = findViewById(R.id.placeSearchResult);
                        list.setVisibility(View.VISIBLE);
                        mAdapter.setResult(mPlaces);
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.e("MapMode", "HTTP_HTREAD_COMPLETE");
                    break;
            }
            super.handleMessage(msg);
        }

    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);
        mMapView = (MapView) findViewById(R.id.MapView01);
        // 设置为交通模式
        // mMapView.setTraffic(true);
        // 设置为卫星模式
        // mMapView.setSatellite(true);
        // 设置为街景模式
        // mMapView.setStreetView(true);
        // 取得MapController对象(控制MapView)
        mMapController = mMapView.getController();
        mMapView.setEnabled(true);
        mMapView.setClickable(true);
        // 设置地图支持缩放
        mMapView.setBuiltInZoomControls(true);
        mMapView.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        MyLocationManager.init(MapMode.this.getApplicationContext() , MapMode.this);
        mMyLocationManager = MyLocationManager.getInstance();
        
        mOrigin = mMyLocationManager.getMyLocation();
        if (mOrigin != null) {
            Log.e("MapMode", "get location location.getLatitude()=" + mOrigin.getLatitude()
                    + " location.getLongitude()=" + mOrigin.getLongitude());
            mGeoPoint = new GeoPoint((int) (mOrigin.getLatitude() * 1000000),
                    (int) (mOrigin.getLongitude() * 1000000));
        } else {
            mOrigin= new Location(LocationManager.NETWORK_PROVIDER);
            mOrigin.setLatitude(22.538928);
            mOrigin.setLongitude(113.994162);
            mGeoPoint = new GeoPoint((int) (22.538928 * 1000000), (int) (113.994162 * 1000000));
        }

        // 设置起点为 22.538928,113.994162
        // mGeoPoint = new GeoPoint((int) (22.538928 * 1000000), (int)
        // (113.994162 * 1000000));

        // 定位到深圳

        mMapController.animateTo(mGeoPoint);
        // 设置倍数(1-21)
        mMapController.setZoom(17);

        // 添加Overlay，用于显示标注信息
        MyLocationOverlay positionOverlay = new MyLocationOverlay(getBaseContext(), mMapView);
        positionOverlay.enableCompass();
        positionOverlay.enableMyLocation();
        

        List<Overlay> list = mMapView.getOverlays();

        list.add(positionOverlay);

        ToggleButton its = (ToggleButton) findViewById(R.id.ToggleButton_ITS);
        its.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMapView.setTraffic(true);
                } else {
                    mMapView.setTraffic(false);
                }
                mMapView.invalidate();

            }
        });
        ImageButton hotkey = (ImageButton) findViewById(R.id.ImageButtonHotkey);
        hotkey.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder;
                AlertDialog alertDialog;
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.hotkey_layout,
                        (ViewGroup) findViewById(R.id.hotkey));

                builder = new AlertDialog.Builder(mContext);
                builder.setView(layout);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        Intent intent = getIntent();
        processIntent(intent);
        ListView listView = (ListView) findViewById(R.id.placeSearchList);
        mAdapter = new ParkingAdapter(getBaseContext(), mPlaces, R.layout.place_list_item);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                mMapController.animateTo(mPoints.get(position));

                TextView title_TextView = (TextView) mPopView.findViewById(R.id.ImageButton01);
                title_TextView.setText(mPlaces.getResults()[position].getName());
                TextView desc_TextView = (TextView) mPopView.findViewById(R.id.TextView02);
                desc_TextView.setText(mPlaces.getResults()[position].getVicinity());
                mPopView.findViewById(R.id.ImageButtonRight).setTag(mPoints.get(position));
                MapView.LayoutParams params = (MapView.LayoutParams) mPopView.getLayoutParams();
                params.x = mPinDrawable.getBounds().centerX();// Y轴偏移
                params.y = -mPinDrawable.getBounds().height();// Y轴偏移
                params.point = mPoints.get(position);
                mMapView.updateViewLayout(mPopView, params);
                mPopView.setVisibility(View.VISIBLE);
            }

        });

        // 初始化气泡,并设置为不可见
        mPopView = View.inflate(this, R.layout.popup, null);
        mMapView.addView(mPopView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
                MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
        mPopView.setVisibility(View.GONE);
        ImageButton button = (ImageButton) mPopView.findViewById(R.id.ImageButtonRight);
        button.setOnClickListener(this);
        mPinDrawable = this.getResources().getDrawable(R.drawable.pin);
        // 为maker定义位置和边界
        mPinDrawable.setBounds(0, 0, mPinDrawable.getIntrinsicWidth(),
                mPinDrawable.getIntrinsicHeight());
        mOverlay = new ParkItemizedOverlay(mPinDrawable, this, mMapView, mPopView, mMapController);
        // 设置显示/隐藏气泡的监听器
        // mOverlay.setOnFocusChangeListener(onFocusChangeListener);
        Log.e("MapMode","network is open"+isOpen());

    }

    protected boolean isRouteDisplayed() {
        return false;
    }

    /**
     * [功能描述] 检查GPS的开关状态
     * 
     * @return [参数说明] true：开/false：关
     * @createTime 2011-4-14 下午01:27:06
     */
    private boolean isOpen() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:0"));
        try {
            PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
        String str = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.v("GPS", str);
        if (str != null) {
            return str.contains("network");
        } else {
            return false;
        }

    }
    
    

    @Override
    protected void onResume() {
      /*  //startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
        Thread thread =new Thread(){

            @Override
            public void run() {
                WiFiInfoManager manager = new WiFiInfoManager(getBaseContext());
                Location location = manager.getWIFILocation();
                location.setTime(System.currentTimeMillis());
                location.setAltitude(100);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER,
                        location);
                super.run();
            }
            
        };
        thread.start();
        */
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(22.538928);
        location.setLongitude(113.994162);
        location.setTime(System.currentTimeMillis());
        location.setAltitude(100);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER,
                location);
        super.onResume();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        switch (id) {
            case 0:
                return new AlertDialog.Builder(MapMode.this).setMessage("您确定要打开GPS吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                toggleGPS();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        }).create();
        }

        return super.onCreateDialog(id);
    }

    /**
     * 发送请求，打开GPS
     */
    private void toggleGPS() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
        } catch (CanceledException e) {
            e.printStackTrace();
        }
    }

    private void processIntent(Intent intent) {
        String type = null;
        int key = intent.getIntExtra("hotkey", -1);
        switch (key) {
            case PARKING:
                type = "parking";

                break;
            case GAS_STATION:
                type = "gas_station";
                break;
            case FOOD:
                type = "food";
                break;
            case BANK:
                type = "bank";
                break;
            case CAR_REPAIR:
                type = "car_repair";
                break;
            case CAR_WASH:
                type = "car_wash";
                break;

        }
        String url = "https://maps.googleapis.com/maps/api/place/search/json?location="
                + "22.538928,113.994162" + "&radius=" + mRadius + "&types=" + type
                + "&sensor=true&key=AIzaSyDbYqd7KvrZhqffpw4YfMsDreKgk9MuGJM&language=zh-CN";
        HttpThread thread = new HttpThread(url, new HttpThreadListener() {

            public void start() {
                mHandler.obtainMessage(HTTP_HTREAD_START).sendToTarget();

            }

            public void netError(String error) {
                // TODO Auto-generated method stub

            }

            public void complete(String result) {

                Gson gson = new Gson();
                mPlaces = gson.fromJson(result, PlaceSearchResult.class);
                if (mPlaces.getResults() != null) {
                    if (mPoints != null)
                        mPoints.clear();
                    else
                        mPoints = new ArrayList<GeoPoint>();

                    for (Place place : mPlaces.getResults()) {
                        GeoPoint point = new GeoPoint((int) (place.getGeometry().getLocation()
                                .getLat() * 1000000), (int) (place.getGeometry().getLocation()
                                .getLng() * 1000000));
                        mPoints.add(point);
                        OverlayItem overlayItem = new OverlayItem(point, place.getName(),
                                place.getVicinity());
                        mOverlay.addOverlay(overlayItem);

                    }
                }

                Log.e("MapMode", mPlaces.getStatus());
                mMapView.getOverlays().add(mOverlay);
                mHandler.obtainMessage(HTTP_HTREAD_COMPLETE).sendToTarget();
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getTag() != null) {
            String origin = mOrigin.getLatitude()+","+mOrigin.getLongitude();
            GeoPoint point = (GeoPoint) v.getTag();
            String lat = Integer.toString(point.getLatitudeE6());
            String lng = Integer.toString(point.getLongitudeE6());
            String destination = lat.substring(0, lat.length() - 6) + "."
                    + lat.substring(lat.length() - 6) + "," + lng.substring(0, lng.length() - 6)
                    + "." + lng.substring(lng.length() - 6);
            String url = "http://maps.google.com/maps/api/directions/json?origin="+origin
                    + "&destination=" + destination + "&sensor=false&mode=driving";

            HttpThread thread = new HttpThread(url, new HttpThreadListener() {

                public void start() {
                    mHandler.obtainMessage(HTTP_HTREAD_START).sendToTarget();

                }

                public void netError(String error) {
                    // TODO Auto-generated method stub

                }

                public void complete(String result) {

                    Gson gson = new Gson();
                    Directions directions = gson.fromJson(result, Directions.class);
                    if (directions.getRoutes() != null) {
                        /*
                         * for (Step step :
                         * directions.getRoutes()[0].getLegs()[0].getSteps()) {
                         * GeoPoint point = new GeoPoint( (int)
                         * (step.getStart_location().getLat() * 1000000), (int)
                         * (step.getStart_location().getLng() * 1000000));
                         * mPoints.add(point); point = new GeoPoint( (int)
                         * (step.getEnd_location().getLat() * 1000000), (int)
                         * (step .getEnd_location().getLng() * 1000000));
                         * mPoints.add(point); }
                         */
                        if (mPathOverlay == null) {
                            mPathOverlay = new PathOverlay(decodePoly(directions.getRoutes()[0]
                                    .getOverview_polyline().getPoints()));
                            mMapView.getOverlays().add(0,mPathOverlay);
                        } else {
                            mPathOverlay.setPoints(decodePoly(directions.getRoutes()[0]
                                    .getOverview_polyline().getPoints()));
                        }

                    }

                    Log.e("MapMode", directions.getStatus());

                    mHandler.obtainMessage(HTTP_HTREAD_COMPLETE, GET_PATH, 0).sendToTarget();
                }
            });
            thread.start();
        }
    }

    private List<GeoPoint> decodePoly(String encoded) {

        List<GeoPoint> poly = new ArrayList<GeoPoint>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
                    (int) (((double) lng / 1E5) * 1E6));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onCurrentLocation(Location location) {
        ;
        if (location != null) {
            Log.i("SuperMap", "Location changed :provider"+location.getProvider()+" Lat: " + location.getLatitude()
                    + " Lng: " + location.getLongitude());
            mMapController.animateTo(new GeoPoint(
                    (int) (location.getLatitude() * 1000000), (int) (location
                            .getLongitude() * 1000000)));
            mOrigin = location;
        }
        
    }

}
