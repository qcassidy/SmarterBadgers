package com.example.smarterbadgers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlannerFragment extends Fragment {

    final static String ASSIGNMENT_NOTIFICATION_ID = "assignment_notification_id_";
    final static String ASSIGNMENT_NOTIFICATION_CHANNEL_NAME = "Assignment Notifications";
    final static String ASSIGNMENT_NOTIFICATION_CHANNEL_ID = "assignment_notification_channel_id";
    TodoListAdapter todoListAdapter;
    DBHelper dbHelper;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_planner, container, false);


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
                            boolean notify = data.getBooleanExtra("notify", false);
                            int notifyHoursBefore = data.getIntExtra("notifyHours", -1);

                            Log.d("notify", "" + notify + " " + notifyHoursBefore);


                            Assignment newAssignment = new Assignment(name, year + "/" + month + "/" + day, hour + ":" + minute, desc);
                            newAssignment.setNotify(notify);
                            newAssignment.setNotifyHoursBefore(notifyHoursBefore);

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
                            boolean notify = data.getBooleanExtra("notify", false);
                            int notifyHoursBefore = data.getIntExtra("notifyHoursBefore", -1);

                            currAssignment.setName(name);
                            currAssignment.setDescription(desc);
                            currAssignment.changeDate(new int[] {month, day, year});
                            currAssignment.setDueDate(year + "/" + month + "/" + day);
                            currAssignment.setDueTime(hour + ":" + minute);
                            currAssignment.setNotify(notify);
                            currAssignment.setNotifyHoursBefore(notifyHoursBefore);

                            EditAssignmentToDatabase databaseUpload = new EditAssignmentToDatabase();
                            databaseUpload.execute(currAssignment);
                        }
                    }
                });

        // create recycler view for todolist
        todoListRecyclerView = view.findViewById(R.id.TodoListRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        todoListRecyclerView.setLayoutManager(linearLayoutManager);
        // add new days to recycler view if beginning or end is reached
        todoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(1) && todoListAdapter.needLaterDays) {
                    Log.d("canScrollVert", "need later days");
                    todoListAdapter.addNextYear();
                }

                if (recyclerView.canScrollVertically(-1) && todoListAdapter.needEarlierDays) {
                    Log.d("canScrollVert", "need earlier days");
                    todoListAdapter.addPrevYear();
                }

            }
        });



        // create database
        SQLiteDatabase sqLiteDatabase = getActivity().openOrCreateDatabase("assignments",Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);

        // create adapter for recycler view
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        todoListAdapter = new TodoListAdapter( calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR), dbHelper, this);
        todoListRecyclerView.setAdapter(todoListAdapter);
        todoListRecyclerView.scrollToPosition(todoListAdapter.getPositionOfDate(new int[] {calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)}));

        // selection tracker to handle selection of recycler view items
        SelectionTracker tracker = new SelectionTracker.Builder<>(
                "todolist-selection",
                todoListRecyclerView,
                new StableIdKeyProvider(todoListRecyclerView),
                new MyDetailsLookup(todoListRecyclerView),
                StorageStrategy.createLongStorage())
                .build();

        ActionMenuView actionMenuView = new ActionMenuView(getContext());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_planner_action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        switch(item.getItemId()) {
            case R.id.goToDateMenuItem:
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        DatePickerDialog goToDatePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                int position = todoListAdapter.getPositionOfDate(new int[] {m,d,y});
                                todoListRecyclerView.scrollToPosition(position);
                            }

                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                        goToDatePickerDialog.show();
                    }
                    return true;
            case R.id.clearAllAssignmentsMenuItem:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("Are you sure you want to delete all assignments? This cannot be undone.");
                alertDialogBuilder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                alertDialogBuilder.setPositiveButton("Delete All Assignments", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        todoListAdapter.clearDatabase();
                        Toast toast = new Toast(getContext());
                        toast.setText("All assignments deleted");
                        toast.show();
                    }
                });

                alertDialogBuilder.create().show();
                dbHelper.clearDatabase();
                return true;
            case R.id.addAssignmentMenuItem:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
                        int[] selectedDate = new int[] {yearPicked, monthPicked, dayPicked};

                        Intent intent = new Intent(view.getContext(), CreateAssignmentActivity.class);
                        intent.putExtra("hour", "" + hour);
                        intent.putExtra("minute", "" + minute);
                        intent.putExtra("year", "" + selectedDate[0]);
                        intent.putExtra("month", "" + selectedDate[1]);
                        intent.putExtra("day", "" + selectedDate[2]);

                        createAssignmentActivityResultLauncher.launch(intent);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                return true;
            default:
                return false;
        }

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

            // create notificaiton for assignment if user selected
            if (assignment.shouldNotify()) {
                createNotification(assignment);
            }

            todoListAdapter.updateDay(daysToUpdate);
        }
    }

    public static class AlarmBroadcastReceiver extends BroadcastReceiver {

        public static final String ACTION_ALARM_BROADCAST_RECEIVER = "action_alarm_broadcast_receiver_badgers";
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d("alarm", "alarm received");
            String name = intent.getStringExtra("name");
            int hoursBefore = intent.getIntExtra("hoursBefore", 0);
            int id = intent.getIntExtra("id", -1);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,ASSIGNMENT_NOTIFICATION_CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_baseline_assignment_24);
            builder.setContentTitle(name);
            builder.setContentText("Your assignment is due in " + hoursBefore + " hours!");
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(id, builder.build());
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

            Log.d("oldDueYear", "" + assignment.getOldDueYear());
            if (assignment.getOldDueYear() != 0) {
                daysToUpdate.add(new Integer[] {assignment.getOldDueMonth(), assignment.getOldDueDay(), assignment.getOldDueYear()});
            }
            Log.d("edit", daysToUpdate.get(0)[1] + " " + daysToUpdate.get(1)[1]);

            if (assignment.shouldNotify()) {
                createNotification(assignment);
            }

            todoListAdapter.updateDay(daysToUpdate);
            todoListAdapter.assignmentDialogFragment.dialog.cancel();

        }
    }

    public void createNotification(Assignment assignment) {
        Intent notificationIntent = new Intent(getContext(), AlarmBroadcastReceiver.class);
        notificationIntent.setAction(AlarmBroadcastReceiver.ACTION_ALARM_BROADCAST_RECEIVER);
        notificationIntent.putExtra("name", assignment.getName());
        notificationIntent.putExtra("hoursBefore", assignment.getNotifyHoursBefore());
        notificationIntent.putExtra("id", assignment.getId());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), assignment.getId(), notificationIntent, PendingIntent.FLAG_MUTABLE);

        Calendar calendar = Calendar.getInstance();
        Log.d("hour", " " + assignment.getDueHour());
        calendar.set(Calendar.YEAR, assignment.getDueYear());
        calendar.set(Calendar.MONTH, assignment.getDueMonth());
        calendar.set(Calendar.DAY_OF_MONTH, assignment.getDueDay());
        calendar.set(Calendar.HOUR_OF_DAY, assignment.getDueHour());
        calendar.set(Calendar.MINUTE, assignment.getDueMin());
        calendar.add(calendar.HOUR, -assignment.getNotifyHoursBefore());
        Log.d("alarm", "setting alarm for " + calendar.get(GregorianCalendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) +" " + calendar.get(Calendar.HOUR_OF_DAY) +":" + calendar.get(Calendar.MINUTE));

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        // subtract 500ms because the notifications seem to be sent at the very end of the minute
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 1000, alarmIntent);

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
        intent.putExtra("notify", currAssignment.shouldNotify());
        intent.putExtra("notifyHoursBefore", currAssignment.getNotifyHoursBefore());
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

            todoListAdapter.removeAssignment(assignment);
            todoListAdapter.updateDay(daysToUpdate);
            Toast toast = Toast.makeText(getContext(), "assignment deleted", Toast.LENGTH_SHORT);
            toast.show();
            todoListAdapter.assignmentDialogFragment.dialog.cancel();

            deleteNotification(assignment);
        }
    }

    public void deleteAssignment(Assignment assignment) {
        DeleteAssignmentToDatabase databaseDelete = new DeleteAssignmentToDatabase();
        databaseDelete.execute(assignment);
    }

    public void deleteNotification(Assignment assignment) {
        Intent notificationIntent = new Intent(getContext(), AlarmBroadcastReceiver.class);
        notificationIntent.setAction(AlarmBroadcastReceiver.ACTION_ALARM_BROADCAST_RECEIVER);
        notificationIntent.putExtra("name", assignment.getName());
        notificationIntent.putExtra("hoursBefore", assignment.getNotifyHoursBefore());
        notificationIntent.putExtra("id", assignment.getId());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), assignment.getId(), notificationIntent, PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
        alarmIntent.cancel();

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

                    //myViewHolder.changeActivated();

                    ItemDetails myDetails = myViewHolder.getItemDetails();
                    return myDetails;
                }
            }
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onSaveInstanceState(null);
    }


}
