
package com.carit.imhere;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.carit.imhere.NaviAideService.ServiceCallBack;
import com.carit.imhere.obj.Directions;
import com.carit.imhere.obj.Place;
import com.carit.imhere.obj.PlaceSearchResult;
import com.carit.imhere.obj.Step;
import com.carit.imhere.provider.LocationTable;
import com.carit.imhere.test.MockProvider;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;

public class MapMode extends MapActivity implements OnClickListener, ServiceCallBack {

    public static final String TAG = "MapMode";

    private MapView mMapView;

    private MapController mMapController;

    private GeoPoint mGeoPoint;

    public static final int PARKING = 0x01;

    public static final int GAS_STATION = 0x02;

    public static final int FOOD = 0x03;

    public static final int CAR_REPAIR = 0x04;

    public static final int CAR_WASH = 0x05;

    public static final int BANK = 0x06;

    public static final int ATM = 0x07;

    public static final int SPORT = 0x08;

    public static final int CAFE = 0x09;

    public static final int CAR_DEALER = 0x10;

    public static final int CAR_ROUTE = 0x11;

    public static final int HTTP_HTREAD_START = 0x101;

    public static final int HTTP_HTREAD_COMPLETE = 0x102;

    public static final int NOPOINTS = 0x103;

    public static final int NETWORKERROR = 0x104;
    
    public static final int NOTRACK = 0x105;
    
    public static final int NOTGETLOCATION = 0x106;

    public static final int GETLOCATION = 0x107;
    
    public static final int GET_PATH = 0x201;

    private int mRadius = 1000;

    private PlaceSearchResult mPlaces;

    private ParkingAdapter mAdapter;

    private List<GeoPoint> mPoints;

    private Location mOrigin;

    private GeoPoint mDestination;

    private String mtypes;

    private int mViewType;
    //private SMS_Receiver mSMSRec;

    /**
     * 弹出的气泡View
     */
    private View mPopView;

    private View mPopNoBtnView;

    private ParkItemizedOverlay mOverlay;

    private PathOverlay mPathOverlay;

    private TrackOverlay mTrackOverlay;

    private Drawable mPinDrawable;

    private Directions mDirections;

    private Drawable mPathPinDrawable;

    private Drawable mStartPinDrawable;

    private Drawable mPassPinDrawable;

    private boolean mIsPause;

    private LongPressOverlay mLongPressOverlay;

    private MyLocationOverlay mMylocationOverlay;
    
    private RailOverlay mRailoverlay;
    
    protected NaviAideService mMyService;

    private ProgressDialog mProgDialog;

   
    
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
                        findViewById(R.id.show).setOnClickListener(MapMode.this);
                        findViewById(R.id.hide).setOnClickListener(MapMode.this);
                        View list = findViewById(R.id.placeSearchResult);
                        list.setVisibility(View.VISIBLE);
                        mAdapter.setResult(mPlaces);
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.e("MapMode", "HTTP_HTREAD_COMPLETE");
                    break;

                case NOPOINTS:
                    findViewById(R.id.progress_loc).setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), R.string.no_poi, Toast.LENGTH_LONG).show();
                    break;
                case NETWORKERROR:
                    findViewById(R.id.progress_loc).setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_LONG)
                            .show();
                    break;
                case NOTRACK:
                    Toast.makeText(getBaseContext(), R.string.no_track, Toast.LENGTH_LONG).show();
                    break;
                case NOTGETLOCATION:
                    Toast.makeText(getBaseContext(), R.string.not_get_location, Toast.LENGTH_LONG).show();
                    break;
                case GETLOCATION:
                    mProgDialog.dismiss();
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

//        mOrigin = new Location(LocationManager.NETWORK_PROVIDER);
//        mOrigin.setLatitude(22.541949);
//        mOrigin.setLongitude(113.989629);
        //mGeoPoint = new GeoPoint((int) (22.541949 * 1000000), (int) (113.989629 * 1000000));

        // 定位到深圳

        //mMapController.animateTo(mGeoPoint);
        // 设置倍数(1-21)
        mMapController.setZoom(18);

       
        //Log.e("MapMode", "network is open" + isOpen());
        processIntent(getIntent());

