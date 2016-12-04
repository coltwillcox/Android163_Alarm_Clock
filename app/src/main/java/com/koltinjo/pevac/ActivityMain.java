package com.koltinjo.pevac;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

// TODO Configuration change.
// TODO Check when screen off.
// TODO Popup. Or open ActivityMain when alarm.

public class ActivityMain extends AppCompatActivity {

    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pendingIntent;
    private Calendar calendar;

    private TimePicker timePicker;
    private Switch switcher;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        calendar = Calendar.getInstance();

        timePicker = (TimePicker) findViewById(R.id.activity_main_timepicker);
        switcher = (Switch) findViewById(R.id.activity_main_switch);
        message = (TextView) findViewById(R.id.activity_main_textview_message);

        // TODO Set switcher position, if alarm is set? Before listener.

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checked();
                } else {
                    unchecked();
                }
            }
        });
    }

    private void checked() {
        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            message.setText(hour + " : " + minute);
            intent.putExtra("alarm", true);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            message.setText("Time in the past.");
            switcher.setChecked(false);
        }
    }

    private void unchecked() {
        message.setText("No");
        alarmManager.cancel(pendingIntent);
        intent.putExtra("alarm", false);
        sendBroadcast(intent);
    }

}