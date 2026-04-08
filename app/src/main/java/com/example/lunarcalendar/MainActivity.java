package com.example.lunarcalendar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.lunarcalendar.SpecialDay;
import com.example.lunarcalendar.SpecialDayDialog;
import com.example.lunarcalendar.SpecialDayListActivity;

public class MainActivity extends AppCompatActivity implements SpecialDayDialog.OnSpecialDayActionListener {

    private Spinner spinnerMonth, spinnerYear;
    private Button btnViewCalendar;
    private TextView tvTitle, tvCalendarInfo;
    private GridView gridViewHeader, gridViewCalendar;
    
    private CalendarAdapter calendarAdapter;
    private List<CalendarCell> calendarCells;
    
    public int selectedMonth;
    public int selectedYear;
    private int currentDay, currentMonth, currentYear;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        btnViewCalendar = findViewById(R.id.btnViewCalendar);
        tvTitle = findViewById(R.id.tvTitle);
        tvCalendarInfo = findViewById(R.id.tvCalendarInfo);
        gridViewHeader = findViewById(R.id.gridViewHeader);
        gridViewCalendar = findViewById(R.id.gridViewCalendar);

        // Get current date
        Calendar today = Calendar.getInstance();
        currentDay = today.get(Calendar.DAY_OF_MONTH);
        currentMonth = today.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        currentYear = today.get(Calendar.YEAR);

        // Set default selection to current month and year
        selectedMonth = currentMonth;
        selectedYear = currentYear;

        // Setup spinners
        setupMonthSpinner();
        setupYearSpinner();

        // Setup header (days of week)
        setupHeader();

        // Setup calendar grid
        calendarCells = new ArrayList<>();
        calendarAdapter = new CalendarAdapter(this, calendarCells);
        gridViewCalendar.setAdapter(calendarAdapter);

        // Setup swipe gesture
        setupSwipeGesture();

        // Display current month calendar
        displayCalendar(selectedMonth, selectedYear);

        // Schedule special day notifications
        SpecialDayNotificationHelper.scheduleDailyPlanner(this);

