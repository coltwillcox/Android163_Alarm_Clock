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
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), ActivityMain.class), 0))
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
        if (alarm) {
            mediaPlayer.start();
            notificationManager.notify(0, notification);
        } else {
            // TODO Clear notification.
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }
}