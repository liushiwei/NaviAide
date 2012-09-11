
package com.carit.imhere;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class RouteActivity extends Activity implements OnClickListener {

    private TextView start_text;

    private TextView end_text;

    private Calendar mStartTime;

    private Calendar mEndTime;

    private boolean mIsSetStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();
        Button start = (Button) findViewById(R.id.start_time_setup);
        Button end = (Button) findViewById(R.id.end_time_setup);
        start_text = (TextView) findViewById(R.id.start_time);
        start_text.setText("" + mStartTime.get(Calendar.YEAR) + "-"
                + mStartTime.get(Calendar.MONTH) + 1 + "-" + mStartTime.get(Calendar.DAY_OF_MONTH)
                + " " + mStartTime.get(Calendar.HOUR_OF_DAY) + ":"
                + mStartTime.get(Calendar.MINUTE));
        end_text = (TextView) findViewById(R.id.end_time);
        end_text.setText("" + mEndTime.get(Calendar.YEAR) + "-" + mEndTime.get(Calendar.MONTH) + 1
                + "-" + mEndTime.get(Calendar.DAY_OF_MONTH) + " "
                + mEndTime.get(Calendar.HOUR_OF_DAY) + ":" + mEndTime.get(Calendar.MINUTE));
        start.setOnClickListener(this);
        end.setOnClickListener(this);
        findViewById(R.id.search).setOnClickListener(this);
        findViewById(R.id.search_today).setOnClickListener(this);
        findViewById(R.id.clean).setOnClickListener(this);
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
                                start_text.setText("" + mStartTime.get(Calendar.YEAR) + "-"
                                        + mStartTime.get(Calendar.MONTH) + 1 + "-"
                                        + mStartTime.get(Calendar.DAY_OF_MONTH) + " "
                                        + mStartTime.get(Calendar.HOUR_OF_DAY) + ":"
                                        + mStartTime.get(Calendar.MINUTE));
                            } else {
                                mEndTime.set(arg1, arg2, arg3);
                                end_text.setText("" + mEndTime.get(Calendar.YEAR) + "-"
                                        + mEndTime.get(Calendar.MONTH) + 1 + "-"
                                        + mEndTime.get(Calendar.DAY_OF_MONTH) + " "
                                        + mEndTime.get(Calendar.HOUR_OF_DAY) + ":"
                                        + mEndTime.get(Calendar.MINUTE));
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
                        start_text.setText("" + mStartTime.get(Calendar.YEAR) + "-"
                                + mStartTime.get(Calendar.MONTH) + 1 + "-"
                                + mStartTime.get(Calendar.DAY_OF_MONTH) + " "
                                + mStartTime.get(Calendar.HOUR_OF_DAY) + ":"
                                + mStartTime.get(Calendar.MINUTE));
                    } else {
                        mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                                mEndTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        end_text.setText("" + mEndTime.get(Calendar.YEAR) + "-"
                                + mEndTime.get(Calendar.MONTH) + 1 + "-"
                                + mEndTime.get(Calendar.DAY_OF_MONTH) + " "
                                + mEndTime.get(Calendar.HOUR_OF_DAY) + ":"
                                + mEndTime.get(Calendar.MINUTE));
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
                                        start_text.setText("" + mStartTime.get(Calendar.YEAR) + "-"
                                                + mStartTime.get(Calendar.MONTH) + 1 + "-"
                                                + mStartTime.get(Calendar.DAY_OF_MONTH) + " "
                                                + mStartTime.get(Calendar.HOUR_OF_DAY) + ":"
                                                + mStartTime.get(Calendar.MINUTE));
                                    } else {
                                        mEndTime = Calendar.getInstance();
                                        end_text.setText("" + mEndTime.get(Calendar.YEAR) + "-"
                                                + mEndTime.get(Calendar.MONTH) + 1 + "-"
                                                + mEndTime.get(Calendar.DAY_OF_MONTH) + " "
                                                + mEndTime.get(Calendar.HOUR_OF_DAY) + ":"
                                                + mEndTime.get(Calendar.MINUTE));
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

        } else if(v.getId() == R.id.clean){
            AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this)
            .setMessage(R.string.sure_clean)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    setResult(RESULT_OK);
                    // finish();
                }
            })
            .setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
            builder.show();
        }

    }

}
