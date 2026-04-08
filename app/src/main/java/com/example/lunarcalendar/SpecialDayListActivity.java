package com.example.lunarcalendar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class SpecialDayListActivity extends AppCompatActivity implements SpecialDayDialog.OnSpecialDayActionListener {
    
    private ListView lvSpecialDays;
    private Button btnAddSpecialDay;
    private TextView tvEmptyMessage;
    private SpecialDayAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<SpecialDay> specialDays;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_day_list);
        
        // Initialize views
        lvSpecialDays = findViewById(R.id.lvSpecialDays);
        btnAddSpecialDay = findViewById(R.id.btnAddSpecialDay);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        
        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
        
        // Setup click listeners
        btnAddSpecialDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to main activity to create special day
            }
        });
        
        // Load special days
        loadSpecialDays();
    }
    
    private void loadSpecialDays() {
        specialDays = dbHelper.getAllSpecialDays();
        
        if (specialDays.isEmpty()) {
            lvSpecialDays.setVisibility(View.GONE);
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            lvSpecialDays.setVisibility(View.VISIBLE);
            tvEmptyMessage.setVisibility(View.GONE);
            
            adapter = new SpecialDayAdapter(this, specialDays);
            lvSpecialDays.setAdapter(adapter);
            
            lvSpecialDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SpecialDay clickedDay = specialDays.get(position);
                    SpecialDayDialog dialog = SpecialDayDialog.newInstance(
                            clickedDay.getDay(), 
                            clickedDay.getMonth(), 
                            2026, // Dùng tạm năm nay vì SpecialDay không lưu năm
                            clickedDay.getId(), 
                            clickedDay.getName(), 
                            clickedDay.getNotes(), 
                            clickedDay.getNotificationTime()
                    );
                    dialog.show(getSupportFragmentManager(), "SpecialDayDialog");
                }
            });
        }
    }
    
    @Override
    public void onSpecialDayCreated(String name, String notes, int day, int month, int year, String notificationTime) {
        // Function này không dùng ở activity này vì tạo mới nằm ở màn hình chính
        // Tuy nhiên bắt buộc phải override do implement interface
    }
    
    @Override
    public void onSpecialDayUpdated(int id, String newName, String newNotes, String notificationTime) {
        // Cập nhật thông tin trong DB
        android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("name", newName);
        values.put("notes", newNotes);
        values.put("notification_time", notificationTime);
        db.update("special_days", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();

        Toast.makeText(this, "Đã cập nhật: " + newName, Toast.LENGTH_SHORT).show();
        loadSpecialDays(); // Refresh list
        SpecialDayNotificationHelper.scheduleDailyPlanner(this); // Lên lịch lại
    }
    
    @Override
    public void onSpecialDayDeleted(int id) {
        // Delete special day
        dbHelper.deleteSpecialDay(id);
        Toast.makeText(this, "Đã xóa ngày đặc biệt", Toast.LENGTH_SHORT).show();
        loadSpecialDays(); // Refresh list
        SpecialDayNotificationHelper.scheduleDailyPlanner(this); // Lên lịch lại
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadSpecialDays(); // Refresh list when returning to activity
    }
}