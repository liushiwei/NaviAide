
package com.carit.imhere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.gson.Gson;

public class RouteActivity extends Activity implements OnClickListener {

    private static final String TAG = "RouteActivity";

    private TextView start_text;

    private TextView end_text;

    private TextView upload_start_text;

    private TextView upload_end_text;

    private TextView download_start_text;

    private TextView download_end_text;

    private Calendar mStartTime;

    private Calendar mEndTime;

    private Calendar mUploadStartTime;

    private Calendar mUploadEndTime;

    private Calendar mDownloadStartTime;

    private Calendar mDownloadEndTime;

    private boolean mIsSetStartTime;

    private SimpleDateFormat mSDF;

    private static final int WRONG_TIME = 0x0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WRONG_TIME:
                    Toast.makeText(getBaseContext(), R.string.wrong_time, Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }

    };

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
        start_text = (TextView) findViewById(R.id.start_time);
        start_text.setText(mSDF.format(mStartTime.getTime()));
        end_text = (TextView) findViewById(R.id.end_time);
        end_text.setText(mSDF.format(mEndTime.getTime()));
        mUploadStartTime = Calendar.getInstance();
        mUploadEndTime = Calendar.getInstance();
        upload_start_text = (TextView) findViewById(R.id.upload_start_time);
        upload_start_text.setText(mSDF.format(mUploadStartTime.getTime()));
        upload_end_text = (TextView) findViewById(R.id.upload_end_time);
        upload_end_text.setText(mSDF.format(mUploadEndTime.getTime()));

        mDownloadStartTime = Calendar.getInstance();
        mDownloadEndTime = Calendar.getInstance();
        download_start_text = (TextView) findViewById(R.id.download_start_time);
        download_start_text.setText(mSDF.format(mDownloadStartTime.getTime()));
        download_end_text = (TextView) findViewById(R.id.download_end_time);
        download_end_text.setText(mSDF.format(mDownloadEndTime.getTime()));

        start.setOnClickListener(this);
        end.setOnClickListener(this);
        findViewById(R.id.search).setOnClickListener(this);
        findViewById(R.id.search_today).setOnClickListener(this);
        findViewById(R.id.clean).setOnClickListener(this);
        findViewById(R.id.start_track).setOnClickListener(this);
        findViewById(R.id.stop_track).setOnClickListener(this);
        findViewById(R.id.upload).setOnClickListener(this);
        findViewById(R.id.upload_today).setOnClickListener(this);
        findViewById(R.id.upload_start_time_setup).setOnClickListener(this);
        findViewById(R.id.upload_end_time_setup).setOnClickListener(this);
        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.download_today).setOnClickListener(this);
        findViewById(R.id.download_start_time_setup).setOnClickListener(this);
        findViewById(R.id.download_end_time_setup).setOnClickListener(this);
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

        } else if (R.id.stop_track == v.getId()) {
            SharedPreferences track = getSharedPreferences("LocationTrack", 0);
            Editor editor = track.edit();
            editor.putBoolean("track", false);
            editor.putBoolean("reboot_track", false);
            editor.commit();
            Intent it = new Intent(Intent.ACTION_RUN);
            it.setClass(RouteActivity.this, NaviAideService.class);
            it.putExtra("from", NaviAideService.STOP_TRACK);
            RouteActivity.this.startService(it);

        } else if (R.id.upload == v.getId()) {
            if(mUploadStartTime.getTimeInMillis()>= mUploadEndTime.getTimeInMillis()){
                mHandler.sendEmptyMessage(WRONG_TIME);
                return;
            }
            new TrackThread(mUploadStartTime.getTimeInMillis(), mUploadEndTime.getTimeInMillis(),true)
                    .start();

        } else if (R.id.upload_today == v.getId()) {
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            long start = c.getTimeInMillis();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            long end = c.getTimeInMillis();
            new TrackThread(start, end,true).start();

        } else if (v.getId() == R.id.upload_start_time_setup
                || v.getId() == R.id.upload_end_time_setup) {
            View view = View.inflate(RouteActivity.this, R.layout.timesetup, null);
            DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
            Calendar c = null;
            if (v.getId() == R.id.upload_start_time_setup) {
                mIsSetStartTime = true;
                c = mUploadStartTime;
            } else {
                mIsSetStartTime = false;
                c = mUploadEndTime;
            }
            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

                        public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
                            // start_text.setText("" + arg0.getYear() + "-"
                            // + (arg0.getMonth() + 1)
                            // + "-" + arg0.getDayOfMonth() + "");
                            if (mIsSetStartTime) {
                                mUploadStartTime.set(arg1, arg2, arg3);
                                upload_start_text.setText(mSDF.format(mUploadStartTime.getTime()));
                            } else {
                                mUploadEndTime.set(arg1, arg2, arg3);
                                upload_end_text.setText(mSDF.format(mUploadEndTime.getTime()));
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
                        mUploadStartTime.set(mUploadStartTime.get(Calendar.YEAR),
                                mUploadStartTime.get(Calendar.MONTH),
                                mUploadStartTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        upload_start_text.setText(mSDF.format(mUploadStartTime.getTime()));
                    } else {
                        mUploadEndTime.set(mUploadEndTime.get(Calendar.YEAR),
                                mUploadEndTime.get(Calendar.MONTH),
                                mUploadEndTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        upload_end_text.setText(mSDF.format(mUploadEndTime.getTime()));
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
                                        mUploadStartTime = Calendar.getInstance();
                                        upload_start_text.setText(mSDF.format(mUploadStartTime
                                                .getTime()));
                                    } else {
                                        mUploadEndTime = Calendar.getInstance();
                                        upload_end_text.setText(mSDF.format(mUploadEndTime
                                                .getTime()));
                                    }
                                }
                            });
            if (v.getId() == R.id.upload_start_time_setup) {
                builder.setTitle(R.string.start_time);
            } else
                builder.setTitle(R.string.end_time);
            builder.show();

        } else if (R.id.download == v.getId()) {
            if(mDownloadStartTime.getTimeInMillis()>= mDownloadEndTime.getTimeInMillis()){
                mHandler.sendEmptyMessage(WRONG_TIME);
                return;
            }
            new TrackThread(mDownloadStartTime.getTimeInMillis(),
                    mDownloadEndTime.getTimeInMillis(),false).start();

        } else if (R.id.download_today == v.getId()) {
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            long start = c.getTimeInMillis();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            long end = c.getTimeInMillis();
            new TrackThread(start, end,false).start();

        } else if (v.getId() == R.id.download_start_time_setup
                || v.getId() == R.id.download_end_time_setup) {
            View view = View.inflate(RouteActivity.this, R.layout.timesetup, null);
            DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
            Calendar c = null;
            if (v.getId() == R.id.download_start_time_setup) {
                mIsSetStartTime = true;
                c = mDownloadStartTime;
            } else {
                mIsSetStartTime = false;
                c = mDownloadEndTime;
            }
            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

                        public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
                            // start_text.setText("" + arg0.getYear() + "-"
                            // + (arg0.getMonth() + 1)
                            // + "-" + arg0.getDayOfMonth() + "");
                            if (mIsSetStartTime) {
                                mDownloadStartTime.set(arg1, arg2, arg3);
                                download_start_text.setText(mSDF.format(mDownloadStartTime
                                        .getTime()));
                            } else {
                                mDownloadEndTime.set(arg1, arg2, arg3);
                                download_end_text.setText(mSDF.format(mDownloadEndTime.getTime()));
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
                        mDownloadStartTime.set(mDownloadStartTime.get(Calendar.YEAR),
                                mDownloadStartTime.get(Calendar.MONTH),
                                mDownloadStartTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        download_start_text.setText(mSDF.format(mDownloadStartTime.getTime()));
                    } else {
                        mDownloadEndTime.set(mDownloadEndTime.get(Calendar.YEAR),
                                mDownloadEndTime.get(Calendar.MONTH),
                                mDownloadEndTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        download_end_text.setText(mSDF.format(mDownloadEndTime.getTime()));
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
                                        mDownloadStartTime = Calendar.getInstance();
                                        download_start_text.setText(mSDF.format(mDownloadStartTime
                                                .getTime()));
                                    } else {
                                        mDownloadEndTime = Calendar.getInstance();
                                        download_end_text.setText(mSDF.format(mDownloadEndTime
                                                .getTime()));
                                    }
                                }
                            });
            if (v.getId() == R.id.download_start_time_setup) {
                builder.setTitle(R.string.start_time);
            } else
                builder.setTitle(R.string.end_time);
            builder.show();

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
                            // TODO Auto-generated method stub

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
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void complete(String result) {
                            Log.e(TAG, "update result =" + result);
                            
                        }

                    }, param);
            Log.e(TAG, ClientUtils.buildRequestUrl(CaritClient.getInstance().getServerUrl(),
                    param));
            thread.start();
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

}
