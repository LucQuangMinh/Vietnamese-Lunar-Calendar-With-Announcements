package com.example.lunarcalendar;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class EventManager {
    private DatabaseHelper dbHelper;
    private NotificationHelper notificationHelper;

    public EventManager(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.notificationHelper = new NotificationHelper(context);
    }

    /**
     * Add a new event and schedule notification if enabled
     */
    public long addEvent(Event event) {
        long eventId = dbHelper.addEvent(event);
        
        // Schedule notification if enabled
        if (event.isHasNotification()) {
            notificationHelper.scheduleNotification(event);
        }
        
        return eventId;
    }

    /**
     * Update an existing event and reschedule notification
     */
    public int updateEvent(Event event) {
        int result = dbHelper.updateEvent(event);
        
        // Cancel existing notification and schedule new one if enabled
        notificationHelper.cancelNotification(event.getId());
        if (event.isHasNotification()) {
            notificationHelper.scheduleNotification(event);
        }
        
        return result;
    }

    /**
     * Delete an event and cancel its notification
     */
    public void deleteEvent(Event event) {
        dbHelper.deleteEvent(event);
        notificationHelper.cancelNotification(event.getId());
    }

    /**
     * Get all events for a specific solar date
     */
    public List<Event> getEventsForSolarDate(int day, int month, int year) {
        return dbHelper.getEventsBySolarDate(day, month, year);
    }

    /**
     * Get all events for a specific lunar date
     */
    public List<Event> getEventsForLunarDate(int day, int month, int year, boolean isLeapMonth) {
        return dbHelper.getEventsByLunarDate(day, month, year, isLeapMonth);
    }

    /**
     * Get all events
     */
    public List<Event> getAllEvents() {
        return dbHelper.getAllEvents();
    }

    /**
     * Check if a specific solar date has any events
     */
    public boolean hasEventsOnSolarDate(int day, int month, int year) {
        List<Event> events = getEventsForSolarDate(day, month, year);
        return !events.isEmpty();
    }

    /**
     * Check if a specific lunar date has any events
     */
    public boolean hasEventsOnLunarDate(int day, int month, int year, boolean isLeapMonth) {
        List<Event> events = getEventsForLunarDate(day, month, year, isLeapMonth);
        return !events.isEmpty();
    }

    /**
     * Check if a specific lunar date has any yearly recurring events
     * This method checks for events that repeat yearly based on lunar date
     */
    public boolean hasYearlyEventsOnLunarDate(int day, int month, boolean isLeapMonth) {
        List<Event> allEvents = dbHelper.getAllEvents();
        for (Event event : allEvents) {
            if (event.isYearly() && 
                event.getLunarDay() == day && 
                event.getLunarMonth() == month && 
                event.isLeapMonth() == isLeapMonth) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get yearly recurring events for a specific lunar date
     */
    public List<Event> getYearlyEventsForLunarDate(int day, int month, boolean isLeapMonth) {
        List<Event> allEvents = dbHelper.getAllEvents();
        List<Event> yearlyEvents = new java.util.ArrayList<>();
        
        for (Event event : allEvents) {
            if (event.isYearly() && 
                event.getLunarDay() == day && 
                event.getLunarMonth() == month && 
                event.isLeapMonth() == isLeapMonth) {
                yearlyEvents.add(event);
            }
        }
        
        return yearlyEvents;
    }

    /**
     * Get events with notifications enabled
     */
    public List<Event> getEventsWithNotifications() {
        return dbHelper.getEventsWithNotifications();
    }

    /**
     * Create a new event from solar date
     */
    public Event createEventFromSolarDate(int solarDay, int solarMonth, int solarYear, 
                                        String name, String description, 
                                        String eventType, boolean hasNotification) {
        // Convert solar date to lunar date
        int[] lunarDate = LunarCalendar.convertSolarToLunar(solarDay, solarMonth, solarYear);
        int lunarDay = lunarDate[0];
        int lunarMonth = lunarDate[1];
        int lunarYear = lunarDate[2];
        boolean isLeapMonth = lunarDate[3] == 1;

        return new Event(solarDay, solarMonth, solarYear, lunarDay, lunarMonth, lunarYear,
                        name, description, eventType, hasNotification, isLeapMonth);
    }

    /**
     * Create a new event from lunar date
     */
    public Event createEventFromLunarDate(int lunarDay, int lunarMonth, int lunarYear, 
                                        boolean isLeapMonth, String name, 
                                        String description, String eventType, 
                                        boolean hasNotification) {
        // Note: For now, we'll set solar date to 0,0,0 as we don't have reverse conversion
        // In a full implementation, you might want to add reverse conversion or let user input solar date
        return new Event(0, 0, 0, lunarDay, lunarMonth, lunarYear,
                        name, description, eventType, hasNotification, isLeapMonth);
    }

    /**
     * Get event types for spinner
     */
    public String[] getEventTypes() {
        return new String[]{
            "Sinh nhật",
            "Kỷ niệm",
            "Công việc",
            "Cá nhân",
            "Gia đình",
            "Khác"
        };
    }

    /**
     * Get event type color for display
     */
    public int getEventTypeColor(String eventType) {
        switch (eventType) {
            case "Sinh nhật":
                return android.graphics.Color.parseColor("#E91E63"); // Pink
            case "Kỷ niệm":
                return android.graphics.Color.parseColor("#9C27B0"); // Purple
            case "Công việc":
                return android.graphics.Color.parseColor("#2196F3"); // Blue
            case "Cá nhân":
                return android.graphics.Color.parseColor("#4CAF50"); // Green
            case "Gia đình":
                return android.graphics.Color.parseColor("#FF9800"); // Orange
            default:
                return android.graphics.Color.parseColor("#607D8B"); // Gray
        }
    }
}