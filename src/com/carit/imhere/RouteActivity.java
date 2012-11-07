
package com.carit.imhere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.carit.imhere.provider.LocationTable;
import com.carit.platform.CaritClient;
import com.carit.platform.ClientUtils;
import com.carit.platform.response.LocationListResponse;
import com.carit.platform.response.LocationResponse;
import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

public class RouteActivity extends Activity implements OnClickListener {

    private static final String TAG = "RouteActivity";

    private TextView start_text;

    private TextView end_text;

    private Calendar mStartTime;

    private Calendar mEndTime;

    private boolean mIsSetStartTime;

    private SimpleDateFormat mSDF;

    private static final int WRONG_TIME = 0x0;
    
    private static final int UPLOAD_COMPLETED = 0x1;
    
    private static final int DOWNLOAD_COMPLETED = 0x2;
    
    private boolean isTrack;
    
    private long mUserId;
    
    private ProgressDialog mProgDialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WRONG_TIME:
                    Toast.makeText(getBaseContext(), R.string.wrong_time, Toast.LENGTH_LONG).show();
                    break;
                case UPLOAD_COMPLETED:
                    if(mProgDialog!=null&&mProgDialog.isShowing())
                        mProgDialog.dismiss();
                    if(msg.arg1==0){
                        Toast.makeText(getBaseContext(), R.string.upload_success, Toast.LENGTH_LONG).show();
                    }else if(msg.arg1==1){
                        Toast.makeText(getBaseContext(), R.string.upload_failed, Toast.LENGTH_LONG).show();
                    }else if(msg.arg1==2){
                        Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getBaseContext(), R.string.no_data_upload, Toast.LENGTH_LONG).show();
                    }
                    break;
                case DOWNLOAD_COMPLETED:
                    if(mProgDialog!=null&&mProgDialog.isShowing())
                        mProgDialog.dismiss();
                    if(msg.arg1==0){
                        Toast.makeText(getBaseContext(), R.string.download_success, Toast.LENGTH_LONG).show();
                    }else if(msg.arg1==1){
                        Toast.makeText(getBaseContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                    }else if(msg.arg1==2){
                        Toast.makeText(getBaseContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getBaseContext(), R.string.no_data_download, Toast.LENGTH_LONG).show();
                    }
                    break;
            }
            super.handleMessage(msg);
        }

    };

    protected NaviAideServiceAidl mMyService;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        mSDF = new SimpleDateFormat(getString(R.string.date_format), getResources()
                .getConfiguration().locale);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();
        Button start = (Button) findViewById(R.id.start_time_setup);
        Button end = (Button) findViewById(R.id.end_time_setup);
        start.setOnClickListener(this);
        end.setOnClickListener(this);
        start_text = (TextView) findViewById(R.id.start_time);
        start_text.setText(mSDF.format(mStartTime.getTime()));
        end_text = (TextView) findViewById(R.id.end_time);
        end_text.setText(mSDF.format(mEndTime.getTime()));

        
        findViewById(R.id.search).setOnClickListener(this);
        findViewById(R.id.search_today).setOnClickListener(this);
        findViewById(R.id.clean).setOnClickListener(this);
        findViewById(R.id.start_track).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
        findViewById(R.id.upload_today).setOnClickListener(this);
        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.download_today).setOnClickListener(this);
        Intent i = new Intent();
        i.setClass(this, NaviAideService.class);
        i.putExtra("remote", true);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_time_setup || v.getId() == R.id.end_time_setup) {
            View view = View.inflate(RouteActivity.this, R.layout.timesetup, null);
            DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
            Calendar c = null;
            if (v.getId() == R.id.start_time_setup) {
                mIsSetStartTime = true;
                c = mStartTime;
            } else {
                mIsSetStartTime = false;
                c = mEndTime;
            }
            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

                        public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
                            // start_text.setText("" + arg0.getYear() + "-"
                            // + (arg0.getMonth() + 1)
                            // + "-" + arg0.getDayOfMonth() + "");
                            if (mIsSetStartTime) {
                                mStartTime.set(arg1, arg2, arg3);
                                start_text.setText(mSDF.format(mStartTime.getTime()));
                            } else {
                                mEndTime.set(arg1, arg2, arg3);
                                end_text.setText(mSDF.format(mEndTime.getTime()));
                            }

                        }

                    });

            TimePicker tpicker = (TimePicker) view.findViewById(R.id.timePicker1);
            tpicker.setIs24HourView(false);// 设置是否为24小时制
            tpicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            tpicker.setCurrentMinute(c.get(Calendar.MINUTE));
            tpicker.setOnTimeChangedListener(new OnTimeChangedListener() {

                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    // start_text.setText("[" + hourOfDay + ":" + minute +
                    // "]" + "["
                    // + view.getCurrentHour() + ":" +
                    // view.getCurrentMinute()
                    // + "]");
                    if (mIsSetStartTime) {
                        mStartTime.set(mStartTime.get(Calendar.YEAR),
                                mStartTime.get(Calendar.MONTH),
                                mStartTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        start_text.setText(mSDF.format(mStartTime.getTime()));
                    } else {
                        mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                                mEndTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        end_text.setText(mSDF.format(mEndTime.getTime()));
                    }

                }

            });
            // c.getTimeInMillis();

            AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this)

                    .setView(view)
                    .setIcon(android.R.drawable.ic_menu_today)
                    .setPositiveButton(R.string.setup, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setResult(RESULT_OK);
                            // finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (mIsSetStartTime) {
                                        mStartTime = Calendar.getInstance();
                                        start_text.setText(mSDF.format(mStartTime.getTime()));
                                    } else {
                                        mEndTime = Calendar.getInstance();
                                        end_text.setText(mSDF.format(mEndTime.getTime()));
                                    }
                                }
                            });
            if (v.getId() == R.id.start_time_setup) {
                builder.setTitle(R.string.start_time);
            } else
                builder.setTitle(R.string.end_time);
            builder.show();

        } else if (v.getId() == R.id.search) {
            if(mStartTime.getTimeInMillis()>= mEndTime.getTimeInMillis()){
                mHandler.sendEmptyMessage(WRONG_TIME);
                return;
            }
            Intent it = new Intent(getBaseContext(), MapMode.class);
            it.putExtra("hotkey", MapMode.CAR_ROUTE);
            it.putExtra("start_time", mStartTime.getTimeInMillis());
            it.putExtra("end_time", mEndTime.getTimeInMillis());
            startActivity(it);
        } else if (v.getId() == R.id.search_today) {
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            Intent it = new Intent(getBaseContext(), MapMode.class);
            it.putExtra("hotkey", MapMode.CAR_ROUTE);
            it.putExtra("start_time", c.getTimeInMillis());
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            it.putExtra("end_time", c.getTimeInMillis());
            startActivity(it);

        } else if (v.getId() == R.id.clean) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this)
                    .setMessage(R.string.sure_clean)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // setResult(RESULT_OK);
                            // finish();
                            getContentResolver().delete(LocationTable.CONTENT_URI, null, null);
                            Toast.makeText(getBaseContext(), R.string.clean_all, Toast.LENGTH_LONG)
                                    .show();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
            builder.show();
        } else if (R.id.start_track == v.getId()) {
            if(!isTrack){
            AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this)
                    .setMessage(R.string.reboot_track).setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            SharedPreferences track = getSharedPreferences("LocationTrack", 0);
                            Editor editor = track.edit();
                            editor.putBoolean("track", true);
                            editor.putBoolean("reboot_track", true);
                            editor.commit();
                            Intent it = new Intent(Intent.ACTION_RUN);
                            it.setClass(RouteActivity.this, NaviAideService.class);
                            it.putExtra("from", NaviAideService.START_TRACK);
                            RouteActivity.this.startService(it);
                            Toast.makeText(getBaseContext(), R.string.start_track,
                                    Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            SharedPreferences track = getSharedPreferences("LocationTrack", 0);
                            Editor editor = track.edit();
                            editor.putBoolean("track", true);
                            editor.putBoolean("reboot_track", false);
                            editor.commit();
                            Intent it = new Intent(Intent.ACTION_RUN);
                            it.setClass(RouteActivity.this, NaviAideService.class);
                            it.putExtra("from", NaviAideService.START_TRACK);
                            RouteActivity.this.startService(it);
                            Toast.makeText(getBaseContext(), R.string.start_track,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            builder.show();
            ((Button)v).setText(R.string.stop_track);
            }else{
                SharedPreferences track = getSharedPreferences("LocationTrack", 0);
                Editor editor = track.edit();
                editor.putBoolean("track", false);
                editor.putBoolean("reboot_track", false);
                editor.commit();
                Intent it = new Intent(Intent.ACTION_RUN);
                it.setClass(RouteActivity.this, NaviAideService.class);
                it.putExtra("from", NaviAideService.STOP_TRACK);
                RouteActivity.this.startService(it);
                ((Button)v).setText(R.string.start_track);
            }
        } /*else if (R.id.stop_track == v.getId()) {

        }*/ else if (R.id.upload == v.getId()) {
            if(mStartTime.getTimeInMillis()>= mEndTime.getTimeInMillis()){
                mHandler.sendEmptyMessage(WRONG_TIME);
                return;
            }
            showDialog(getString(R.string.uploading));
            new TrackThread(mStartTime.getTimeInMillis(), mEndTime.getTimeInMillis(),true)
                    .start();

        } else if (R.id.upload_today == v.getId()) {
            
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            long start = c.getTimeInMillis();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            long end = c.getTimeInMillis();
            showDialog(getString(R.string.uploading));
            new TrackThread(start, end,true).start();

        } else if (R.id.download == v.getId()) {
            if(mStartTime.getTimeInMillis()>= mEndTime.getTimeInMillis()){
                mHandler.sendEmptyMessage(WRONG_TIME);
                return;
            }
            showDialog(getString(R.string.downloading));
            new TrackThread(mStartTime.getTimeInMillis(),
                    mEndTime.getTimeInMillis(),false).start();

        } else if (R.id.download_today == v.getId()) {
            
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            long start = c.getTimeInMillis();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            long end = c.getTimeInMillis();
            showDialog(getString(R.string.downloading));
            new TrackThread(start, end,false).start();

        } 

    }

    class TrackThread extends Thread {
        private long start;

        private long end;
        
        private boolean isUpload;

        public TrackThread(long start, long end,boolean isUpload) {
            this.start = start;
            this.end = end;
            this.isUpload = isUpload;
        }

        @Override
        public void run() {
            if(isUpload){
                
                upload(start,end);
            }else{
                
                download(start,end);
            }
        }
    };

    private List<Location> getPoints(Cursor cur) {

        List<Location> mLocations = new ArrayList<Location>();
        if (cur.moveToFirst()) {

            int lat = cur.getColumnIndex(LocationTable.LAT);
            int lng = cur.getColumnIndex(LocationTable.LNG);
            int time = cur.getColumnIndex(LocationTable.TIME);

            do {
                // Get the field values

                Location location = new Location();
                location.setLat(Double.valueOf(cur.getString(lat)));
                location.setLng(Double.valueOf(cur.getString(lng)));
                location.setTime(cur.getLong(time));
                mLocations.add(location);

            } while (cur.moveToNext());

        }
        return mLocations;
    }

    private void download(long start, long end) {

        
        Log.e(TAG, "start = " + start + ",end=" + end);
//        ContentResolver cr = getContentResolver();
//        Cursor cur = cr.query(LocationTable.CONTENT_URI, null, " time > " + start
//                + " and time < " + end, null, LocationTable.DEFAULT_SORT_ORDER);
//        if (cur.getCount() > 0) {
//            List<Location> lists = getPoints(cur);
//            Gson gson = new Gson();
//            // Log.e(TAG, gson.toJson(lists));
            Map<String, String> param = CaritClient.getInstance().buildParamValues(
                    "platform.location.search", "1.0");
            param.put("deviceId", getDeviceId());
            param.put("startTime", start+"");
            param.put("endTime", end+"");
            param.put("type", "2");
            param.put("accountId", mUserId+"");

            String sign = ClientUtils.sign(param, CaritClient.getInstance().getAppSecret());
            param.put(CaritClient.SYSTEM_PARAM_SIGN, sign);

            // String result
            // =CaritClient.getHttpResponse(ClientUtils.buildRequestUrl(CaritClient.getInstance().getServerUrl(),
            // param), "POST");

            // Log.e(TAG, "update result ="+result);

            HttpThread thread = new HttpThread(ClientUtils.buildRequestUrl(CaritClient.getInstance().getServerUrl(),param),
                    new HttpThreadListener() {

                        @Override
                        public void start() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void netError(String error) {
                            mHandler.obtainMessage(DOWNLOAD_COMPLETED, 2, 0).sendToTarget();

                        }

                        @Override
                        public void complete(String result) {
                            Log.e(TAG, "update result =" + result);
                            Gson gson = new Gson();
                            LocationListResponse listResponse =gson.fromJson(result, LocationListResponse.class);
                            if(listResponse!=null&&listResponse.getLists()!=null&&listResponse.getLists().size()>0){
                                for(LocationResponse response:listResponse.getLists()){
                                    saveLocation(response.getLat(), response.getLng(), response.getTime());
                                }
                                mHandler.obtainMessage(DOWNLOAD_COMPLETED, 0, 0).sendToTarget();
                                return;
                            }
                            if(listResponse==null||listResponse.getLists()==null){
                                mHandler.obtainMessage(DOWNLOAD_COMPLETED, 1, 0).sendToTarget();
                                return;
                            }
                            if(listResponse.getLists().size()==0){
                                mHandler.obtainMessage(DOWNLOAD_COMPLETED, -1, 0).sendToTarget();
                            }
                        }

                    });
            Log.e(TAG, ClientUtils.buildRequestUrl(CaritClient.getInstance().getServerUrl(),
                    param));
            thread.start();
    
        
    }

    public class Location {
        private double lat;

        private double lng;

        private long time;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

    }

    public class UploadLocations {
        private List<Location> lists;

        private String deviceId;

        private int locationSize;

        public int getLocationSize() {
            return locationSize;
        }

        public void setLocationSize(int locationSize) {
            this.locationSize = locationSize;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public List<Location> getLists() {
            return lists;
        }

        public void setLists(List<Location> lists) {
            this.lists = lists;
        }

    }

    public static String getDeviceId() {
        final String MMC_CID_PATH = "/sys/class/mmc_host/mmc0/mmc0:0001/cid";
        String id = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(MMC_CID_PATH)));
            while ((id = br.readLine()) != null) {
                break;
            }
            br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (id != null) {
            id = Integer.toHexString(id.hashCode()).toUpperCase();
        } else {
            id = "00000000";
        }
        Log.e(TAG, "DevideId = " + id);
        return id;
    }
    
    private void upload(long start,long end){
        Log.e(TAG, "start = " + start + ",end=" + end);
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(LocationTable.CONTENT_URI, null, " time > " + start
                + " and time < " + end, null, LocationTable.DEFAULT_SORT_ORDER);
        if (cur.getCount() > 0) {
            List<Location> lists = getPoints(cur);
            Gson gson = new Gson();
            // Log.e(TAG, gson.toJson(lists));
            Map<String, String> param = CaritClient.getInstance().buildParamValues(
                    "platform.location.upload", "1.0");
            param.put("deviceId", getDeviceId());
            param.put("lists", gson.toJson(lists));
            param.put("accountId", mUserId+"");
            String sign = ClientUtils.sign(param, CaritClient.getInstance().getAppSecret());
            param.put(CaritClient.SYSTEM_PARAM_SIGN, sign);

            // String result
            // =CaritClient.getHttpResponse(ClientUtils.buildRequestUrl(CaritClient.getInstance().getServerUrl(),
            // param), "POST");

            // Log.e(TAG, "update result ="+result);

            HttpThread thread = new HttpThread(CaritClient.getInstance().getServerUrl(),
                    new HttpThreadListener() {

                        @Override
                        public void start() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void netError(String error) {
                            mHandler.obtainMessage(UPLOAD_COMPLETED, 2, 0).sendToTarget();

                        }

                        @Override
                        public void complete(String result) {
                            if(result.contains("successful")&&result.contains("true")){
                                mHandler.obtainMessage(UPLOAD_COMPLETED, 0, 0).sendToTarget();
                            }else{
                                mHandler.obtainMessage(UPLOAD_COMPLETED, 1, 0).sendToTarget();
                            }
                            
                        }

                    }, param);
            Log.e(TAG, ClientUtils.buildRequestUrl(CaritClient.getInstance().getServerUrl(),
                    param));
            thread.start();
        }else{
                mHandler.obtainMessage(UPLOAD_COMPLETED, -1, 0).sendToTarget();
        }
    }
    
    private void saveLocation(double lat,double lng,long time) {
        ContentValues values = new ContentValues();
        values.put(LocationTable.TIME, time);
        values.put(LocationTable.LAT, lat);
        values.put(LocationTable.LNG, lng);
        Uri uri = this.getApplicationContext().getContentResolver()
                .insert(LocationTable.CONTENT_URI, values);
    }
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        // 当我bindService时，让TextView显示MyService里getSystemTime()方法的返回值
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onServiceConnected");
            mMyService = NaviAideServiceAidl.Stub.asInterface(service);  
            try {
                if(mMyService.isTrack()){
                Button start = (Button) findViewById(R.id.start_track);
                start.setText(getString(R.string.stop_track));
                isTrack = mMyService.isTrack();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }

        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }
    };


    
    

   
    @Override
    protected void onResume() {
        if(mUserId==0)
        getUserId();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
    
    private long getUserId(){
        
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.carit.account");
        if(accounts==null||accounts.length==0){
            Intent intent = new Intent();
            intent.setAction("com.carit.account_manager");
            startActivityForResult(intent, 10);
        }
        for (Account account : accounts)
        {
            Log.e(TAG, account.name + " - " + account.type+" email="+manager.getUserData(account, "email")+ " user id="+manager.getUserData(account, "user_id")+" device_id = "+manager.getUserData(account, "device_id"));
            mUserId = Long.valueOf(manager.getUserData(account, "user_id"));
            return mUserId;
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null)
        Log.e(TAG, "requestCode = "+requestCode+" resultCode="+resultCode+" user_id="+data.getLongExtra("user_id", 0));
        else
            Log.e(TAG, "requestCode = "+requestCode+" resultCode="+resultCode);
        if(requestCode==10 && resultCode==RESULT_OK){
            mUserId = data.getLongExtra("user_id", 0);
        }else if(requestCode==10){
            finish();
        }
        
    }
    
    private void showDialog(String context){
        mProgDialog = ProgressDialog.show(RouteActivity.this, null, context,true, false);
    }
    


}