//        mSMSRec = new SMS_Receiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        this.registerReceiver(mSMSRec, filter);

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
        // Intent gpsIntent = new Intent();
        // gpsIntent.setClassName("com.android.settings",
        // "com.android.settings.widget.SettingsAppWidgetProvider");
        // gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        // gpsIntent.setData(Uri.parse("custom:0"));
        // try {
        // PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
        // } catch (CanceledException e) {
        // e.printStackTrace();
        // }
        String str = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Log.v("MapMode", "available providers :" + str);
        if (str != null) {
            return str.contains("network");
        } else {
            return false;
        }

    }

    @Override
    protected void onPause() {
        if(CAR_ROUTE!=mViewType){
        mMylocationOverlay.disableMyLocation();
        mMylocationOverlay.disableCompass();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(CAR_ROUTE!=mViewType){
        mMylocationOverlay.enableCompass();
        mMylocationOverlay.enableMyLocation();
        }
//         Location location = new Location(MockProvider.MODK_PROVIDER);
//         location.setLatitude(22.538928);
//         location.setLongitude(113.994162);
//         location.setTime(System.currentTimeMillis());
//         location.setAltitude(100);
//
//         MockProvider.getInstance()
//         .init((LocationManager) getSystemService(Context.LOCATION_SERVICE))
//         .setLocation(location);
        // MockProvider.getInstance().init((LocationManager)
        Intent i = new Intent();
        i.setClass(this, NaviAideService.class);
        startService(i);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    /*
     * @Override protected Dialog onCreateDialog(int id) { // TODO
     * Auto-generated method stub switch (id) { case 0: return new
     * AlertDialog.Builder(MapMode.this).setMessage("您确定要打开GPS吗？")
     * .setPositiveButton("确定", new DialogInterface.OnClickListener() { public
     * void onClick(DialogInterface dialog, int which) { // TODO Auto-generated
     * method stub toggleGPS(); } }).setNegativeButton("取消", new
     * DialogInterface.OnClickListener() { public void onClick(DialogInterface
     * dialog, int which) { // TODO Auto-generated method stub } }).create(); }
     * return super.onCreateDialog(id); }
     */
    private void init(){
        // 添加Overlay，用于显示标注信息
//        mMapView.setOnTouchListener(new OnTouchListener() {
//            
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.e(TAG, "x="+event.getX()+",y="+event.getY()+" code="+event.getAction());
//                if(MotionEvent.ACTION_DOWN==event.getAction()){
//                    mRailoverlay.setLeftTop(new Point((int)event.getX(),(int)event.getY()));
//                    mRailoverlay.setRightBelow(new Point((int)event.getX(),(int)event.getY()));
//                }else{
//                    mRailoverlay.setRightBelow(new Point((int)event.getX(),(int)event.getY()));
//                }
//                mMapView.invalidate();
//                return true;
//            }
//        });
        mProgDialog = ProgressDialog.show(MapMode.this, null, getString(R.string.getting_location), true, false);
        mProgDialog.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(KeyEvent.KEYCODE_BACK==keyCode)
                    MapMode.this.finish();
                return false;
            }
        });
        mMylocationOverlay = new MyLocationOverlay(getBaseContext(), mMapView);
        mMylocationOverlay.runOnFirstFix(new Thread() {

            @Override
            public void run() {
                Log.e(TAG, "runOnFirstFix lat="
                        + mMylocationOverlay.getMyLocation().getLatitudeE6() + " lng="
                        + mMylocationOverlay.getMyLocation().getLongitudeE6());
                mMapController.animateTo(mMylocationOverlay.getMyLocation());
                if(mOrigin!=null){
                    mOrigin.setLatitude(mMylocationOverlay.getMyLocation().getLatitudeE6()/1E6);
                    mOrigin.setLongitude(mMylocationOverlay.getMyLocation().getLongitudeE6()/1E6);
                }else{
                    //mOrigin = 
                    mOrigin = mMylocationOverlay.getLastFix();
                }
                if (mtypes != null) {
                    mHandler.sendEmptyMessageDelayed(GETLOCATION, 500);
                    queryPoi(null, mtypes);
                }
                super.run();
            }

        });
        List<Overlay> list = mMapView.getOverlays();

        //电子栅栏
