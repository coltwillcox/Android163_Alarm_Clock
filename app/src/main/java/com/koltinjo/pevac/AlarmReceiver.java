package com.koltinjo.pevac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by colt on 03.12.2016.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("oiram", "ok");
        Toast.makeText(context, "Inside receiver.", Toast.LENGTH_LONG).show();
    }

}