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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.lunarcalendar.SpecialDay;
import com.example.lunarcalendar.SpecialDayDialog;
import com.example.lunarcalendar.SpecialDayListActivity;
import com.example.lunarcalendar.NotificationSettingsActivity;

public class MainActivity extends AppCompatActivity implements SpecialDayDialog.OnSpecialDayCreatedListener {

    private Spinner spinnerMonth, spinnerYear;
    private Button btnViewCalendar;
    private TextView tvTitle, tvCalendarInfo;
    private GridView gridViewHeader, gridViewCalendar;
    
    private CalendarAdapter calendarAdapter;
    private List<CalendarCell> calendarCells;
    
    public int selectedMonth;
    public int selectedYear;
    private int currentDay, currentMonth, currentYear;

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

        // Display current month calendar
        displayCalendar(selectedMonth, selectedYear);

        // Schedule special day notifications
        SpecialDayNotificationHelper specialDayHelper = new SpecialDayNotificationHelper(this);
        specialDayHelper.scheduleDailyCheck(this);

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

        // Notification Settings button
        Button btnNotificationSettings = findViewById(R.id.btnNotificationSettings);
        btnNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationSettingsActivity.class);
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

    public void showSpecialDayDialog(int day, int month, int year) {
        // Convert solar date to lunar date for display
        int[] lunarDate = LunarCalendar.convertSolarToLunar(day, month, year);
        int lunarDay = lunarDate[0];
        int lunarMonth = lunarDate[1];
        boolean isLeapMonth = lunarDate[3] == 1;

        SpecialDayDialog dialog = SpecialDayDialog.newInstance(day, month, year);
        dialog.show(getSupportFragmentManager(), "SpecialDayDialog");
    }

    @Override
    public void onSpecialDayCreated(String name, int day, int month, int year) {
        // Save special day to database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SpecialDay specialDay = new SpecialDay();
        specialDay.setName(name);
        specialDay.setDay(day);
        specialDay.setMonth(month);
        specialDay.setCreatedAt(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
        
        dbHelper.addSpecialDay(specialDay);
        Toast.makeText(this, "Đã tạo ngày đặc biệt: " + name, Toast.LENGTH_SHORT).show();
        
        // Cập nhật lại giao diện ngay lập tức thay vì phải bấm nút
        displayCalendar(selectedMonth, selectedYear);
    }

    private void setupMonthSpinner() {
        // Create month array (1-12)
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
        // Create year array (1900-2100)
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
        // Update title
        tvTitle.setText(getString(R.string.calendar_title, month, year));
        tvCalendarInfo.setText(getString(R.string.view_calendar, month, year));

        // Clear previous data
        calendarCells.clear();

        // Get first day of month
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        
        // Convert Sunday=1 to Monday=1 format
        // In Java Calendar: Sunday=1, Monday=2, ..., Saturday=7
        // We want: Monday=1, Tuesday=2, ..., Sunday=7
        int startDayOffset = (firstDayOfWeek == 1) ? 6 : firstDayOfWeek - 2;

        // Get days in month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Get previous month info
        Calendar prevMonth = (Calendar) calendar.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        int prevMonthNum = prevMonth.get(Calendar.MONTH) + 1;
        int prevYear = prevMonth.get(Calendar.YEAR);

        // Get next month info
        Calendar nextMonth = (Calendar) calendar.clone();
        nextMonth.add(Calendar.MONTH, 1);
        int nextMonthNum = nextMonth.get(Calendar.MONTH) + 1;
        int nextYear = nextMonth.get(Calendar.YEAR);

        // Add previous month's trailing days
        for (int i = startDayOffset - 1; i >= 0; i--) {
            int day = daysInPrevMonth - i;
            int[] lunarDate = LunarCalendar.convertSolarToLunar(day, prevMonthNum, prevYear);
            calendarCells.add(new CalendarCell(day, lunarDate[0], lunarDate[1], false, false));
        }

        // Add current month's days
        for (int day = 1; day <= daysInMonth; day++) {
            int[] lunarDate = LunarCalendar.convertSolarToLunar(day, month, year);
            boolean isToday = (day == currentDay && month == currentMonth && year == currentYear);
            calendarCells.add(new CalendarCell(day, lunarDate[0], lunarDate[1], isToday, true));
        }

        // Add next month's leading days to complete the grid
        int totalCells = calendarCells.size();
        int remainingCells = (totalCells % 7 == 0) ? 0 : 7 - (totalCells % 7);
        
        // Always show at least 5 rows (35 cells)
        if (totalCells + remainingCells < 35) {
            remainingCells += 7;
        }

        for (int day = 1; day <= remainingCells; day++) {
            int[] lunarDate = LunarCalendar.convertSolarToLunar(day, nextMonthNum, nextYear);
            calendarCells.add(new CalendarCell(day, lunarDate[0], lunarDate[1], false, false));
        }

        // Update adapter
        calendarAdapter.updateData(calendarCells);
    }
}
