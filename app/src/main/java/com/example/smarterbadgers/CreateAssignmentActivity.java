package com.example.smarterbadgers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.URL;
import java.util.ArrayList;

public class CreateAssignmentActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText descText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        getActivityResultRegistry();

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

       Intent returnIntent = new Intent();
       returnIntent.putExtra("name", name);
       returnIntent.putExtra("desc", desc);
       returnIntent.putExtra("year", year);
       returnIntent.putExtra("month", month);
       returnIntent.putExtra("day", day);
       returnIntent.putExtra("hour", hour);
       returnIntent.putExtra("minute", minute);
       setResult(Activity.RESULT_OK, returnIntent);

       this.finish();
    }
}