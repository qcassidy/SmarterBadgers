package com.example.smarterbadgers;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class Day {
    private int d;
    private int m;
    private int y;
    private ArrayList<Assignment> assignments;
    private Calendar calendar;
    private String[] daysOfWeek = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private int dayIndex;

    public Day(int[] mdy) {
        assignments = new ArrayList<>();
        m = mdy[0];
        d = mdy[1];
        y = mdy[2];

        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, m);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public ArrayList<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(ArrayList<Assignment> newAssignments) {
        assignments = newAssignments;
    }

    public void removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
    }

    /**
     *
     * @return returns day of week, 0 based. e.g. monday = 0, tuesday = 1...
     */
    public int getDayOfWeek() {
        return dayIndex;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public String toString() {
        return String.format("%s - %d/%d/%d", daysOfWeek[dayIndex], m + 1, d, y);
    }
}
