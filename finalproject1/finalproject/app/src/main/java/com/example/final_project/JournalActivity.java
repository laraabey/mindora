package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.JournalAdapter;
import com.example.final_project.data.Journal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    private TextView tvMonthYear;
    private GridLayout calendarGrid;
    private ImageButton btnPrevMonth, btnNextMonth;
    private RecyclerView rvRecentJournals;
    private FloatingActionButton fabAdd;

    private FirebaseFirestore db;
    private JournalAdapter adapter;
    private List<Journal> journalList;
    private HashMap<String, Boolean> journalDates;

    private Calendar currentCalendar;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);

        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        tvMonthYear = findViewById(R.id.tvMonthYear);
        calendarGrid = findViewById(R.id.calendarGrid);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        rvRecentJournals = findViewById(R.id.rvRecentJournals);
        fabAdd = findViewById(R.id.fabAdd);

        currentCalendar = Calendar.getInstance();
        journalDates = new HashMap<>();

        journalList = new ArrayList<>();
        adapter = new JournalAdapter(this, journalList);
        rvRecentJournals.setLayoutManager(new LinearLayoutManager(this));
        rvRecentJournals.setAdapter(adapter);

        loadRecentJournals();

        updateCalendar();

        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(JournalActivity.this, AddJournalActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentJournals();
    }

    private void updateCalendar() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(monthFormat.format(currentCalendar.getTime()));

        calendarGrid.removeAllViews();

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            TextView dayView = new TextView(this);
            dayView.setText(day);
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextColor(Color.parseColor("#666666"));
            dayView.setTextSize(12);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(5, 5, 5, 5);
            dayView.setLayoutParams(params);
            calendarGrid.addView(dayView);
        }

        Calendar tempCal = (Calendar) currentCalendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDayOfWeek; i++) {
            View emptyView = new View(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 60;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyView.setLayoutParams(params);
            calendarGrid.addView(emptyView);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar dayCal = (Calendar) currentCalendar.clone();

        for (int day = 1; day <= daysInMonth; day++) {
            dayCal.set(Calendar.DAY_OF_MONTH, day);
            String dateStr = dateFormat.format(dayCal.getTime());
            boolean hasJournal = journalDates.containsKey(dateStr);

            TextView dayView = new TextView(this);
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextColor(Color.parseColor("#000000"));
            dayView.setTextSize(14);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 60;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(5, 5, 5, 5);
            dayView.setLayoutParams(params);

            Calendar today = Calendar.getInstance();
            if (day == today.get(Calendar.DAY_OF_MONTH) &&
                    currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                dayView.setBackgroundResource(R.drawable.calendar_selected_day);
                dayView.setTextColor(Color.WHITE);
            }

            if (hasJournal && !dayView.getText().toString().isEmpty()) {
                String currentText = dayView.getText().toString();
                dayView.setText(currentText + "\n•");
                dayView.setTextSize(12);
            }

            final int selectedDay = day;
            dayView.setOnClickListener(v -> {
                Toast.makeText(this, "Selected: " + selectedDay, Toast.LENGTH_SHORT).show();
            });

            calendarGrid.addView(dayView);
        }
    }

    private void loadRecentJournals() {
        db.collection("journals")
                .document(userId)
                .collection("entries")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    journalList.clear();
                    journalDates.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Journal journal = document.toObject(Journal.class);
                        journal.setId(document.getId());
                        journalList.add(journal);

                        journalDates.put(journal.getDate(), true);
                    }

                    adapter.updateData(journalList);
                    updateCalendar();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading journals: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}