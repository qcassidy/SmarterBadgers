package com.example.smarterbadgers;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

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
    ActivityResultLauncher<Intent> editAssignmentActivityResultLauncher;
    Assignment currAssignment;
    RecyclerView todoListRecyclerView;
    int dayPicked;
    int monthPicked;
    int yearPicked;
    TimePickerDialog timePickerDialog;

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
        int[] selectedDate = calendarFragment.getSelectedDate();

        // hand result of create assignment activity
        createAssignmentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String name = data.getStringExtra("name");
                            String desc = data.getStringExtra("desc");
                            String hour = data.getStringExtra("hour");
                            String minute = data.getStringExtra("minute");
                            String day = data.getStringExtra("day");
                            String month = data.getStringExtra("month");
                            String year = data.getStringExtra("year");

                            Assignment newAssignment = new Assignment(name, year + "/" + month + "/" + day, hour + ":" + minute, desc);

                            SaveAssignmentToDatabase databaseUpload = new SaveAssignmentToDatabase();
                            databaseUpload.execute(newAssignment);
                        }
                    }
                });

        editAssignmentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String name = data.getStringExtra("name");
                            String desc = data.getStringExtra("desc");
                            int hour = data.getIntExtra("hour", -1);
                            int minute = data.getIntExtra("minute", -1);
                            int day = data.getIntExtra("day", -1);
                            int month = data.getIntExtra("month", -1);
                            int year = data.getIntExtra("year", -1);

                            currAssignment.setName(name);
                            currAssignment.setDescription(desc);
                            currAssignment.changeDate(new int[] {month, day, year});
                            currAssignment.setDueTime(hour + ":" + minute);

                            EditAssignmentToDatabase databaseUpload = new EditAssignmentToDatabase();
                            databaseUpload.execute(currAssignment);
                        }
                    }
                });

        // set add assignment button to launch result launcher from above
        Button addAssignmentButton = view.findViewById(R.id.addAssignmentButton);
        addAssignmentButton.setOnClickListener( (View view) -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                       dayPicked = d;
                       monthPicked = m;
                       yearPicked = y;

                       timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }


            timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    //int[] selectedDate = calendarFragment.getSelectedDate();
                    int[] selectedDate = new int[] {yearPicked, monthPicked, dayPicked};

                    Intent intent = new Intent(view.getContext(), CreateAssignmentActivity.class);
                    intent.putExtra("hour", "" + hour);
                    intent.putExtra("minute", "" + minute);
                    intent.putExtra("year", "" + selectedDate[0]);
                    intent.putExtra("month", "" + selectedDate[1]);
                    intent.putExtra("day", "" + selectedDate[2]);

                    createAssignmentActivityResultLauncher.launch(intent);
                }
            }, 0, 0, false);
        });

        // create recycler view for todolist
        todoListRecyclerView = view.findViewById(R.id.TodoListRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        todoListRecyclerView.setLayoutManager(linearLayoutManager);


        // create database
        SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase("assignments",Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);

        // create adapter for recycler view
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        todoListAdapter = new TodoListAdapter( calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR), dbHelper, this);
        todoListRecyclerView.setAdapter(todoListAdapter);
        todoListRecyclerView.scrollToPosition(calendar.get(Calendar.DAY_OF_YEAR) - 1);

        // selection tracker to handle selection of recycler view items
        SelectionTracker tracker = new SelectionTracker.Builder<>(
                "todolist-selection",
                todoListRecyclerView,
                new StableIdKeyProvider(todoListRecyclerView),
                new MyDetailsLookup(todoListRecyclerView),
                StorageStrategy.createLongStorage())
                .build();

        return view;
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

            ArrayList<Integer[]> daysToUpdate = new ArrayList<>();
            daysToUpdate.add(new Integer[] {assignment.getDueMonth(), assignment.getDueDay(), assignment.getDueYear()});

            todoListAdapter.updateDay(daysToUpdate);
        }
    }

    public class EditAssignmentToDatabase extends AsyncTask<Assignment, Integer, Long> {
        Assignment assignment;

        @Override
        protected Long doInBackground(Assignment... assignments) {
            assignment = assignments[0];
            dbHelper.updateAssignment(assignments[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);


            ArrayList<Integer[]> daysToUpdate = new ArrayList<>();
            daysToUpdate.add(new Integer[] {assignment.getDueMonth(), assignment.getDueDay(), assignment.getDueYear()});

            if (assignment.getOldDueYear() != 0) {
                daysToUpdate.add(new Integer[] {assignment.getDueMonth(), assignment.getOldDueDay(), assignment.getOldDueYear()});
            }

            todoListAdapter.updateDay(daysToUpdate);

        }
    }


    public void editAssignment(Assignment assignment) {
        currAssignment = assignment;
        Intent intent = new Intent(getContext(), EditAssignmentActivity.class);
        intent.putExtra("name", currAssignment.getName());
        intent.putExtra("desc", currAssignment.getDescription());
        intent.putExtra("hour", currAssignment.getDueHour());
        intent.putExtra("minute", currAssignment.getDueMin());
        intent.putExtra("day", currAssignment.getDueDay());
        intent.putExtra("month", currAssignment.getDueMonth());
        intent.putExtra("year", currAssignment.getDueYear());
        editAssignmentActivityResultLauncher.launch(intent);
    }

    public class DeleteAssignmentToDatabase extends AsyncTask<Assignment, Integer, Long> {
        Assignment assignment;

        @Override
        protected Long doInBackground(Assignment... assignments) {
            assignment = assignments[0];
            dbHelper.deleteAssignment(assignments[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            ArrayList<Integer[]> daysToUpdate = new ArrayList<>();
            daysToUpdate.add(new Integer[] {assignment.getDueMonth(), assignment.getDueDay(), assignment.getDueYear()});

            todoListAdapter.updateDay(daysToUpdate);
            Toast toast = Toast.makeText(getContext(), "assignment deleted", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void deleteAssignment(Assignment assignment) {
        DeleteAssignmentToDatabase databaseDelete = new DeleteAssignmentToDatabase();
        databaseDelete.execute(assignment);
    }



    @Override
    public void onDestroy() {
        Log.d("destroy", "destroying fragment");
       super.onDestroy();
    }

    // used to allow selection on recycler view items
    private class MyDetailsLookup extends ItemDetailsLookup {
        RecyclerView recyclerView;

        public MyDetailsLookup(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Nullable
        @Override
        public ItemDetails getItemDetails(@NonNull MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (view != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
                if (holder instanceof TodoListAdapter.ViewHolder) {
                    TodoListAdapter.ViewHolder myViewHolder = ((TodoListAdapter.ViewHolder) holder);
                    //Log.d("TodoList", "item #" + myViewHolder.getItemDetails().getPosition() + " was selected");

                    myViewHolder.changeActivated();

                    ItemDetails myDetails = myViewHolder.getItemDetails();
                    return myDetails;
                }
            }
            return null;
        }
    }
}
