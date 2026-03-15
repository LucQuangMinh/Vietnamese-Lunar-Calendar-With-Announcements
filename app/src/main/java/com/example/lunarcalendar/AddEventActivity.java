package com.example.lunarcalendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {
    
    private EditText etEventName, etSolarDay, etSolarMonth, etSolarYear, etDescription;
    private Spinner spinnerEventType;
    private TextView tvLunarDay, tvLunarMonth, tvLunarYear;
    private CheckBox cbNotification, cbYearly;
    private Button btnDatePicker, btnSave, btnCancel;
    
    private DatabaseHelper dbHelper;
    private EventManager eventManager;
    private Event currentEvent;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize database and event manager
        dbHelper = new DatabaseHelper(this);
        eventManager = new EventManager(this);

        // Initialize views
        etEventName = findViewById(R.id.etEventName);
        etSolarDay = findViewById(R.id.etSolarDay);
        etSolarMonth = findViewById(R.id.etSolarMonth);
        etSolarYear = findViewById(R.id.etSolarYear);
        etDescription = findViewById(R.id.etDescription);
        spinnerEventType = findViewById(R.id.spinnerEventType);
        tvLunarDay = findViewById(R.id.tvLunarDay);
        tvLunarMonth = findViewById(R.id.tvLunarMonth);
        tvLunarYear = findViewById(R.id.tvLunarYear);
        cbNotification = findViewById(R.id.cbNotification);
        cbYearly = findViewById(R.id.cbYearly);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Setup event types spinner
        setupEventTypesSpinner();

        // Check if we're editing an existing event
        long eventId = getIntent().getLongExtra("event_id", -1);
        if (eventId != -1) {
            isEditMode = true;
            loadEventForEdit(eventId);
        } else {
            // Set default values for new event
            setDefaultValues();
        }

        // Set up click listeners
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupEventTypesSpinner() {
        String[] eventTypes = eventManager.getEventTypes();
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(adapter);
    }

    private void setDefaultValues() {
        Calendar calendar = Calendar.getInstance();
        etSolarDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        etSolarMonth.setText(String.valueOf(calendar.get(Calendar.MONTH) + 1));
        etSolarYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        
        updateLunarDate();
    }

    private void loadEventForEdit(long eventId) {
        currentEvent = dbHelper.getEvent(eventId);
        if (currentEvent != null) {
            etEventName.setText(currentEvent.getName());
            etSolarDay.setText(String.valueOf(currentEvent.getSolarDay()));
            etSolarMonth.setText(String.valueOf(currentEvent.getSolarMonth()));
            etSolarYear.setText(String.valueOf(currentEvent.getSolarYear()));
            etDescription.setText(currentEvent.getDescription());
            cbNotification.setChecked(currentEvent.isHasNotification());
            cbYearly.setChecked(currentEvent.isYearly());
            
            // Set event type in spinner
            String[] eventTypes = eventManager.getEventTypes();
            for (int i = 0; i < eventTypes.length; i++) {
                if (eventTypes[i].equals(currentEvent.getEventType())) {
                    spinnerEventType.setSelection(i);
                    break;
                }
            }
            
            updateLunarDate();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    etSolarDay.setText(String.valueOf(selectedDay));
                    etSolarMonth.setText(String.valueOf(selectedMonth + 1));
                    etSolarYear.setText(String.valueOf(selectedYear));
                    updateLunarDate();
                }, year, month, day);
        
        datePickerDialog.show();
    }

    private void updateLunarDate() {
        try {
            int day = Integer.parseInt(etSolarDay.getText().toString());
            int month = Integer.parseInt(etSolarMonth.getText().toString());
            int year = Integer.parseInt(etSolarYear.getText().toString());
            
            int[] lunarDate = LunarCalendar.convertSolarToLunar(day, month, year);
            
            tvLunarDay.setText(String.valueOf(lunarDate[0]));
            tvLunarMonth.setText(String.valueOf(lunarDate[1]));
            tvLunarYear.setText(String.valueOf(lunarDate[2]));
            
        } catch (NumberFormatException e) {
            // Clear lunar date if input is invalid
            tvLunarDay.setText("");
            tvLunarMonth.setText("");
            tvLunarYear.setText("");
        }
    }

    private void saveEvent() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        String name = etEventName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String eventType = spinnerEventType.getSelectedItem().toString();
        boolean hasNotification = cbNotification.isChecked();
        boolean isYearly = cbYearly.isChecked();
        
        int solarDay = Integer.parseInt(etSolarDay.getText().toString());
        int solarMonth = Integer.parseInt(etSolarMonth.getText().toString());
        int solarYear = Integer.parseInt(etSolarYear.getText().toString());
        
        int[] lunarDate = LunarCalendar.convertSolarToLunar(solarDay, solarMonth, solarYear);
        int lunarDay = lunarDate[0];
        int lunarMonth = lunarDate[1];
        int lunarYear = lunarDate[2];
        boolean isLeapMonth = lunarDate[3] == 1;

        if (isEditMode) {
            // Update existing event
            currentEvent.setName(name);
            currentEvent.setDescription(description);
            currentEvent.setEventType(eventType);
            currentEvent.setHasNotification(hasNotification);
            currentEvent.setSolarDay(solarDay);
            currentEvent.setSolarMonth(solarMonth);
            currentEvent.setSolarYear(solarYear);
            currentEvent.setLunarDay(lunarDay);
            currentEvent.setLunarMonth(lunarMonth);
            currentEvent.setLunarYear(lunarYear);
            currentEvent.setLeapMonth(isLeapMonth);

            int result = eventManager.updateEvent(currentEvent);
            if (result > 0) {
                Toast.makeText(this, "Cập nhật sự kiện thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Create new event
            Event event = new Event(solarDay, solarMonth, solarYear, lunarDay, lunarMonth, lunarYear,
                    name, description, eventType, hasNotification, isLeapMonth);
            event.setYearly(isYearly);
            
            long result = eventManager.addEvent(event);
            if (result > 0) {
                Toast.makeText(this, "Thêm sự kiện thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput() {
        if (etEventName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên sự kiện", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int day = Integer.parseInt(etSolarDay.getText().toString());
            int month = Integer.parseInt(etSolarMonth.getText().toString());
            int year = Integer.parseInt(etSolarYear.getText().toString());

            if (day < 1 || day > 31) {
                Toast.makeText(this, "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (month < 1 || month > 12) {
                Toast.makeText(this, "Tháng không hợp lệ", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (year < 1900 || year > 2100) {
                Toast.makeText(this, "Năm không hợp lệ", Toast.LENGTH_SHORT).show();
                return false;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập ngày tháng năm hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}