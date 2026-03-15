package com.example.lunarcalendar;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class NotificationSettingsActivity extends AppCompatActivity {
    
    private TextView tvCurrentTime;
    private Button btnSetTime, btnResetDefault;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "NotificationPrefs";
    private static final String KEY_HOUR = "notification_hour";
    private static final String KEY_MINUTE = "notification_minute";
    private static final int DEFAULT_HOUR = 18;
    private static final int DEFAULT_MINUTE = 0;
    
    private int currentHour, currentMinute;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        
        // Initialize views
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        btnSetTime = findViewById(R.id.btnSetTime);
        btnResetDefault = findViewById(R.id.btnResetDefault);
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        // Load saved time or use default
        loadSavedTime();
        
        // Update display
        updateDisplay();
        
        // Set click listeners
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        
        btnResetDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetToDefault();
            }
        });
    }
    
    private void loadSavedTime() {
        currentHour = sharedPreferences.getInt(KEY_HOUR, DEFAULT_HOUR);
        currentMinute = sharedPreferences.getInt(KEY_MINUTE, DEFAULT_MINUTE);
    }
    
    private void saveTime() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HOUR, currentHour);
        editor.putInt(KEY_MINUTE, currentMinute);
        editor.apply();
        
        // Reschedule notification with new time
        SpecialDayNotificationHelper helper = new SpecialDayNotificationHelper(this);
        helper.scheduleDailyCheck(this);
        
        Toast.makeText(this, "Đã lưu thời gian thông báo", Toast.LENGTH_SHORT).show();
    }
    
    private void updateDisplay() {
        String timeText = String.format("%02d:%02d", currentHour, currentMinute);
        tvCurrentTime.setText("Thời gian hiện tại: " + timeText);
    }
    
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    currentHour = hourOfDay;
                    currentMinute = minute;
                    updateDisplay();
                    saveTime();
                }
            },
            currentHour,
            currentMinute,
            true
        );
        timePickerDialog.setTitle("Chọn thời gian thông báo");
        timePickerDialog.show();
    }
    
    private void resetToDefault() {
        currentHour = DEFAULT_HOUR;
        currentMinute = DEFAULT_MINUTE;
        updateDisplay();
        saveTime();
        Toast.makeText(this, "Đã đặt lại thời gian mặc định (18:00)", Toast.LENGTH_SHORT).show();
    }
}