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
        int idIndex = c.getColumnIndex("id");

        c.moveToFirst();

        ArrayList<Assignment> assignmentsList = new ArrayList<>();

        while (!c.isAfterLast()) {

            String name = c.getString(nameIndex);
            String dueDate = c.getString(dueDateIndex);
            String dueTime = c.getString(dueTimeIndex);
            String description = c.getString(descriptionIndex);
            int id = Integer.parseInt(c.getString(idIndex));

            Assignment currAssignment = new Assignment(name, dueDate, dueTime, description, id);
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

        int idIndex = c.getColumnIndex("id");
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
            int id = Integer.parseInt(c.getString(idIndex));

            Assignment currAssignment = new Assignment(name, dueDate, dueTime, description, id);
            assignmentsList.add(currAssignment);
            Log.d("assignment", "found assignment " + id + " " + currAssignment.getName());
            c.moveToNext();
        }
        c.close();

        return assignmentsList;
    }

    public static int getNumberOfDaysInYear(int year) {
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

        return numDays;
    }

    public ArrayList<Day> getAssignmentsFromYearRange(int startYear, int endYear) {
        StringBuilder builder = new StringBuilder();



        // create query for sqllite database
        Calendar calendar = Calendar.getInstance();
        ArrayList<Day> days = new ArrayList<>();
        builder.append("SELECT * from assignments WHERE ");
        for (int i = startYear; i <= endYear; i++) {
            builder.append("assignments.dueYear = " + i);

            if (i != endYear) {
                builder.append(" OR ");
            }

            // create Day object for each day of a year
            int numDays = getNumberOfDaysInYear(i);
            calendar.set(i, 0, 1);
            for (int j = 0; j < numDays; j++) {
                Day newDay = new Day(new int[] { calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR) } );
                days.add(newDay);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        builder.append(" ORDER BY dueYear ASC, dueMonth ASC, dueDay ASC, dueHour ASC, dueMin ASC");
        String query = builder.toString();
        Log.d("getrange", query);
        Log.d("getrangesize", days.size() + "");

        // get selected range of assignments
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        int idIndex = c.getColumnIndex("id");
        int nameIndex = c.getColumnIndex("name");
        int dueDateIndex = c.getColumnIndex("dueDate");
        int dueTimeIndex = c.getColumnIndex("dueTime");
        int descriptionIndex = c.getColumnIndex("description");
        int dueDayIndex = c.getColumnIndex("dueDay");
        int dueDayOfYearIndex = c.getColumnIndex("dueDayOfYear");
        int dueYearIndex = c.getColumnIndex("dueYear");

        c.moveToFirst();
        int dayOfYearOffset = 0;
        int previousYear = startYear;
        while (!c.isAfterLast()) {

            String name = c.getString(nameIndex);
            String dueDate = c.getString(dueDateIndex);
            String dueTime = c.getString(dueTimeIndex);
            String description = c.getString(descriptionIndex);
            int dueDay = Integer.parseInt(c.getString(dueDayIndex));
            int dueDayOfYear = Integer.parseInt(c.getString(dueDayOfYearIndex)) - 1;
            int id = Integer.parseInt(c.getString(idIndex));
            int dueYear = Integer.parseInt(c.getString(dueYearIndex));

            if (dueYear != previousYear) {
                dayOfYearOffset += getNumberOfDaysInYear(previousYear);
                previousYear = dueYear;
            }

            Assignment currAssignment = new Assignment(name, dueDate, dueTime, description, id);

            //Log.d("assignment", String.format("adding %s (%s) to %s", name, dueDate, days.get(dueDayOfYear + dayOfYearOffset)));
            //Log.d("assignment", "dueDayOfYear: " + dueDayOfYear);
            //Log.d("assignment", "found assignment " + id + " " + currAssignment.getName());


            days.get(dueDayOfYear + dayOfYearOffset).addAssignment(currAssignment);
            c.moveToNext();
        }
        c.close();

        return days;
    }

    public ArrayList<Day> getAssignmentsFromYear(int year) {
        Cursor c = sqLiteDatabase.rawQuery(String.format(Locale.getDefault(), "SELECT * from assignments WHERE assignments.dueYear = '%d'", year), null);

        int idIndex = c.getColumnIndex("id");
        int nameIndex = c.getColumnIndex("name");
        int dueDateIndex = c.getColumnIndex("dueDate");
        int dueTimeIndex = c.getColumnIndex("dueTime");
        int descriptionIndex = c.getColumnIndex("description");
        int dueDayIndex = c.getColumnIndex("dueDay");
        int dueDayOfYearIndex = c.getColumnIndex("dueDayOfYear");

        c.moveToFirst();

        ArrayList<Assignment> assignmentsList = new ArrayList<>();
        ArrayList<Day> days = new ArrayList<>();

        int numDays = getNumberOfDaysInYear(year);

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
            int id = Integer.parseInt(c.getString(idIndex));

            Assignment currAssignment = new Assignment(name, dueDate, dueTime, description, id);

            Log.d("assignment", String.format("adding %s (%s) to %s", name, dueDate, days.get(dueDayOfYear)));
            Log.d("assignment", "dueDayOfYear: " + dueDayOfYear);
            Log.d("assignment", "found assignment " + id + " " + currAssignment.getName());
            days.get(dueDayOfYear).addAssignment(currAssignment);
            c.moveToNext();
        }
        c.close();

        return days;
    }

    public void saveAssignment(Assignment currAssignment) {
        // TODO deal with apostrophes and other potential breaking characters

        sqLiteDatabase.execSQL(String.format(Locale.getDefault(),"INSERT INTO assignments (name, dueDate, dueTime, description, dueYear, dueMonth, dueDay, dueHour, dueMin, dueDayOfYear)" +
                        " VALUES ('%s', '%s', '%s', '%s', '%d', '%d', '%d', '%d', '%d', '%d')",
                currAssignment.getName(), currAssignment.getDueDate(), currAssignment.getDueTime(), currAssignment.getDescription(),
                currAssignment.getDueYear(), currAssignment.getDueMonth(), currAssignment.getDueDay(), currAssignment.getDueHour(),
                currAssignment.getDueMin(), currAssignment.getDueDayOfYear()));

        // get and set id
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM assignments ORDER BY id DESC LIMIT 1", null);
        c.moveToFirst();
        String id = c.getString(0);
        currAssignment.setId(Integer.parseInt(id));
    }

    public void updateAssignment(Assignment currAssignment) {
        sqLiteDatabase.execSQL(String.format(Locale.getDefault(), "UPDATE assignments set name = '%s', dueDate = '%s', dueTime = '%s', description = '%s'"
                         + ", dueYear = '%s', dueMonth = '%d', dueDay = '%d', dueHour = '%d', dueMin = '%d', dueDayOfYear = '%d'"
                         + " where id = '%d'",
                currAssignment.getName(), currAssignment.getDueDate(), currAssignment.getDueTime(), currAssignment.getDescription(), currAssignment.getDueYear(),
                currAssignment.getDueMonth(), currAssignment.getDueDay(), currAssignment.getDueHour(), currAssignment.getDueMin(), currAssignment.getDueDayOfYear(), currAssignment.getId()));

    }

    public void clearDatabase() {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS assignments");
    }

    public void deleteAssignment(Assignment currAssignment) {
        sqLiteDatabase.execSQL("DELETE FROM assignments WHERE id = " + currAssignment.getId());
    }


}
