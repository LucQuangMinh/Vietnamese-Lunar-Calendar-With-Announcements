package com.example.lunarcalendar;

public class SpecialDay {
    private int id;
    private String name;
    private int day;
    private int month;
    private String createdAt;
    private String notes;

    public SpecialDay() {}

    public SpecialDay(String name, String notes, int day, int month, String createdAt) {
        this.name = name;
        this.notes = notes;
        this.day = day;
        this.month = month;
        this.createdAt = createdAt;
    }

    public SpecialDay(int id, String name, String notes, int day, int month, String createdAt) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.day = day;
        this.month = month;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return name + " - " + day + "/" + month;
    }
}