//        mRailoverlay = new RailOverlay();
//        list.add(mRailoverlay);
        list.add(mMylocationOverlay);

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
        //findViewById(R.id.ImageButtonAR).setOnClickListener(this);
        
        findViewById(R.id.ImageButtonHotkey).setOnClickListener(this);

        

        // 初始化气泡,并设置为不可见
        mPopView = View.inflate(this, R.layout.popup, null);
        mMapView.addView(mPopView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
                MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
        mPopView.setVisibility(View.GONE);

        mPopNoBtnView = View.inflate(this, R.layout.popup_nobtn, null);
        mMapView.addView(mPopNoBtnView, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
                MapView.LayoutParams.WRAP_CONTENT, null, MapView.LayoutParams.BOTTOM_CENTER));
        mPopNoBtnView.setVisibility(View.GONE);
        ImageButton button = (ImageButton) mPopView.findViewById(R.id.ImageButtonRight);
        button.setOnClickListener(this);
        mPinDrawable = this.getResources().getDrawable(R.drawable.pin);
        // 为maker定义位置和边界
        mPinDrawable.setBounds(0, 0, mPinDrawable.getIntrinsicWidth(),
                mPinDrawable.getIntrinsicHeight());
        mPathPinDrawable = getResources().getDrawable(R.drawable.pin_orange);
        mPathPinDrawable.setBounds(0, 0, mPathPinDrawable.getIntrinsicWidth(),
                mPathPinDrawable.getIntrinsicHeight());
        mOverlay = new ParkItemizedOverlay(mPinDrawable, this, mMapView, mPopView, mMapController);
        // 设置显示/隐藏气泡的监听器
        // mOverlay.setOnFocusChangeListener(onFocusChangeListener);
        mPassPinDrawable = getResources().getDrawable(R.drawable.pin_purple);
        mLongPressOverlay = new LongPressOverlay(this, mMapView, mMapController, mPassPinDrawable);
        list.add(mLongPressOverlay);
        findViewById(R.id.ImageButtonMyloc);
    }
    /**
     * 发送请求，打开GPS
     */
    /*
     * private void toggleGPS() { Intent gpsIntent = new Intent();
     * gpsIntent.setClassName("com.android.settings",
     * "com.android.settings.widget.SettingsAppWidgetProvider");
     * gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
     * gpsIntent.setData(Uri.parse("custom:3")); try {
     * PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send(); } catch
     * (CanceledException e) { e.printStackTrace(); } }
     */
    private void processIntent(Intent intent) {
        mtypes = null;
        mViewType = intent.getIntExtra("hotkey", -1);
        switch (mViewType) {
            case PARKING:
                mtypes = "parking";
                break;
            case GAS_STATION:
                mtypes = "gas_station";
                break;
            case FOOD:
                mtypes = "food";
                break;
            case BANK:
                mtypes = "bank";
                break;
            case CAR_REPAIR:
                mtypes = "car_repair";
                break;
            case CAR_DEALER:
                mtypes = "car_dealer";
                break;
            case CAFE:
                mtypes = "cafe";
                break;
            case SPORT:
                mtypes = "sport";
                break;
            case CAR_ROUTE:
                findViewById(R.id.trackControl).setVisibility(View.VISIBLE);
                findViewById(R.id.LinearLayoutMapSearch).setVisibility(View.GONE);
                findViewById(R.id.ToggleButton_ITS).setVisibility(View.GONE);
                findViewById(R.id.pause).setOnClickListener(this);
                findViewById(R.id.play).setOnClickListener(this);
                mPathPinDrawable = getResources().getDrawable(R.drawable.pin_orange);
                mPathPinDrawable.setBounds(0, 0, mPathPinDrawable.getIntrinsicWidth(),
                        mPathPinDrawable.getIntrinsicHeight());
                queryPoint(intent.getLongExtra("start_time", 0), intent.getLongExtra("end_time", 0));
                break;

        }
        if(CAR_ROUTE!=mViewType){
            init();
        }
        
    }

    private void queryPoi(String keyWord, String type) {
        String url = null;
        if(mOrigin == null){
            mHandler.sendEmptyMessage(NOTGETLOCATION);
            return;
        }
        String origin = mOrigin.getLatitude() + "," + mOrigin.getLongitude();
        if (keyWord == null)
            url = "https://maps.googleapis.com/maps/api/place/search/json?location="
                    + origin + "&radius=" + mRadius + "&types=" + type
                    + "&sensor=true&key=AIzaSyDbYqd7KvrZhqffpw4YfMsDreKgk9MuGJM&language=zh-CN";
        else
            url = "https://maps.googleapis.com/maps/api/place/search/json?location="
                    + origin + "&radius=" + mRadius + "&types=" + type + "&name="
                    + keyWord
                    + "&sensor=true&key=AIzaSyDbYqd7KvrZhqffpw4YfMsDreKgk9MuGJM&language=zh-CN";
        HttpThread thread = new HttpThread(url, new HttpThreadListener() {

            public void start() {
                mHandler.sendEmptyMessage(HTTP_HTREAD_START);

            }

            public void netError(String error) {

                mHandler.sendEmptyMessage(NETWORKERROR);
            }

            public void complete(String result) {

                Gson gson = new Gson();
                mPlaces = gson.fromJson(result, PlaceSearchResult.class);
                if (mPlaces.getResults() != null) {
                    if (mPoints != null)
                        mPoints.clear();
                    else
                        mPoints = new ArrayList<GeoPoint>();
                    mOverlay.cleanOverlayItem();
                    for (Place place : mPlaces.getResults()) {
                        GeoPoint point = new GeoPoint((int) (place.getGeometry().getLocation()
                                .getLat() * 1000000), (int) (place.getGeometry().getLocation()
                                .getLng() * 1000000));
                        mPoints.add(point);
                        OverlayItem overlayItem = new OverlayItem(point, place.getName(),
                                place.getVicinity());
                        mOverlay.addOverlay(overlayItem);

                    }
                    Log.e("MapMode", mPlaces.getStatus());
                    if (!mMapView.getOverlays().contains(mOverlay))
                        mMapView.getOverlays().add(mOverlay);
                    mHandler.obtainMessage(HTTP_HTREAD_COMPLETE).sendToTarget();
                }else{
                    mHandler.sendEmptyMessage(NOPOINTS);
                }

            }
        },false);
        thread.start();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (v.getTag() != null) {
            getPath((GeoPoint) v.getTag(), null);
        } else {
            switch (v.getId()) {
                case R.id.hide:
                    findViewById(R.id.placeSearchResult).setVisibility(View.GONE);
                    findViewById(R.id.show).setVisibility(View.VISIBLE);
                    break;
                case R.id.show:
                    findViewById(R.id.placeSearchResult).setVisibility(View.VISIBLE);
                    findViewById(R.id.show).setVisibility(View.GONE);
                    break;
                case R.id.ImageButtonAR:
                    try {
                        Bitmap bm = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bm);
                        mMapView.draw(canvas);
                        bm.compress(CompressFormat.JPEG, 95, new FileOutputStream(
                                "/sdcard/media/image.jpg"));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
                case R.id.pause:
                    mIsPause = true;
                    break;
                case R.id.play:
                    mIsPause = false;
                    break;
                case R.id.ImageButtonHotkey:
                    EditText text = (EditText) findViewById(R.id.TextViewSearch);
                    String keyWord = text.getText().toString().trim().replaceAll(" ", "|");
                    queryPoi(keyWord, mtypes);
                    break;
            }
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

    public void getPath(GeoPoint point, GeoPoint[] passPoint) {
        if(mOrigin == null){
            mHandler.sendEmptyMessage(NOTGETLOCATION);
            return;
        }
        if (mPopNoBtnView != null && mPopNoBtnView.isShown()) {
            mPopNoBtnView.setVisibility(View.GONE);
        }
        String origin = mOrigin.getLatitude() + "," + mOrigin.getLongitude();
        mDestination = point;
        String lat = Integer.toString(mDestination.getLatitudeE6());
        String lng = Integer.toString(mDestination.getLongitudeE6());
        String destination = lat.substring(0, lat.length() - 6) + "."
                + lat.substring(lat.length() - 6) + "," + lng.substring(0, lng.length() - 6) + "."
                + lng.substring(lng.length() - 6);
        String url = null;
        if (passPoint == null) {
            url = "http://maps.google.com/maps/api/directions/json?origin=" + origin
                    + "&destination=" + destination + "&sensor=false&mode=driving";
        } else {
            String waypoints = "";
            for (GeoPoint pass : passPoint) {
                waypoints += pass.getLatitudeE6() / 1E6 + "," + pass.getLongitudeE6() / 1E6 + "|";
            }
            waypoints = waypoints.substring(0, waypoints.length() - 2);
            try {
                waypoints = URLEncoder.encode(waypoints, "UTF-8");
                Log.e("ParkingOverlay", "waypoints = " + waypoints);
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            url = "http://maps.google.com/maps/api/directions/json?origin=" + origin
                    + "&destination=" + destination + "&sensor=false&mode=driving&waypoints="
                    + waypoints;
        }
        HttpThread thread = new HttpThread(url, new HttpThreadListener() {

            public void start() {
                mHandler.obtainMessage(HTTP_HTREAD_START).sendToTarget();

            }

            public void netError(String error) {
                mHandler.obtainMessage(NETWORKERROR).sendToTarget();

            }

            public void complete(String result) {

                Gson gson = new Gson();
                mDirections = gson.fromJson(result, Directions.class);
                if (mDirections != null && mDirections.getRoutes() != null) {
                    /*
                     * for (Step step :
                     * directions.getRoutes()[0].getLegs()[0].getSteps()) {
                     * GeoPoint point = new GeoPoint( (int)
                     * (step.getStart_location().getLat() * 1000000), (int)
                     * (step.getStart_location().getLng() * 1000000));
                     * mPoints.add(point); point = new GeoPoint( (int)
                     * (step.getEnd_location().getLat() * 1000000), (int) (step
                     * .getEnd_location().getLng() * 1000000));
                     * mPoints.add(point); }
                     */
                    List<GeoPoint> points = decodePoly(mDirections.getRoutes()[0]
                            .getOverview_polyline().getPoints());
                    points.add(
                            0,
                            new GeoPoint((int) (mOrigin.getLatitude() * 1E6), (int) (mOrigin
                                    .getLongitude() * 1E6)));
                    points.add(mDestination);
                    // MockProvider.generateGpsFile(points);
                    if (mPathOverlay == null) {

                        mStartPinDrawable = getResources().getDrawable(R.drawable.icon_nav_start);
                        mStartPinDrawable.setBounds(0 - mStartPinDrawable.getIntrinsicWidth() / 2,
                                0 - mStartPinDrawable.getIntrinsicHeight(),
                                mStartPinDrawable.getIntrinsicWidth() / 2, 0);
                        mPathOverlay = new PathOverlay(points, mMapView, mPopNoBtnView,
                                mMapController, mPathPinDrawable);
                        mPathOverlay.getOverlays().clear();
                        OverlayItem overlayItem = new OverlayItem(points.get(0),
                                getString(R.string.full)
                                        + mDirections.getRoutes()[0].getLegs()[0].getDistance()
                                                .getText(), getString(R.string.take)
                                        + mDirections.getRoutes()[0].getLegs()[0].getDuration()
                                                .getText());
                        overlayItem.setMarker(mStartPinDrawable);
                        mPathOverlay.addOverlay(overlayItem);
                        // OverlayItem overlayItem=null;
                        for (Step step : mDirections.getRoutes()[0].getLegs()[0].getSteps()) {
                            overlayItem = new OverlayItem(new GeoPoint((int) (step
                                    .getStart_location().getLat() * 1E6), (int) (step
                                    .getStart_location().getLng() * 1E6)),
                                    step.getHtml_instructions(), step.getDistance().getText() + "-"
                                            + step.getDuration().getText());
                            mPathOverlay.addOverlay(overlayItem);
                        }

                        mMapView.getOverlays().add(mPathOverlay);
                    } else {
                        
                        mPathOverlay.setPoints(decodePoly(mDirections.getRoutes()[0]
                                .getOverview_polyline().getPoints()));
                        mPathOverlay.getPoints().add(0,new GeoPoint((int) (mOrigin.getLatitude() * 1E6), (int) (mOrigin
                                        .getLongitude() * 1E6)));
                        mPathOverlay.getPoints().add(mDestination);
                        mPathOverlay.getOverlays().clear();
                        OverlayItem overlayItem = new OverlayItem(points.get(0),
                                getString(R.string.full)
                                        + mDirections.getRoutes()[0].getLegs()[0].getDistance()
                                                .getText(), getString(R.string.take)
                                        + mDirections.getRoutes()[0].getLegs()[0].getDuration()
                                                .getText());
                        overlayItem.setMarker(mStartPinDrawable);
                        mPathOverlay.addOverlay(overlayItem);
                        for (Step step : mDirections.getRoutes()[0].getLegs()[0].getSteps()) {
                            overlayItem = new OverlayItem(new GeoPoint((int) (step
                                    .getStart_location().getLat() * 1E6), (int) (step
                                    .getStart_location().getLng() * 1E6)),
                                    step.getHtml_instructions(), step.getDistance().getText() + "-"
                                            + step.getDuration().getText());
                            mPathOverlay.addOverlay(overlayItem);
                        }
                    }
                    mMapController.animateTo(points.get(0));

                    Log.e("MapMode", mDirections.getStatus());
                }

                mHandler.obtainMessage(HTTP_HTREAD_COMPLETE, GET_PATH, 0).sendToTarget();

            }
        },false);
        thread.start();

    }

    private void queryPoint(long startTime, long endTime) {

        new TrackThread(startTime, endTime).start();
    }

    private List<Location> getPoints(Cursor cur) {

        List<Location> mLocations = new ArrayList<Location>();
        if (cur.moveToFirst()) {

            int lat = cur.getColumnIndex(LocationTable.LAT);
            int lng = cur.getColumnIndex(LocationTable.LNG);

            do {
                // Get the field values

                Location location = new Location(LocationManager.NETWORK_PROVIDER);
                location.setLatitude(Double.valueOf(cur.getString(lat)));
                location.setLongitude(Double.valueOf(cur.getString(lng)));
                mLocations.add(location);

            } while (cur.moveToNext());

        }
        return mLocations;
    }

    class TrackThread extends Thread {
        private long start;

        private long end;

        public TrackThread(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(LocationTable.CONTENT_URI, null, " time > " + start
                    + " and time < " + end, null, LocationTable.DEFAULT_SORT_ORDER);
            if (cur.getCount() > 2) {
                List<Location> lists = getPoints(cur);
                if (lists.size() > 2) {
                    for (int i = 0; i < lists.size(); i++) {
                        if (i < 1) {
                            mTrackOverlay = new TrackOverlay(lists.get(i), mMapView, mPopNoBtnView,
                                    mMapController, mPathPinDrawable);
                            mMapView.getOverlays().add(mTrackOverlay);
                        } else {
                            try {
                                sleep(1000);
                                if (!mIsPause)
                                    mTrackOverlay.addLocation(lists.get(i));
                                else
                                    i--;
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }
                }
            } else {
                mHandler.sendEmptyMessage(NOTRACK);
            }
            super.run();
        }

    };

  /*  public class SMS_Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // #navi#|22534717,113987961|
            Log.e("SMS_Receiver", "收到短信");
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                // 不知道为什么明明只有一条消息，传过来的却是数组，也许是为了处理同时同分同秒同毫秒收到多条短信
                // 但这个概率有点小
                SmsMessage[] message = new SmsMessage[pdus.length];
                StringBuilder sb = new StringBuilder();
                Log.e("SMS_Receiver", "pdus长度" + pdus.length);
                for (int i = 0; i < pdus.length; i++) {
                    // 虽然是循环，其实pdus长度一般都是1
                    message[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append("接收到短信来自:\n");
                    sb.append(message[i].getDisplayOriginatingAddress() + "\n");
                    sb.append("内容:" + message[i].getDisplayMessageBody());
                    String body = message[i].getDisplayMessageBody();
                    if (body.contains("#navi#")) {
                        String point = body.substring(body.indexOf("|") + 1,
                                body.indexOf("|", body.indexOf("|") + 1));
                        String[] tmp = point.split(",");
                        OverlayItem overlayItem = new OverlayItem(new GeoPoint(
                                Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1])), "", "");
                        mLongPressOverlay.addOverlayItem(overlayItem);
                        this.abortBroadcast();
                    }
                }
                Log.e("SMS_Receiver", sb.toString());
            }
        }

    }
*/
    @Override
    protected void onDestroy() {
        // mMyLocationManager.destoryLocationManager();
        //unregisterReceiver(mSMSRec);
        unbindService(mServiceConnection);
        super.onDestroy();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        // 当我bindService时，让TextView显示MyService里getSystemTime()方法的返回值
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onServiceConnected");
            mMyService = ((NaviAideService.MyBinder) service).getService();
            mMyService.setCallBack(MapMode.this);
            mOrigin = mMyService.getLastLocation();
            if(mOrigin!=null)
            mMapController.animateTo(new GeoPoint((int)(mOrigin.getLatitude()*1E6),(int)(mOrigin.getLongitude()*1E6)));
        }

        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onLocationChange(Location location) {
        if (location != null) {

            // Log.i("SuperMap", "Location changed :provider = " +
            // location.getProvider() + " Lat: "
            // + location.getLatitude() + " Lng: " + location.getLongitude());
//            mMapController.animateTo(new GeoPoint((int) (location.getLatitude() * 1000000),
//                    (int) (location.getLongitude() * 1000000)));
            mOrigin = location;
            // Toast.makeText(getBaseContext(), "provider = " +
            // location.getProvider() + " Lat: "
            // + location.getLatitude() + " Lng: " + location.getLongitude(),
            // Toast.LENGTH_LONG).show();
        }

    }
    
    

}
