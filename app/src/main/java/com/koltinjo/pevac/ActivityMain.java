package com.koltinjo.pevac;

import android.app.AlarmManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class ActivityMain extends AppCompatActivity {

    private AlarmManager alarmManager;
    private TimePicker timePicker;
    private Switch switcher;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker = (TimePicker) findViewById(R.id.activity_main_timepicker);
        switcher = (Switch) findViewById(R.id.activity_main_switch);
        message = (TextView) findViewById(R.id.activity_main_textview_message);
    }

}