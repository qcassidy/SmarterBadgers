package com.example.smarterbadgers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DBHelper {

    SQLiteDatabase sqLiteDatabase;
    ArrayList<Day> lastRetrieval;

    public DBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        createTable();
    }

    public void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS assignments (id INTEGER PRIMARY KEY, name TEXT, dueDate TEXT, dueTime TEXT, description TEXT, " +
                "dueYear INT, dueMonth INT, dueDay INT, dueHour INT, dueMin INT, dueDayOfYear INT)");
    }

    public void closeTable() {
        sqLiteDatabase.close();
    }

    public ArrayList<Assignment> getAllAssignments() {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * from assignments", null);

        int nameIndex = c.getColumnIndex("name");
        int dueDateIndex = c.getColumnIndex("dueDate");
        int dueTimeIndex = c.getColumnIndex("dueTime");
        int descriptionIndex = c.getColumnIndex("description");

        c.moveToFirst();

        ArrayList<Assignment> assignmentsList = new ArrayList<>();

        while (!c.isAfterLast()) {

            String name = c.getString(nameIndex);
            String dueDate = c.getString(dueDateIndex);
            String dueTime = c.getString(dueTimeIndex);
            String description = c.getString(descriptionIndex);

            Assignment currAssignment = new Assignment(name, dueDate, dueTime, description);
            assignmentsList.add(currAssignment);
            assignmentsList.add(currAssignment);
            c.moveToNext();
        }
        c.close();

        return assignmentsList;
    }

    public ArrayList<Assignment> getAssignmentsFromDay(int mdy[]) {
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * from assignments WHERE assignments.dueMonth = '%d' and " +
                "assignments.dueDay = '%d' and assignments.dueYear = '%d'", mdy[0], mdy[1], mdy[2]), null);

        int nameIndex = c.getColumnIndex("name");
        int dueDateIndex = c.getColumnIndex("dueDate");
        int dueTimeIndex = c.getColumnIndex("dueTime");
        int descriptionIndex = c.getColumnIndex("description");

        c.moveToFirst();

        ArrayList<Assignment> assignmentsList = new ArrayList<>();

        while (!c.isAfterLast()) {

            String name = c.getString(nameIndex);
            String dueDate = c.getString(dueDateIndex);
            String dueTime = c.getString(dueTimeIndex);
            String description = c.getString(descriptionIndex);

            Assignment currAssignment = new Assignment(name, dueDate, dueTime, description);
            assignmentsList.add(currAssignment);
            Log.d("assignment", "found assignment " + currAssignment.getName());
            c.moveToNext();
        }
        c.close();

        return assignmentsList;
    }

    public ArrayList<Day> getAssignmentsFromYear(int year) {
        Cursor c = sqLiteDatabase.rawQuery(String.format(Locale.getDefault(), "SELECT * from assignments WHERE assignments.dueYear = '%d'", year), null);

        int nameIndex = c.getColumnIndex("name");
        int dueDateIndex = c.getColumnIndex("dueDate");
        int dueTimeIndex = c.getColumnIndex("dueTime");
        int descriptionIndex = c.getColumnIndex("description");
        int dueDayIndex = c.getColumnIndex("dueDay");
        int dueDayOfYearIndex = c.getColumnIndex("dueDayOfYear");

        c.moveToFirst();

        ArrayList<Assignment> assignmentsList = new ArrayList<>();
        ArrayList<Day> days = new ArrayList<>();

        int numDays = 365;
        if (year % 4 != 0) {
        }
        else if (year % 100 != 0) {
            numDays = 366;
        }
        else if (year % 400 != 0) {
        }
        else {
            numDays = 366;
        }


        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1);
        for (int i = 0; i < numDays; i++) {
            Day newDay = new Day(new int[] { calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR) } );
            days.add(newDay);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        while (!c.isAfterLast()) {

            String name = c.getString(nameIndex);
            String dueDate = c.getString(dueDateIndex);
            String dueTime = c.getString(dueTimeIndex);
            String description = c.getString(descriptionIndex);
            int dueDay = Integer.parseInt(c.getString(dueDayIndex));
            int dueDayOfYear = Integer.parseInt(c.getString(dueDayOfYearIndex)) - 1;

            Assignment currAssignment = new Assignment(name, dueDate, dueTime, description);

            Log.d("assignment", String.format("adding %s (%s) to %s", name, dueDate, days.get(dueDayOfYear)));
            Log.d("assignment", "dueDayOfYear: " + dueDayOfYear);
            days.get(dueDayOfYear).addAssignment(currAssignment);
            c.moveToNext();
        }
        c.close();

        return days;
    }

    public void saveAssignment(Assignment currAssignment) {
        sqLiteDatabase.execSQL(String.format(Locale.getDefault(),"INSERT INTO assignments (name, dueDate, dueTime, description, dueYear, dueMonth, dueDay, dueHour, dueMin, dueDayOfYear)" +
                        " VALUES ('%s', '%s', '%s', '%s', '%d', '%d', '%d', '%d', '%d', '%d')",
                currAssignment.getName(), currAssignment.getDueDate(), currAssignment.getDueTime(), currAssignment.getDescription(),
                currAssignment.getDueYear(), currAssignment.getDueMonth(), currAssignment.getDueDay(), currAssignment.getDueHour(),
                currAssignment.getDueMin(), currAssignment.getDueDayOfYear()));

        Log.d("assignment", "Saved assignment: " + currAssignment.getName());
    }

    public void updateAssignment(Assignment currAssignment, String newName, String newDueDate) {
        sqLiteDatabase.execSQL(String.format("UPDATE assignments set name = '%s', dueDate = '%s', dueTime = '%s', description = '%s'" +
                        " where name = '%s' and dueDate = '%s' and dueTime = '%s' and description = '%s'",
                newName, newDueDate, currAssignment.getName(), currAssignment.getDueDate()));
    }

    public void clearDatabase() {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS assignments");
    }


}
