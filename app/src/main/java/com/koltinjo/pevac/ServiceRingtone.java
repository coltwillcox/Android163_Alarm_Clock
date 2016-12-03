package com.koltinjo.pevac;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

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
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.powerup);
            mediaPlayer.setLooping(true);
        }

        boolean alarm = intent.getExtras().getBoolean("alarm");

        if (alarm) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        super.onDestroy();
    }
}