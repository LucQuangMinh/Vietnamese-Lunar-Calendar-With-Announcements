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

public class SpecialDayListActivity extends AppCompatActivity implements SpecialDayAdapter.OnDeleteClickListener {
    
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
            
            adapter = new SpecialDayAdapter(this, specialDays, this);
            lvSpecialDays.setAdapter(adapter);
        }
    }
    
    @Override
    public void onDeleteClick(SpecialDay specialDay) {
        // Delete special day
        dbHelper.deleteSpecialDay(specialDay.getId());
        Toast.makeText(this, "Đã xóa ngày đặc biệt", Toast.LENGTH_SHORT).show();
        loadSpecialDays(); // Refresh list
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadSpecialDays(); // Refresh list when returning to activity
    }
}