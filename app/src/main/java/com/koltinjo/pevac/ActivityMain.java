package com.koltinjo.pevac;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

// TODO Check when device off.
// TODO Check when screen off.
// TODO Configuration change.
// TODO Popup. Or open ActivityMain when alarm.
// TODO Background.
// TODO Icon.
// TODO Vibrate.
// TODO Shake to turn off.

public class ActivityMain extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private AlarmManager alarmManager;
    private Calendar calendar;

    // Intents.
    private Intent intent;
    private PendingIntent pendingIntent;

    // Views.
    private TimePicker timePicker;
    private Switch switcher;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("PEVAC", MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();

        // Intents.
        intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Views.
        timePicker = (TimePicker) findViewById(R.id.activity_main_timepicker);
        switcher = (Switch) findViewById(R.id.activity_main_switch);
        message = (TextView) findViewById(R.id.activity_main_textview_message);

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
        readAlarm();
    }

    // Read previously set alarm and set timepicker and switcher.
    private void readAlarm() {
        boolean checked = sharedPreferences.getBoolean("checked", false);
        int hour = sharedPreferences.getInt("hour", 0);
        int minute = sharedPreferences.getInt("minute", 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
        switcher.setChecked(checked);
    }

    private void checked() {
        Log.d("oiram", "from checked");
        int hour;
        int minute;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
            Log.d("oiram", "1");
        }
        Log.d("oiram", calendar.getTimeInMillis() + "");
        message.setText(hour + " : " + minute);
        intent.putExtra("alarm", true);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        sharedPreferences.edit().putBoolean("checked", true).putInt("hour", hour).putInt("minute", minute).commit();
    }

    private void unchecked() {
        Log.d("oiram", "from unchecked");
        message.setText("No");
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        intent.putExtra("alarm", false);
        sendBroadcast(intent);
        sharedPreferences.edit().putBoolean("checked", false).commit();
    }

}