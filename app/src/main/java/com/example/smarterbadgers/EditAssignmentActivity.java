package com.example.smarterbadgers;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Locale;

public class EditAssignmentActivity extends AppCompatActivity {
    private String name;
    private String desc;
    private String date;
    private String time;
    private int hour;
    private int minute;
    private int year;
    private int month;
    private int dayOfMonth;
    TextView nameTextView;
    TextView descTextView;
    TextView dateTextView;
    TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);

        getActivityResultRegistry();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        desc = intent.getStringExtra("desc");
        int startYear = intent.getIntExtra("year", -1);
        int startMonth = intent.getIntExtra("month", -1);
        int startDay = intent.getIntExtra("day", -1);
        year = startYear;
        month = startMonth;
        dayOfMonth = startDay;

        date = String.format(Locale.getDefault(), "%d/%d/%d", startMonth + 1, startDay, startYear);

        int currMinute = intent.getIntExtra("minute", -1);
        minute = currMinute;
        String twoDigitMinute = Integer.toString(currMinute);
        if (currMinute < 10) {
            twoDigitMinute = String.format(Locale.getDefault(), "%d%d", 0, currMinute);
        }

        int startHour = intent.getIntExtra("hour", -1);
        hour = startHour;
        String morningOrNight = "am";
        int displayHour = startHour;
        if (startHour > 12) {
            morningOrNight = "pm";
            displayHour -= 12;
        }
        time = String.format(Locale.getDefault(), "%d:%s%s", displayHour, twoDigitMinute, morningOrNight);

        nameTextView = findViewById(R.id.EditAssignmentNameText);
        descTextView = findViewById(R.id.EditAssignmentDescText);
        dateTextView = findViewById(R.id.EditAssignmentDateText);
        timeTextView = findViewById(R.id.EditAssignmentTimeText);
        Button button = findViewById(R.id.SaveEditAssignmentButton);

        timeTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int pickedHour, int pickedMinute) {
                        hour = pickedHour;
                        minute = pickedMinute;


                        String twoDigitMinute = Integer.toString(minute);
                        if (minute < 10) {
                            twoDigitMinute = String.format(Locale.getDefault(), "%d%d", 0, minute);
                        }

                        String morningOrNight = "am";
                        int displayHour = hour;
                        if (hour > 12) {
                            morningOrNight = "pm";
                            displayHour -= 12;
                        }

                        timeTextView.setText(displayHour + ":" + twoDigitMinute + morningOrNight);
                    }
                }, 0, 0, false);

                timePickerDialog.show();
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                            year = y;
                            month = m;
                            dayOfMonth = d;

                            dateTextView.setText(month + "/" + dayOfMonth + "/" + year);
                        }

                    }, startYear, startMonth, startDay);

                    datePickerDialog.show();
                }
            }
        });

        nameTextView.setText(name);
        descTextView.setText(desc);
        dateTextView.setText(date);
        timeTextView.setText(time);

        Log.d("assignment", "editing assignment");
    }

    public void onSaveClick(View view) {
        String newName = (String) nameTextView.getText().toString();
        String newDesc = (String) descTextView.getText().toString();
        String newDate = (String) dateTextView.getText().toString();
        String newTime = (String) timeTextView.getText().toString();


        Intent returnIntent = new Intent();
        returnIntent.putExtra("name", newName);
        returnIntent.putExtra("desc", newDesc);
        returnIntent.putExtra("year", year);
        returnIntent.putExtra("month", month);
        returnIntent.putExtra("day", dayOfMonth);
        returnIntent.putExtra("hour", hour);
        returnIntent.putExtra("minute", minute);
        setResult(Activity.RESULT_OK, returnIntent);

        this.finish();
    }
}