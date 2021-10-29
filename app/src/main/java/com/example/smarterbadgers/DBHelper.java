package com.example.smarterbadgers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper {

    SQLiteDatabase sqLiteDatabase;

    public DBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        createTable();
    }

    public void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS assignments (id INTEGER PRIMARY KEY, name TEXT, dueDate TEXT, dueTime TEXT, description TEXT)");
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

    public ArrayList<Assignment> getAssignmentsFromDay(String date) {
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * from assignments WHERE assignments.dueDate = '%s'", date), null);

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
            c.moveToNext();
        }
        c.close();

        return assignmentsList;
    }

    public void saveAssignment(Assignment currAssignment) {
        sqLiteDatabase.execSQL(String.format("INSERT INTO assignments (name, dueDate, dueTime, description) VALUES ('%s', '%s', '%s', '%s')",
                currAssignment.getName(), currAssignment.getDueDate(), currAssignment.getDueTime(), currAssignment.getDescription()));

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