        // Button click listeners
        btnViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCalendar(selectedMonth, selectedYear);
            }
        });

        // Special Day List button
        Button btnSpecialDayList = findViewById(R.id.btnSpecialDayList);
        btnSpecialDayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SpecialDayListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh calendar when returning from other activities
        displayCalendar(selectedMonth, selectedYear);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector != null && gestureDetector.onTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setupSwipeGesture() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                goToPreviousMonth();
                            } else {
                                goToNextMonth();
                            }
                            result = true;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        });
    }

    private void goToPreviousMonth() {
        selectedMonth--;
        if (selectedMonth < 1) {
            selectedMonth = 12;
            selectedYear--;
            if (selectedYear >= 1900) {
                spinnerYear.setSelection(selectedYear - 1900);
            }
        }
        spinnerMonth.setSelection(selectedMonth - 1);
        displayCalendar(selectedMonth, selectedYear);
    }

    private void goToNextMonth() {
        selectedMonth++;
        if (selectedMonth > 12) {
            selectedMonth = 1;
            selectedYear++;
            if (selectedYear <= 2100) {
                spinnerYear.setSelection(selectedYear - 1900);
            }
        }
        spinnerMonth.setSelection(selectedMonth - 1);
        displayCalendar(selectedMonth, selectedYear);
    }

    public void showSpecialDayDialog(int day, int month, int year) {
        showSpecialDayDialog(day, month, year, -1, "", "", "18:00");
    }

    public void showSpecialDayDialog(int day, int month, int year, int specialDayId, String specialDayName, String specialDayNotes, String notificationTime) {
        SpecialDayDialog dialog = SpecialDayDialog.newInstance(day, month, year, specialDayId, specialDayName, specialDayNotes, notificationTime);
        dialog.show(getSupportFragmentManager(), "SpecialDayDialog");
    }

    @Override
    public void onSpecialDayCreated(String name, String notes, int day, int month, int year, String notificationTime) {
        // Save special day to database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SpecialDay specialDay = new SpecialDay();
        specialDay.setName(name);
        specialDay.setNotes(notes);
        specialDay.setNotificationTime(notificationTime);
        specialDay.setDay(day);
        specialDay.setMonth(month);
        specialDay.setCreatedAt(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
        
        dbHelper.addSpecialDay(specialDay);
        Toast.makeText(this, "Đã tạo ngày đặc biệt: " + name, Toast.LENGTH_SHORT).show();
        
        displayCalendar(selectedMonth, selectedYear);
        SpecialDayNotificationHelper.scheduleDailyPlanner(this);
    }

    @Override
    public void onSpecialDayUpdated(int id, String newName, String newNotes, String notificationTime) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("name", newName);
        values.put("notes", newNotes);
        values.put("notification_time", notificationTime);
        db.update("special_days", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();

        Toast.makeText(this, "Đã cập nhật: " + newName, Toast.LENGTH_SHORT).show();
        displayCalendar(selectedMonth, selectedYear);
        SpecialDayNotificationHelper.scheduleDailyPlanner(this);
    }

    @Override
    public void onSpecialDayDeleted(int id) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.deleteSpecialDay(id);
        Toast.makeText(this, "Đã xóa ngày đặc biệt", Toast.LENGTH_SHORT).show();
        displayCalendar(selectedMonth, selectedYear);
        SpecialDayNotificationHelper.scheduleDailyPlanner(this);
    }

    private void setupMonthSpinner() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = String.valueOf(i + 1);
        }

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(currentMonth - 1);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupYearSpinner() {
        List<String> years = new ArrayList<>();
        for (int i = 1900; i <= 2100; i++) {
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(currentYear - 1900);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = 1900 + position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupHeader() {
        String[] daysOfWeek = {
                getString(R.string.monday),
                getString(R.string.tuesday),
                getString(R.string.wednesday),
                getString(R.string.thursday),
                getString(R.string.friday),
                getString(R.string.saturday),
                getString(R.string.sunday)
        };

        ArrayAdapter<String> headerAdapter = new ArrayAdapter<String>(this,
                R.layout.header_cell, R.id.tvHeaderDay, daysOfWeek);
        gridViewHeader.setAdapter(headerAdapter);
    }

    private void displayCalendar(int month, int year) {
        tvTitle.setText(getString(R.string.calendar_title, month, year));
        tvCalendarInfo.setText(getString(R.string.view_calendar, month, year));

        calendarCells.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int startDayOffset = (firstDayOfWeek == 1) ? 6 : firstDayOfWeek - 2;

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar prevMonth = (Calendar) calendar.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        int prevMonthNum = prevMonth.get(Calendar.MONTH) + 1;
        int prevYear = prevMonth.get(Calendar.YEAR);

        Calendar nextMonth = (Calendar) calendar.clone();
        nextMonth.add(Calendar.MONTH, 1);
        int nextMonthNum = nextMonth.get(Calendar.MONTH) + 1;
        int nextYear = nextMonth.get(Calendar.YEAR);

        for (int i = startDayOffset - 1; i >= 0; i--) {
            int day = daysInPrevMonth - i;
            int[] lunarDate = LunarCalendar.convertSolarToLunar(day, prevMonthNum, prevYear);
            calendarCells.add(new CalendarCell(day, lunarDate[0], lunarDate[1], false, false));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            int[] lunarDate = LunarCalendar.convertSolarToLunar(day, month, year);
            boolean isToday = (day == currentDay && month == currentMonth && year == currentYear);
            calendarCells.add(new CalendarCell(day, lunarDate[0], lunarDate[1], isToday, true));
        }

        int totalCells = calendarCells.size();
        int remainingCells = (totalCells % 7 == 0) ? 0 : 7 - (totalCells % 7);
        if (totalCells + remainingCells < 35) {
            remainingCells += 7;
        }

        for (int day = 1; day <= remainingCells; day++) {
            int[] lunarDate = LunarCalendar.convertSolarToLunar(day, nextMonthNum, nextYear);
            calendarCells.add(new CalendarCell(day, lunarDate[0], lunarDate[1], false, false));
        }

        calendarAdapter.updateData(calendarCells);
    }
}
