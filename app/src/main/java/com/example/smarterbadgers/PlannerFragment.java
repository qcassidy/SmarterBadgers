package com.example.smarterbadgers;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlannerFragment extends Fragment {

    TodoListAdapter todoListAdapter;
    DBHelper dbHelper;
    CalendarFragment calendarFragment;
    View view;
    final int CREATE_ASSIGNMENT_ACTIVITY = 1;
    ActivityResultLauncher<Intent> createAssignmentActivityResultLauncher;

    public PlannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlannerFragment.
     */
    public static PlannerFragment newInstance() {
        PlannerFragment fragment = new PlannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_planner, container, false);

        calendarFragment = new CalendarFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.CalendarFragmentContainer, calendarFragment).commit();

        Button addAssignmentButton = view.findViewById(R.id.addAssignmentButton);
        addAssignmentButton.setOnClickListener(this::addAssignmentButtonOnClick);

        RecyclerView todoListRecyclerView = view.findViewById(R.id.TodoListRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        todoListRecyclerView.setLayoutManager(linearLayoutManager);


        SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase("assignments",Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        todoListAdapter = new TodoListAdapter("10/26/2021", dbHelper);
        todoListRecyclerView.setAdapter(todoListAdapter);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        todoListRecyclerView.scrollToPosition(calendar.get(Calendar.DAY_OF_YEAR) - 1);



        return view;
    }


    public void addAssignmentButtonOnClick(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                goToCreateAssignmentActivity(hour, minute);
            }
        }, 0, 0, false);

        timePickerDialog.show();
    }

    public void goToCreateAssignmentActivity(int hour, int minute) {
        int[] selectedDate = calendarFragment.getSelectedDate();

        createAssignmentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String name = data.getStringExtra("name");
                            String desc = data.getStringExtra("desc");

                            Assignment newAssignment = new Assignment(name, selectedDate[0] + "/" + selectedDate[1] + "/" + selectedDate[2], hour + ":" + minute, desc);

                            //dbHelper.saveAssignment(newAssignment);
                            SaveAssignmentToDatabase databaseUpload = new SaveAssignmentToDatabase();
                            databaseUpload.execute(newAssignment);
                        }
                    }
                }
        );

        Intent intent = new Intent(view.getContext(), CreateAssignmentActivity.class);
        //intent.putExtra("year", "" + selectedDate[0]);
        //intent.putExtra("month", "" + selectedDate[1]);
        //intent.putExtra("day", "" + selectedDate[2]);
        //intent.putExtra("hour", "" + hour);
        //intent.putExtra("minute", "" + minute);
        createAssignmentActivityResultLauncher.launch(intent);
        //todoListAdapter.updateDay(new int[] {selectedDate[1], selectedDate[2], selectedDate[0]});
    }

    public class SaveAssignmentToDatabase extends AsyncTask<Assignment, Integer, Long> {
        Assignment assignment;

        @Override
        protected Long doInBackground(Assignment... assignments) {
            assignment = assignments[0];
            dbHelper.saveAssignment(assignments[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            todoListAdapter.updateDay(new int[] {assignment.getDueMonth(), assignment.getDueDay(), assignment.getDueYear()});
        }
    }


    @Override
    public void onDestroy() {
        Log.d("destroy", "destroying fragment");
       super.onDestroy();
    }
}