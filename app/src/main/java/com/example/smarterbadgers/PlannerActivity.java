package com.example.smarterbadgers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PlannerActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private CalendarFragment calendarFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        calendarFragment = new CalendarFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.CalendarFragmentContainer, calendarFragment).commit();


        RecyclerView todoListRecyclerView = findViewById(R.id.TodoListRecyclerView);

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //todoListRecyclerView.setLayoutManager(linearLayoutManager);

        //TodoListAdapter todoListAdapter = new TodoListAdapter("10/26/2021");
        //todoListRecyclerView.setAdapter(todoListAdapter);

    }

    public void addAssignmentButtonOnClickOld(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                goToCreateAssignmentActivity(hour, minute);
            }
        }, 0, 0, false);

        timePickerDialog.show();
    }

    public void goToCreateAssignmentActivity(int hour, int minute) {
        int[] selectedDate = calendarFragment.getSelectedDate();

        Intent intent = new Intent(this, CreateAssignmentActivity.class);
        intent.putExtra("year", "" + selectedDate[0]);
        intent.putExtra("month", "" + selectedDate[1]);
        intent.putExtra("day", "" + selectedDate[2]);
        intent.putExtra("hour", "" + hour);
        intent.putExtra("minute", "" + minute);

        startActivity(intent);
    }
    /**
     * Used to print assignment information to Log for testing purposes.
     * @param assignments assignments to be logged
     */
    protected static void logAssignments(ArrayList<Assignment> assignments) {
        if (assignments.size() == 0) {
            Log.d("assignment", "no assignments");
        }
        for (int i = 0; i < assignments.size(); i++) {
            Log.d("assignment", "Name: " + assignments.get(i).getName() + " Due Date: " + assignments.get(i).getDueDate());
        }
    }
}