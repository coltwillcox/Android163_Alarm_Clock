package com.koltinjo.pevac;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

// TODON Check when device off.
// TODOK Check when screen off.
// TODOK Background.
// TODOK Icon.
// TODOK Change messages.
// TODOK Shake to turn off.
// TODO Turn on screen on alarm. And unlock?
// TODO Configuration change.
// TODO Popup for snooze.
// TODO Vibrate.
// TODO Notification to turn off.
// TODO TimePicker colors.
// TODO Show how much time is left when alarm is set.

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

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();

        // Intents.
        intent = new Intent(this, ReceiverAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(this, Constants.INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        int hour;
        int minute;

        // Get timepicker time.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        // Set calendar.
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        message.setText(getString(R.string.textview_message_set_yes) + hour + " : " + minute);

        // Set pendingIntent and send it to alarmManager.
        intent.putExtra("alarm", true);
        pendingIntent = PendingIntent.getBroadcast(this, Constants.INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Save preference.
        sharedPreferences.edit().putBoolean("checked", true).putInt("hour", hour).putInt("minute", minute).commit();
    }

    private void unchecked() {
        message.setText(getString(R.string.textview_message_set_no));

        // Cancel pendingIntent.
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        // Send broadcast to ReceiverAlarm.
        intent.putExtra("alarm", false);
        sendBroadcast(intent);

        // Save preference.
        sharedPreferences.edit().putBoolean("checked", false).commit();
    }

}