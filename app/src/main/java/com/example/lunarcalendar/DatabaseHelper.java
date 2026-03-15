package com.example.lunarcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lunar_calendar.db";
    private static final int DATABASE_VERSION = 2;
    
    // Table name
    private static final String TABLE_EVENTS = "events";
    
    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SOLAR_DAY = "solar_day";
    private static final String COLUMN_SOLAR_MONTH = "solar_month";
    private static final String COLUMN_SOLAR_YEAR = "solar_year";
    private static final String COLUMN_LUNAR_DAY = "lunar_day";
    private static final String COLUMN_LUNAR_MONTH = "lunar_month";
    private static final String COLUMN_LUNAR_YEAR = "lunar_year";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_EVENT_TYPE = "event_type";
    private static final String COLUMN_HAS_NOTIFICATION = "has_notification";
    private static final String COLUMN_IS_LEAP_MONTH = "is_leap_month";
    private static final String COLUMN_IS_YEARLY = "is_yearly";

    // Create table SQL query
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SOLAR_DAY + " INTEGER,"
            + COLUMN_SOLAR_MONTH + " INTEGER,"
            + COLUMN_SOLAR_YEAR + " INTEGER,"
            + COLUMN_LUNAR_DAY + " INTEGER,"
            + COLUMN_LUNAR_MONTH + " INTEGER,"
            + COLUMN_LUNAR_YEAR + " INTEGER,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_EVENT_TYPE + " TEXT,"
            + COLUMN_HAS_NOTIFICATION + " INTEGER,"
            + COLUMN_IS_LEAP_MONTH + " INTEGER,"
            + COLUMN_IS_YEARLY + " INTEGER"
            + ")";

    // Special days table
    private static final String TABLE_SPECIAL_DAYS = "special_days";
    private static final String COLUMN_SPECIAL_DAY_NAME = "name";
    private static final String COLUMN_SPECIAL_DAY_DAY = "day";
    private static final String COLUMN_SPECIAL_DAY_MONTH = "month";
    private static final String COLUMN_SPECIAL_DAY_CREATED_AT = "created_at";

    private static final String CREATE_TABLE_SPECIAL_DAYS = "CREATE TABLE " + TABLE_SPECIAL_DAYS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SPECIAL_DAY_NAME + " TEXT,"
            + COLUMN_SPECIAL_DAY_DAY + " INTEGER,"
            + COLUMN_SPECIAL_DAY_MONTH + " INTEGER,"
            + COLUMN_SPECIAL_DAY_CREATED_AT + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_SPECIAL_DAYS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add is_yearly column for version 2
            db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN " + COLUMN_IS_YEARLY + " INTEGER DEFAULT 0");
        }
    }

    // Insert event
    public long addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SOLAR_DAY, event.getSolarDay());
        values.put(COLUMN_SOLAR_MONTH, event.getSolarMonth());
        values.put(COLUMN_SOLAR_YEAR, event.getSolarYear());
        values.put(COLUMN_LUNAR_DAY, event.getLunarDay());
        values.put(COLUMN_LUNAR_MONTH, event.getLunarMonth());
        values.put(COLUMN_LUNAR_YEAR, event.getLunarYear());
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_EVENT_TYPE, event.getEventType());
        values.put(COLUMN_HAS_NOTIFICATION, event.isHasNotification() ? 1 : 0);
        values.put(COLUMN_IS_LEAP_MONTH, event.isLeapMonth() ? 1 : 0);
        values.put(COLUMN_IS_YEARLY, event.isYearly() ? 1 : 0);

        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    // Get event by ID
    public Event getEvent(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_ID, COLUMN_SOLAR_DAY, COLUMN_SOLAR_MONTH,
                        COLUMN_SOLAR_YEAR, COLUMN_LUNAR_DAY, COLUMN_LUNAR_MONTH, COLUMN_LUNAR_YEAR,
                        COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_EVENT_TYPE, COLUMN_HAS_NOTIFICATION,
                        COLUMN_IS_LEAP_MONTH}, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        
        if (cursor != null)
            cursor.moveToFirst();

        Event event = new Event(
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getInt(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getInt(10) == 1,
                cursor.getInt(11) == 1
        );
        event.setId(cursor.getInt(0));
        event.setYearly(cursor.getInt(12) == 1);
        cursor.close();
        db.close();
        return event;
    }

    // Get all events
    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(0));
                event.setSolarDay(cursor.getInt(1));
                event.setSolarMonth(cursor.getInt(2));
                event.setSolarYear(cursor.getInt(3));
                event.setLunarDay(cursor.getInt(4));
                event.setLunarMonth(cursor.getInt(5));
                event.setLunarYear(cursor.getInt(6));
                event.setName(cursor.getString(7));
                event.setDescription(cursor.getString(8));
                event.setEventType(cursor.getString(9));
                event.setHasNotification(cursor.getInt(10) == 1);
                event.setLeapMonth(cursor.getInt(11) == 1);
                event.setYearly(cursor.getInt(12) == 1);
                
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventList;
    }

    // Get events by solar date
    public List<Event> getEventsBySolarDate(int day, int month, int year) {
        List<Event> eventList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " 
                + COLUMN_SOLAR_DAY + "=? AND " + COLUMN_SOLAR_MONTH + "=? AND " + COLUMN_SOLAR_YEAR + "=?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(day), 
                String.valueOf(month), String.valueOf(year)});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(0));
                event.setSolarDay(cursor.getInt(1));
                event.setSolarMonth(cursor.getInt(2));
                event.setSolarYear(cursor.getInt(3));
                event.setLunarDay(cursor.getInt(4));
                event.setLunarMonth(cursor.getInt(5));
                event.setLunarYear(cursor.getInt(6));
                event.setName(cursor.getString(7));
                event.setDescription(cursor.getString(8));
                event.setEventType(cursor.getString(9));
                event.setHasNotification(cursor.getInt(10) == 1);
                event.setLeapMonth(cursor.getInt(11) == 1);
                event.setYearly(cursor.getInt(12) == 1);
                
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventList;
    }

    // Get events by lunar date (for yearly events, ignore year)
    public List<Event> getEventsByLunarDate(int day, int month, int year, boolean isLeapMonth) {
        List<Event> eventList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " 
                + COLUMN_LUNAR_DAY + "=? AND " + COLUMN_LUNAR_MONTH + "=? AND " 
                + COLUMN_IS_LEAP_MONTH + "=? AND " + COLUMN_IS_YEARLY + "=?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(day), 
                String.valueOf(month), String.valueOf(isLeapMonth ? 1 : 0), "1"});

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(0));
                event.setSolarDay(cursor.getInt(1));
                event.setSolarMonth(cursor.getInt(2));
                event.setSolarYear(cursor.getInt(3));
                event.setLunarDay(cursor.getInt(4));
                event.setLunarMonth(cursor.getInt(5));
                event.setLunarYear(cursor.getInt(6));
                event.setName(cursor.getString(7));
                event.setDescription(cursor.getString(8));
                event.setEventType(cursor.getString(9));
                event.setHasNotification(cursor.getInt(10) == 1);
                event.setLeapMonth(cursor.getInt(11) == 1);
                event.setYearly(cursor.getInt(12) == 1);
                
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventList;
    }

    // Update event
    public int updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SOLAR_DAY, event.getSolarDay());
        values.put(COLUMN_SOLAR_MONTH, event.getSolarMonth());
        values.put(COLUMN_SOLAR_YEAR, event.getSolarYear());
        values.put(COLUMN_LUNAR_DAY, event.getLunarDay());
        values.put(COLUMN_LUNAR_MONTH, event.getLunarMonth());
        values.put(COLUMN_LUNAR_YEAR, event.getLunarYear());
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_EVENT_TYPE, event.getEventType());
        values.put(COLUMN_HAS_NOTIFICATION, event.isHasNotification() ? 1 : 0);
        values.put(COLUMN_IS_LEAP_MONTH, event.isLeapMonth() ? 1 : 0);
        values.put(COLUMN_IS_YEARLY, event.isYearly() ? 1 : 0);

        return db.update(TABLE_EVENTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(event.getId())});
    }

    // Delete event
    public void deleteEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(event.getId())});
        db.close();
    }

    // Delete events by solar date
    public void deleteEventsBySolarDate(int day, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COLUMN_SOLAR_DAY + " = ? AND " + COLUMN_SOLAR_MONTH + " = ? AND " + COLUMN_SOLAR_YEAR + " = ?",
                new String[]{String.valueOf(day), String.valueOf(month), String.valueOf(year)});
        db.close();
    }

    // Get events with notifications enabled
    public List<Event> getEventsWithNotifications() {
        List<Event> eventList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_HAS_NOTIFICATION + " = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(cursor.getInt(0));
                event.setSolarDay(cursor.getInt(1));
                event.setSolarMonth(cursor.getInt(2));
                event.setSolarYear(cursor.getInt(3));
                event.setLunarDay(cursor.getInt(4));
                event.setLunarMonth(cursor.getInt(5));
                event.setLunarYear(cursor.getInt(6));
                event.setName(cursor.getString(7));
                event.setDescription(cursor.getString(8));
                event.setEventType(cursor.getString(9));
                event.setHasNotification(cursor.getInt(10) == 1);
                event.setLeapMonth(cursor.getInt(11) == 1);
                event.setYearly(cursor.getInt(12) == 1);
                
                eventList.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventList;
    }

    // Special days methods
    public long addSpecialDay(SpecialDay specialDay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SPECIAL_DAY_NAME, specialDay.getName());
        values.put(COLUMN_SPECIAL_DAY_DAY, specialDay.getDay());
        values.put(COLUMN_SPECIAL_DAY_MONTH, specialDay.getMonth());
        values.put(COLUMN_SPECIAL_DAY_CREATED_AT, specialDay.getCreatedAt());

        long id = db.insert(TABLE_SPECIAL_DAYS, null, values);
        db.close();
        return id;
    }

    public List<SpecialDay> getAllSpecialDays() {
        List<SpecialDay> specialDayList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SPECIAL_DAYS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SpecialDay specialDay = new SpecialDay();
                specialDay.setId(cursor.getInt(0));
                specialDay.setName(cursor.getString(1));
                specialDay.setDay(cursor.getInt(2));
                specialDay.setMonth(cursor.getInt(3));
                specialDay.setCreatedAt(cursor.getString(4));
                
                specialDayList.add(specialDay);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return specialDayList;
    }

    public SpecialDay getSpecialDayByDate(int day, int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        SpecialDay specialDay = null;
        
        try {
            cursor = db.query(TABLE_SPECIAL_DAYS, new String[]{COLUMN_ID, COLUMN_SPECIAL_DAY_NAME,
                            COLUMN_SPECIAL_DAY_DAY, COLUMN_SPECIAL_DAY_MONTH, COLUMN_SPECIAL_DAY_CREATED_AT}, 
                    COLUMN_SPECIAL_DAY_DAY + "=? AND " + COLUMN_SPECIAL_DAY_MONTH + "=?",
                    new String[]{String.valueOf(day), String.valueOf(month)}, null, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                specialDay = new SpecialDay(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
            }
        } finally {
            // Đảm bảo cursor luôn được đóng
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        
        return specialDay;
    }

    /**
     * Get special day by lunar date (for yearly recurring special days)
     */
    public SpecialDay getSpecialDayByLunarDate(int lunarDay, int lunarMonth, boolean isLeapMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        SpecialDay specialDay = null;
        
        try {
            // Query by lunar date - we need to convert from stored solar date to check
            // Since SpecialDay stores solar date, we need to convert it to lunar for comparison
            String selectQuery = "SELECT * FROM " + TABLE_SPECIAL_DAYS;
            cursor = db.rawQuery(selectQuery, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int storedDay = cursor.getInt(2); // COLUMN_SPECIAL_DAY_DAY
                    int storedMonth = cursor.getInt(3); // COLUMN_SPECIAL_DAY_MONTH
                    
                    // Convert stored solar date to lunar date
                    int[] lunarDate = LunarCalendar.convertSolarToLunar(storedDay, storedMonth, 2026); // Use any year for conversion
                    int storedLunarDay = lunarDate[0];
                    int storedLunarMonth = lunarDate[1];
                    boolean storedIsLeapMonth = lunarDate[3] == 1;
                    
                    // Check if matches the target lunar date
                    if (storedLunarDay == lunarDay && storedLunarMonth == lunarMonth && storedIsLeapMonth == isLeapMonth) {
                        specialDay = new SpecialDay(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getInt(2),
                                cursor.getInt(3),
                                cursor.getString(4)
                        );
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        
        return specialDay;
    }

    public void deleteSpecialDay(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SPECIAL_DAYS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}