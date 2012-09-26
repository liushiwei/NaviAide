
package com.carit.imhere;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.gson.Gson;

public class RouteActivity extends Activity implements OnClickListener {

    private static final String TAG = "RouteActivity";

    private TextView start_text;

    private TextView end_text;

    private TextView upload_start_text;

    private TextView upload_end_text;

    private Calendar mStartTime;

    private Calendar mEndTime;

    private Calendar mUploadStartTime;

    private Calendar mUploadEndTime;

    private boolean mIsSetStartTime;

    private SimpleDateFormat mSDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        mSDF = new SimpleDateFormat(getString(R.string.date_format), getResources()
                .getConfiguration().locale);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();
        mUploadStartTime = Calendar.getInstance();
        mUploadEndTime = Calendar.getInstance();
        Button start = (Button) findViewById(R.id.start_time_setup);
        Button end = (Button) findViewById(R.id.end_time_setup);
        start_text = (TextView) findViewById(R.id.start_time);
        start_text.setText(mSDF.format(mStartTime.getTime()));
        end_text = (TextView) findViewById(R.id.end_time);
        end_text.setText(mSDF.format(mEndTime.getTime()));
        upload_start_text = (TextView) findViewById(R.id.upload_start_time);
        upload_start_text.setText(mSDF.format(mUploadStartTime.getTime()));
        upload_end_text = (TextView) findViewById(R.id.upload_end_time);
        upload_end_text.setText(mSDF.format(mUploadEndTime.getTime()));

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
            // c.getTime().getTime();

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
            Intent it = new Intent(getBaseContext(), MapMode.class);
            it.putExtra("hotkey", MapMode.CAR_ROUTE);
            it.putExtra("start_time", mStartTime.getTime().getTime());
            it.putExtra("end_time", mEndTime.getTime().getTime());
            startActivity(it);
        } else if (v.getId() == R.id.search_today) {
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            Intent it = new Intent(getBaseContext(), MapMode.class);
            it.putExtra("hotkey", MapMode.CAR_ROUTE);
            it.putExtra("start_time", c.getTime().getTime());
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            it.putExtra("end_time", c.getTime().getTime());
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
            
            new TrackThread(mUploadStartTime.getTimeInMillis(),mUploadEndTime.getTimeInMillis()).start();

        } else if (R.id.upload_today == v.getId()) {
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0);
            long start =c.getTimeInMillis();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59);
            long end =c.getTimeInMillis();
            new TrackThread(start,end).start();

        }else if (v.getId() == R.id.upload_start_time_setup
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
            // c.getTime().getTime();

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

        }

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
            Log.e(TAG,"start = "+start+",end="+end);
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(LocationTable.CONTENT_URI, null, " time > " + start
                    + " and time < " + end, null, LocationTable.DEFAULT_SORT_ORDER);
            if (cur.getCount() > 0) {
                List<Location> lists = getPoints(cur);
                Gson gson = new Gson();
                Log.e(TAG, gson.toJson(lists));
            }
            super.run();
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


}
