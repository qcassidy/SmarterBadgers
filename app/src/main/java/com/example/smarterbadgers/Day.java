package com.example.smarterbadgers;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Day {
    private int d;
    private int m;
    private int y;
    private ArrayList<Assignment> assignments;

    public Day(int[] mdy) {
        assignments = new ArrayList<>();
        m = mdy[0];
        d = mdy[1];
        y = mdy[2];
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

    @Override
    public String toString() {
        return String.format("%d/%d/%d", m + 1, d, y);
    }
}
