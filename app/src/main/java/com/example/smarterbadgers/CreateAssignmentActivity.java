package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class CreateAssignmentActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText descText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        Log.d("assignment", "creating new assignment");
    }

    public void saveAssignmentButtonOnClick(View view) {
        nameText = (EditText) findViewById(R.id.assignmentNameEditText);
        descText = (EditText) findViewById(R.id.assignmentDescEditText);

        String name = nameText.getText().toString();
        String desc = descText.getText().toString();

        Intent intent = getIntent();
        String year = intent.getStringExtra("year");
        String month = intent.getStringExtra("month");
        String day = intent.getStringExtra("day");
        String hour = intent.getStringExtra("hour");
        String minute = intent.getStringExtra("minute");

        Assignment newAssignment = new Assignment(name, year + "/" + month  + "/" + day, hour + ":" + minute, desc);

        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("assignments", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        dbHelper.saveAssignment(newAssignment);

    //    getSupportFragmentManager().beginTransaction().replace(R.id.container, new CalendarFragment()).commit();
        this.finish();
        //getSupportFragmentManager().popBackStack();
    }
}