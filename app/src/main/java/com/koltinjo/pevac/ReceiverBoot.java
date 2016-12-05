package com.koltinjo.pevac;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by colt on 05.12.2016.
 */

public class ReceiverBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PEVAC", MODE_PRIVATE);
        boolean checked = sharedPreferences.getBoolean("checked", false);

        // Set alarm on boot if previously checked.
        if (checked) {
            int hour = sharedPreferences.getInt("hour", 0);
            int minute = sharedPreferences.getInt("minute", 0);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DATE, 1);
            }

            Intent intentAlarm = new Intent(context, ReceiverAlarm.class);
            intentAlarm.putExtra("alarm", true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.INTENT_REQUEST_CODE, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            // TODO Some toast that alarm is set.
        }
    }

}