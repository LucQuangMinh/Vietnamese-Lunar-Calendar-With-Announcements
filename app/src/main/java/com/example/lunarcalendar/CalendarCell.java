package com.example.lunarcalendar;

public class CalendarCell {
    private int solarDay;
    private int lunarDay;
    private int lunarMonth;
    private boolean isCurrentDay;
    private boolean isCurrentMonth;
    private boolean hasEvent;
    private java.util.List<com.example.lunarcalendar.Event> eventList;

    public CalendarCell(int solarDay, int lunarDay, int lunarMonth, boolean isCurrentDay, boolean isCurrentMonth) {
        this.solarDay = solarDay;
        this.lunarDay = lunarDay;
        this.lunarMonth = lunarMonth;
        this.isCurrentDay = isCurrentDay;
        this.isCurrentMonth = isCurrentMonth;
        this.hasEvent = false;
        this.eventList = new java.util.ArrayList<>();
    }

    public int getSolarDay() {
        return solarDay;
    }

    public void setSolarDay(int solarDay) {
        this.solarDay = solarDay;
    }

    public int getLunarDay() {
        return lunarDay;
    }

    public void setLunarDay(int lunarDay) {
        this.lunarDay = lunarDay;
    }

    public int getLunarMonth() {
        return lunarMonth;
    }

    public void setLunarMonth(int lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    public boolean isCurrentDay() {
        return isCurrentDay;
    }

    public void setCurrentDay(boolean currentDay) {
        isCurrentDay = currentDay;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        isCurrentMonth = currentMonth;
    }

    public boolean hasEvent() {
        return hasEvent;
    }

    public void setHasEvent(boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public java.util.List<com.example.lunarcalendar.Event> getEventList() {
        return eventList;
    }

    public void setEventList(java.util.List<com.example.lunarcalendar.Event> eventList) {
        this.eventList = eventList;
        this.hasEvent = eventList != null && !eventList.isEmpty();
    }

    public void addEvent(com.example.lunarcalendar.Event event) {
        if (this.eventList == null) {
            this.eventList = new java.util.ArrayList<>();
        }
        this.eventList.add(event);
        this.hasEvent = true;
    }
}
