package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class CreateAssignmentActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText descText;
    private EditText notifyHoursText;
    private CheckBox notifyCheckBox;
    private TextView notifyTimeText;
    private TextView notifyDateText;
    int hour;
    int minute;
    int month;
    int dayOfMonth;
    int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        getActivityResultRegistry();
        notifyHoursText = findViewById(R.id.CreateAssignmentNotificationHoursText);
        notifyCheckBox = findViewById(R.id.CreateAssignmentNotificationCheckBox);

        Log.d("assignment", "creating new assignment");
    }

    public void saveAssignmentButtonOnClick(View view) {
        nameText = (EditText) findViewById(R.id.assignmentNameEditText);
        descText = (EditText) findViewById(R.id.assignmentDescEditText);

        String name = nameText.getText().toString();
        String desc = descText.getText().toString();
        int notifyHoursBefore = Integer.parseInt(notifyHoursText.getText().toString());
        boolean useNotification = notifyCheckBox.isChecked();


       Intent intent = getIntent();
       String year = intent.getStringExtra("year");
       String month = intent.getStringExtra("month");
       String day = intent.getStringExtra("day");
       String hour = intent.getStringExtra("hour");
       String minute = intent.getStringExtra("minute");

        if (useNotification) {
            if (notifyHoursBefore < 0) {
                Toast toast = new Toast(getApplicationContext());
                toast.setText("notification hours before due date cannot be negative");
                toast.show();
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            calendar.set(Calendar.MONTH, Integer.parseInt(month));
            calendar.set(Calendar.YEAR, Integer.parseInt(year));

            calendar.add(Calendar.HOUR_OF_DAY, -notifyHoursBefore);

            Calendar currentCalendar = Calendar.getInstance(TimeZone.getDefault());
            if (calendar.getTimeInMillis() - currentCalendar.getTimeInMillis() < 0) {
                long timeDiff = calendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
                long milliInHour = 3600000;
                long hours = timeDiff / milliInHour;
                Toast toast = new Toast(getApplicationContext());
                toast.setText("notification hours before due date is in the past. Hours before must be less than " + hours);
                toast.show();
                return;
            }
        }

        Intent returnIntent = new Intent();
       returnIntent.putExtra("name", name);
       returnIntent.putExtra("desc", desc);
       returnIntent.putExtra("year", year);
       returnIntent.putExtra("month", month);
       returnIntent.putExtra("day", day);
       returnIntent.putExtra("hour", hour);
       returnIntent.putExtra("minute", minute);
       returnIntent.putExtra("notify", useNotification);
       returnIntent.putExtra("notifyHours", notifyHoursBefore);
       setResult(Activity.RESULT_OK, returnIntent);

       this.finish();
    }
}