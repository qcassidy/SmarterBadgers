package com.example.smarterbadgers;

import android.util.Log;

import java.util.Calendar;

public class Assignment {
    private String name;
    private String dueDate;
    private String dueTime;
    private String description;
    private int dueYear;
    private int dueMonth;
    private int dueDay;
    private int dueHour;
    private int dueMin;
    private int dueDayOfYear;

    public Assignment(String name, String dueDate, String dueTime, String description) {
        this.name = name;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.description = description;

        String[] dueDateArray = dueDate.split("/");
        dueYear = Integer.valueOf(dueDateArray[0]);
        dueMonth = Integer.valueOf(dueDateArray[1]);
        dueDay = Integer.valueOf(dueDateArray[2]);

        String[] dueTimeArray = dueTime.split(":");
        dueHour = Integer.valueOf(dueTimeArray[0]);
        dueMin = Integer.valueOf(dueTimeArray[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(dueYear, dueMonth, dueDay);
        dueDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    }

    public Assignment(String name, String description, int dueYear, int dueMonth, int dueDay, int dueHour, int dueMin) {
        this.name = name;
        this.description = description;
        this.dueYear = dueYear;
        this.dueMonth = dueMonth;
        this.dueDay = dueDay;
        this.dueHour = dueHour;
        this.dueMin = dueMin;

        this.dueDate = dueMonth + "/" + dueDay + "/" + dueYear;
        this.dueTime = dueHour + ":" + dueMin;
    }

    public String getName() {
        return name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public String getDescription() {
        return description;
    }

    public int getDueDay() {
        return dueDay;
    }

    public int getDueHour() {
        return dueHour;
    }

    public int getDueMin() {
        return dueMin;
    }

    public int getDueMonth() {
        return dueMonth;
    }

    public int getDueYear() {
        return dueYear;
    }

    public int getDueDayOfYear() {
        return dueDayOfYear;
    }

    public void setName(String newName) {
        name = newName;
    }
    public void setDueDate(String newDueDate) {
        dueDate = newDueDate;
    }
    public void setDueTime(String newDueTime) {
        dueTime = newDueTime;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }
}
