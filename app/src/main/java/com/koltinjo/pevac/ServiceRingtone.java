package com.koltinjo.pevac;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

public class ServiceRingtone extends Service {

    private MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;
    private Notification notification;
    private boolean alarm;

    public ServiceRingtone() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.powerup);
        mediaPlayer.setLooping(true);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification
                .Builder(this)
                .setSmallIcon(android.R.drawable.ic_delete)
                .setContentTitle("Alarm")
                .setContentText("Turn off")
                .setContentIntent(PendingIntent.getActivity(this, Constants.INTENT_REQUEST_CODE, new Intent(getApplicationContext(), ActivityMain.class), 0))
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm = intent.getExtras().getBoolean("alarm");
        if (alarm && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            notificationManager.notify(Constants.NOTIFICATION_ID, notification);
        } else if (!alarm && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            notificationManager.cancel(Constants.NOTIFICATION_ID);
            // TODO sharedPrefs checked = false. Will this work?
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

}