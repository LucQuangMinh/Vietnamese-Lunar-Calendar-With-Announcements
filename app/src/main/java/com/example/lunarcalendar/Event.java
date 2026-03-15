package com.example.lunarcalendar;

public class Event {
    private int id;
    private int solarDay;
    private int solarMonth;
    private int solarYear;
    private int lunarDay;
    private int lunarMonth;
    private int lunarYear;
    private String name;
    private String description;
    private String eventType;
    private boolean hasNotification;
    private boolean isLeapMonth;
    private boolean isYearly; // Lặp hàng năm theo lịch âm

    public Event() {
    }

    public Event(int solarDay, int solarMonth, int solarYear, int lunarDay, 
                 int lunarMonth, int lunarYear, String name, String description, 
                 String eventType, boolean hasNotification, boolean isLeapMonth) {
        this.solarDay = solarDay;
        this.solarMonth = solarMonth;
        this.solarYear = solarYear;
        this.lunarDay = lunarDay;
        this.lunarMonth = lunarMonth;
        this.lunarYear = lunarYear;
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.hasNotification = hasNotification;
        this.isLeapMonth = isLeapMonth;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSolarDay() { return solarDay; }
    public void setSolarDay(int solarDay) { this.solarDay = solarDay; }

    public int getSolarMonth() { return solarMonth; }
    public void setSolarMonth(int solarMonth) { this.solarMonth = solarMonth; }

    public int getSolarYear() { return solarYear; }
    public void setSolarYear(int solarYear) { this.solarYear = solarYear; }

    public int getLunarDay() { return lunarDay; }
    public void setLunarDay(int lunarDay) { this.lunarDay = lunarDay; }

    public int getLunarMonth() { return lunarMonth; }
    public void setLunarMonth(int lunarMonth) { this.lunarMonth = lunarMonth; }

    public int getLunarYear() { return lunarYear; }
    public void setLunarYear(int lunarYear) { this.lunarYear = lunarYear; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public boolean isHasNotification() { return hasNotification; }
    public void setHasNotification(boolean hasNotification) { this.hasNotification = hasNotification; }

    public boolean isLeapMonth() { return isLeapMonth; }
    public void setLeapMonth(boolean leapMonth) { isLeapMonth = leapMonth; }

    public boolean isYearly() { return isYearly; }
    public void setYearly(boolean yearly) { isYearly = yearly; }
}
