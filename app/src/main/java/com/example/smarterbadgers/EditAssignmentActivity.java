package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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
    private int notifyHour, notifyMinute;
    private int notifyDay, notifyMonth, notifyYear;
    TextView nameTextView;
    TextView descTextView;
    TextView dateTextView;
    TextView timeTextView;
    EditText hoursNotifyBeforeText;
    CheckBox notifyCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // todo add cancel button
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);

        getActivityResultRegistry();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        desc = intent.getStringExtra("desc");
        int startYear = intent.getIntExtra("year", -1);
        int startMonth = intent.getIntExtra("month", -1);
        int startDay = intent.getIntExtra("day", -1);
        boolean notify = intent.getBooleanExtra("notify", false);
        int notifyHoursBefore = intent.getIntExtra("notifyHoursBefore", 0);
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

        // convert to 12 hour format
        int startHour = intent.getIntExtra("hour", -1);
        hour = startHour;
        String morningOrNight = "am";
        int displayHour = startHour;
        if (startHour > 12) {
            morningOrNight = "pm";
            displayHour -= 12;
        }
        if (startHour == 0) {
            displayHour = 12;
        }
        time = String.format(Locale.getDefault(), "%d:%s%s", displayHour, twoDigitMinute, morningOrNight);

        nameTextView = findViewById(R.id.EditAssignmentNameText);
        descTextView = findViewById(R.id.EditAssignmentDescText);
        dateTextView = findViewById(R.id.EditAssignmentDateText);
        timeTextView = findViewById(R.id.EditAssignmentTimeText);
        Button button = findViewById(R.id.SaveEditAssignmentButton);
        notifyCheckBox = findViewById(R.id.EditAssignmentNotificationCheckBox);
        hoursNotifyBeforeText = findViewById(R.id.EditAssignmentHoursBeforeText);

        notifyCheckBox.setChecked(notify);
        hoursNotifyBeforeText.setText(String.valueOf(notifyHoursBefore));

        // update variables and  after time change
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
                        if (hour == 0) {
                            displayHour = 12;
                        }

                        timeTextView.setText(displayHour + ":" + twoDigitMinute + morningOrNight);
                    }
                }, hour, minute, false);

                timePickerDialog.show();
            }
        });


        // change color on touch
        timeTextView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    timeTextView.setBackgroundColor(Color.LTGRAY);
                    return false;
                } else {
                    if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        timeTextView.setBackgroundColor(Color.WHITE);
                    } else if (MotionEvent.ACTION_MOVE == motionEvent.getAction()) {
                        //textView.setBackgroundColor(Color.WHITE);
                    } else if (MotionEvent.ACTION_CANCEL == motionEvent.getAction()) {
                        timeTextView.setBackgroundColor(Color.WHITE);
                    }
                    return false;
                }
            }
        });

        // update variables and textview after date change
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

                            dateTextView.setText((month + 1) + "/" + dayOfMonth + "/" + year);
                        }

                    }, startYear, startMonth, startDay);

                    datePickerDialog.show();
                }
            }
        });

        // change color on touch
        dateTextView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    dateTextView.setBackgroundColor(Color.LTGRAY);
                } else {
                    if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        dateTextView.setBackgroundColor(Color.WHITE);
                    } else if (MotionEvent.ACTION_MOVE == motionEvent.getAction()) {
                        //textView.setBackgroundColor(Color.WHITE);
                    } else if (MotionEvent.ACTION_CANCEL == motionEvent.getAction()) {
                        dateTextView.setBackgroundColor(Color.WHITE);
                    }
                }
                return false;
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
        boolean notify = notifyCheckBox.isChecked();
        int notifyHoursBefore = Integer.parseInt(hoursNotifyBeforeText.getText().toString());


        if (notify) {
            if (notifyHoursBefore < 0) {
                Toast toast = new Toast(getApplicationContext());
                toast.setText("notification hours before due date cannot be negative");
                toast.show();
                return;
            }

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);

            calendar.add(Calendar.HOUR_OF_DAY, -notifyHoursBefore);

            Calendar currentCalendar = Calendar.getInstance(TimeZone.getDefault());
            if (calendar.before(currentCalendar)) {
                calendar.add(Calendar.HOUR_OF_DAY, notifyHoursBefore);
                long timeDiff = calendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
                long milliInHour = 3600000;
                long hours = timeDiff / milliInHour;
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);

                if (hours < 0) {
                    toast.setText("Cannot set notification for assignment in the past.");
                }
                else {
                    toast.setText(notifyHoursBefore + " hours before the due date is in the past. Hours before must be less than or equal to " + hours + ".");
                }
                toast.show();
                return;
            }
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("name", newName);
        returnIntent.putExtra("desc", newDesc);
        returnIntent.putExtra("year", year);
        returnIntent.putExtra("month", month);
        returnIntent.putExtra("day", dayOfMonth);
        returnIntent.putExtra("hour", hour);
        returnIntent.putExtra("minute", minute);
        returnIntent.putExtra("notify", notify);
        returnIntent.putExtra("notifyHoursBefore", notifyHoursBefore);
        setResult(Activity.RESULT_OK, returnIntent);

        this.finish();
    }
}