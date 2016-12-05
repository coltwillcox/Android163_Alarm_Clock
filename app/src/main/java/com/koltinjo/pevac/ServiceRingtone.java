package com.koltinjo.pevac;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

public class ServiceRingtone extends Service {

    private MediaPlayer mediaPlayer;
    private boolean alarm;

    private SharedPreferences sharedPreferences;

    // Notification.
    private NotificationManager notificationManager;
    private Notification notification;

    // Shake sensor. http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it/2318356#2318356
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private float acceleration;
    private float accelerationCurrent;
    private float accelerationLast;

    public ServiceRingtone() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.powerup);
        mediaPlayer.setLooping(true);

        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);

        // Notification.
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

        // Shake sensor.
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                accelerationLast = accelerationCurrent;
                accelerationCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = accelerationCurrent - accelerationLast;
                acceleration = acceleration * 0.9f + delta; // Low-cut filter.
                if (acceleration > 12) {
                    alarmStop();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleration = 0f;
        accelerationCurrent = SensorManager.GRAVITY_EARTH;
        accelerationLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarm = intent.getExtras().getBoolean("alarm");
        if (alarm && !mediaPlayer.isPlaying()) {
            alarmStart();
        } else if (!alarm && mediaPlayer.isPlaying()) {
            alarmStop();
        }
        return START_NOT_STICKY;
    }

    private void alarmStart() {
        mediaPlayer.start();
        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void alarmStop() {
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        notificationManager.cancel(Constants.NOTIFICATION_ID);
        sensorManager.unregisterListener(sensorEventListener);

        // Update preference.
        sharedPreferences.edit().putBoolean("checked", false).commit();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }

}