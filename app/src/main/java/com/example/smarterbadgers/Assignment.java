package com.example.smarterbadgers;

import android.util.Log;

import java.util.Calendar;

public class Assignment {
    public static final String COMPLETED_ASSIGNMENT_PREFERENCE_KEY = "completed_assignment_preference_key";
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
    private int id;
    private int oldDueYear, oldDueMonth, oldDueDay;
    boolean notify;
    private int notifyHoursBefore;
    private boolean completed;
    private boolean hidden;
    private boolean expanded;

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

    /**
     *
     * @param name
     * @param dueDate must be in format YYYY/MM/DD where MM is zero based (jan=0,feb=1,...)
     * @param dueTime must be in format HH:MM
     * @param description
     * @param id
     */
    public Assignment(String name, String dueDate, String dueTime, String description, int id) {
        this.id = id;
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

    public Assignment(String name, String description, int dueYear, int dueMonth, int dueDay, int dueHour, int dueMin, int id) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public boolean getCompleted() {
        return completed;
    }

    public boolean getHidden() {
        return hidden;
    }

    public boolean getExpanded() {
        return expanded;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String newName) {
        name = newName;
    }

    public void setDueDate(String newDueDate) {
        dueDate = newDueDate;
        String[] dueDateArray = dueDate.split("/");
        dueYear = Integer.valueOf(dueDateArray[0]);
        dueMonth = Integer.valueOf(dueDateArray[1]);
        dueDay = Integer.valueOf(dueDateArray[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(dueYear, dueMonth, dueDay);
        dueDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
    }

    public void setDueTime(String newDueTime) {
        dueTime = newDueTime;
        String[] dueTimeArray = dueTime.split(":");
        dueHour = Integer.valueOf(dueTimeArray[0]);
        dueMin = Integer.valueOf(dueTimeArray[1]);
    }

    public void toggleExpanded() {
        expanded = !expanded;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void changeDate(int[] mdy) {
        oldDueDay = dueDay;
        oldDueMonth = dueMonth;
        oldDueYear = dueYear;

        dueMonth = mdy[0];
        dueDay = mdy[1];
        dueYear = mdy[2];

        dueDate = oldDueYear + "/" + oldDueMonth + "/" + oldDueDay;
    }

    public void setDueYear(int dueYear) {
        this.dueYear = dueYear;
    }

    public void setDueMonth(int dueMonth) {
        this.dueMonth = dueMonth;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }

    public void setDueHour(int dueHour) {
        this.dueHour = dueHour;
    }

    public void setDueMin(int dueMin) {
        this.dueMin = dueMin;
    }

    public void setDueDayOfYear(int dueDayOfYear) {
        this.dueDayOfYear = dueDayOfYear;
    }

    @Override
    public String toString() {
        //return String.format("%s\n\t%s", this.name, this.description);
        return this.name;
    }

    public int getOldDueYear() {
        return oldDueYear;
    }

    public int getOldDueMonth() {
        return oldDueMonth;
    }

    public int getOldDueDay() {
        return oldDueDay;
    }

    public boolean shouldNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public int getNotifyHoursBefore() {
        return notifyHoursBefore;
    }

    public void setNotifyHoursBefore(int hours) {
        this.notifyHoursBefore = hours;
    }
}
