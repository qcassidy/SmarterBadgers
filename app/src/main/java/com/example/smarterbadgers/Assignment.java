package com.example.smarterbadgers;

public class Assignment {
    private String name;
    private String dueDate;
    private String dueTime;
    private String description;

    public Assignment() {

    }

    public Assignment(String name, String dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    public Assignment(String name, String dueDate, String dueTime) {
        this.name = name;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
    }

    public Assignment(String name, String dueDate, String dueTime, String description) {
        this.name = name;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.description = description;
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
