package com.example.smarterbadgers;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlannerFragment extends Fragment {

    CalendarFragment calendarFragment;
    View view;

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

        Intent intent = new Intent(view.getContext(), CreateAssignmentActivity.class);
        intent.putExtra("year", "" + selectedDate[0]);
        intent.putExtra("month", "" + selectedDate[1]);
        intent.putExtra("day", "" + selectedDate[2]);
        intent.putExtra("hour", "" + hour);
        intent.putExtra("minute", "" + minute);

        startActivity(intent);
    }
}