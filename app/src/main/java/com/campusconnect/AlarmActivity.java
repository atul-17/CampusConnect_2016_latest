package com.campusconnect;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Atul on 17-08-2016.

public class AlarmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);

        View.OnClickListener setclickListner = new View.OnClickListener() {
            @SuppressWarnings("WrongConstant")
            @Override
            public void onClick(View view) {
                //this invokes the activity alertDialogactivity,which inturn invokes a the alarm alert dialog window
                Intent i = new Intent("com.campusconnect.AlertDialogActivity");
                //crate a pending intent
                PendingIntent operation = PendingIntent.getActivity(getBaseContext(), 0, i, 0);
                //Getting a ref to the system service alarm
                AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

                DatePicker dpDate = (DatePicker) findViewById(R.id.dp_date);
                TimePicker tpTime = (TimePicker) findViewById(R.id.tp_time);
                int year = dpDate.getYear();
                int month = dpDate.getMonth();
                int day = dpDate.getDayOfMonth();
                int hour = tpTime.getCurrentHour();
                int minute = tpTime.getCurrentMinute();




                long alarm_time = calendar.getTimeInMillis();

                //setting an alarm which invokes the opration at alarm
               alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, operation);


                //Toast.makeText(getBaseContext(), "Alarm is set successfully", Toast.LENGTH_SHORT).show();

           }
        };

        View.OnClickListener quitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };


    Button btnSetAlarm = (Button) findViewById(R.id.btn_set_alarm);
    btnSetAlarm.setOnClickListener(setclickListner);

    Button btnQuitAlarm = (Button) findViewById(R.id.btn_quit_alarm);
    btnQuitAlarm.setOnClickListener(quitClickListener);
            }

}
*/

