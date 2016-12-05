package com.koltinjo.pevac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by colt on 03.12.2016.
 */

public class ReceiverAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean alarm = intent.getExtras().getBoolean("alarm");

        Intent serviceRingtone = new Intent(context, ServiceRingtone.class);
        serviceRingtone.putExtra("alarm", alarm);
        context.startService(serviceRingtone);
    }

}