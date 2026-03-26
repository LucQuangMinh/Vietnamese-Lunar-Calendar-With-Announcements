package com.example.lunarcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {
    
    private RecyclerView recyclerViewEvents;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseHelper dbHelper;
    private EventManager eventManager;
    
    private EditText etSearch;
    private Button btnSearch, btnAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Initialize database and event manager
        dbHelper = new DatabaseHelper(this);
        eventManager = new EventManager(this);

        // Initialize views
        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        // Setup RecyclerView
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, eventManager);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvents.setAdapter(eventAdapter);

        // Load all events
        loadEvents();

        // Set up click listeners
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventListActivity.this, AddEventActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to activity
        loadEvents();
    }

    private void loadEvents() {
        eventList.clear();
        eventList.addAll(eventManager.getAllEvents());
        eventAdapter.notifyDataSetChanged();
    }

    private void performSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.isEmpty()) {
            loadEvents();
            return;
        }

        // Simple search implementation - search in event names
        List<Event> searchResults = new ArrayList<>();
        for (Event event : eventManager.getAllEvents()) {
            if (event.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(event);
            }
        }

        eventList.clear();
        eventList.addAll(searchResults);
        eventAdapter.notifyDataSetChanged();
    }
}