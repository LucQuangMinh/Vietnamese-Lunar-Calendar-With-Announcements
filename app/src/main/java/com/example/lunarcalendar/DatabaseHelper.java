package com.example.lunarcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lunar_calendar.db";
    private static final int DATABASE_VERSION = 4;
    
    // Table name
    private static final String TABLE_EVENTS = "events";

    // Special days table
    private static final String TABLE_SPECIAL_DAYS = "special_days";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SPECIAL_DAY_NAME = "name";
    private static final String COLUMN_SPECIAL_DAY_DAY = "day";
    private static final String COLUMN_SPECIAL_DAY_MONTH = "month";
    private static final String COLUMN_SPECIAL_DAY_CREATED_AT = "created_at";
    private static final String COLUMN_SPECIAL_DAY_NOTES = "notes";
    private static final String COLUMN_SPECIAL_DAY_NOTIFICATION_TIME = "notification_time";

    private static final String CREATE_TABLE_SPECIAL_DAYS = "CREATE TABLE " + TABLE_SPECIAL_DAYS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SPECIAL_DAY_NAME + " TEXT,"
            + COLUMN_SPECIAL_DAY_DAY + " INTEGER,"
            + COLUMN_SPECIAL_DAY_MONTH + " INTEGER,"
            + COLUMN_SPECIAL_DAY_CREATED_AT + " TEXT,"
            + COLUMN_SPECIAL_DAY_NOTES + " TEXT,"
            + COLUMN_SPECIAL_DAY_NOTIFICATION_TIME + " TEXT DEFAULT '18:00'"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // We no longer create the events table, but we will leave the older version upgrade logic
        // in case existing users upgrade. However, for new installs, it's just special days.
        db.execSQL(CREATE_TABLE_SPECIAL_DAYS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We ignore version 2 upgrade for the events table layout, but we keep it safe by wrapping it if the table exists
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_EVENTS + " ADD COLUMN is_yearly INTEGER DEFAULT 0");
            } catch (Exception e) {
                // Ignore if table doesn't exist
            }
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_SPECIAL_DAYS + " ADD COLUMN " + COLUMN_SPECIAL_DAY_NOTES + " TEXT");
            } catch (Exception e) {
                // Ignore
            }
        }
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_SPECIAL_DAYS + " ADD COLUMN " + COLUMN_SPECIAL_DAY_NOTIFICATION_TIME + " TEXT DEFAULT '18:00'");
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    // Special days methods
    public long addSpecialDay(SpecialDay specialDay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SPECIAL_DAY_NAME, specialDay.getName());
        values.put(COLUMN_SPECIAL_DAY_DAY, specialDay.getDay());
        values.put(COLUMN_SPECIAL_DAY_MONTH, specialDay.getMonth());
        values.put(COLUMN_SPECIAL_DAY_CREATED_AT, specialDay.getCreatedAt());
        values.put(COLUMN_SPECIAL_DAY_NOTES, specialDay.getNotes());
        values.put(COLUMN_SPECIAL_DAY_NOTIFICATION_TIME, specialDay.getNotificationTime());

        long id = db.insert(TABLE_SPECIAL_DAYS, null, values);
        db.close();
        return id;
    }

    public int updateSpecialDay(SpecialDay specialDay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_SPECIAL_DAY_NAME, specialDay.getName());
        values.put(COLUMN_SPECIAL_DAY_DAY, specialDay.getDay());
        values.put(COLUMN_SPECIAL_DAY_MONTH, specialDay.getMonth());
        values.put(COLUMN_SPECIAL_DAY_NOTES, specialDay.getNotes());
        values.put(COLUMN_SPECIAL_DAY_NOTIFICATION_TIME, specialDay.getNotificationTime());

        int res = db.update(TABLE_SPECIAL_DAYS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(specialDay.getId())});
        db.close();
        return res;
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
                int notesIdx = cursor.getColumnIndex(COLUMN_SPECIAL_DAY_NOTES);
                if (notesIdx != -1) {
                    specialDay.setNotes(cursor.getString(notesIdx));
                }
                int timeIdx = cursor.getColumnIndex(COLUMN_SPECIAL_DAY_NOTIFICATION_TIME);
                if (timeIdx != -1) {
                    specialDay.setNotificationTime(cursor.getString(timeIdx));
                }
                
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
                            COLUMN_SPECIAL_DAY_DAY, COLUMN_SPECIAL_DAY_MONTH, COLUMN_SPECIAL_DAY_CREATED_AT, COLUMN_SPECIAL_DAY_NOTES, COLUMN_SPECIAL_DAY_NOTIFICATION_TIME}, 
                    COLUMN_SPECIAL_DAY_DAY + "=? AND " + COLUMN_SPECIAL_DAY_MONTH + "=?",
                    new String[]{String.valueOf(day), String.valueOf(month)}, null, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                String notes = "";
                int notesIdx = cursor.getColumnIndex(COLUMN_SPECIAL_DAY_NOTES);
                if (notesIdx != -1) notes = cursor.getString(notesIdx);
                
                String time = "18:00";
                int timeIdx = cursor.getColumnIndex(COLUMN_SPECIAL_DAY_NOTIFICATION_TIME);
                if (timeIdx != -1) time = cursor.getString(timeIdx);

                specialDay = new SpecialDay(
                        cursor.getInt(0),     // id
                        cursor.getString(1),  // name
                        notes,                // notes
                        cursor.getInt(2),     // day
                        cursor.getInt(3),     // month
                        cursor.getString(4),  // createdAt
                        time                  // notificationTime
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        
        return specialDay;
    }

    public SpecialDay getSpecialDayByLunarDate(int lunarDay, int lunarMonth, boolean isLeapMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        SpecialDay specialDay = null;
        
        try {
            String selectQuery = "SELECT * FROM " + TABLE_SPECIAL_DAYS;
            cursor = db.rawQuery(selectQuery, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int storedDay = cursor.getInt(2); 
                    int storedMonth = cursor.getInt(3); 
                    
                    int[] lunarDate = LunarCalendar.convertSolarToLunar(storedDay, storedMonth, 2026); 
                    int storedLunarDay = lunarDate[0];
                    int storedLunarMonth = lunarDate[1];
                    boolean storedIsLeapMonth = lunarDate[3] == 1;
                    
                    if (storedLunarDay == lunarDay && storedLunarMonth == lunarMonth && storedIsLeapMonth == isLeapMonth) {
                        int notesIdx = cursor.getColumnIndex(COLUMN_SPECIAL_DAY_NOTES);
                        String notes = notesIdx != -1 ? cursor.getString(notesIdx) : "";
                        int timeIdx = cursor.getColumnIndex(COLUMN_SPECIAL_DAY_NOTIFICATION_TIME);
                        String time = timeIdx != -1 ? cursor.getString(timeIdx) : "18:00";
                        specialDay = new SpecialDay(
                                cursor.getInt(0),
                                cursor.getString(1),
                                notes,
                                cursor.getInt(2),
                                cursor.getInt(3),
                                cursor.getString(4),
                                time
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