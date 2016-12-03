package com.koltinjo.pevac;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class ServiceRingtone extends Service {

    private MediaPlayer mediaPlayer;

    public ServiceRingtone() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = MediaPlayer.create(this, R.raw.powerup);
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

}