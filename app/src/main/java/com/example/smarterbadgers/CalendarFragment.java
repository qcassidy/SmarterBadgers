package com.example.smarterbadgers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

/**
 * Fragent used to show monthly calendar on Planner page
 */
public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView =  inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = inflatedView.findViewById(R.id.calendarView);

        // set default date
        Date currDate = new Date(calendarView.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currDate);
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = day;
            }
        });

        return inflatedView;
    }

    /**
     * Returns date currently selected on calendar in format {year, month, day}
     */
    public int[] getSelectedDate() {
        int[] date = new int[] {selectedYear, selectedMonth, selectedDay};
        return date;
    }
}