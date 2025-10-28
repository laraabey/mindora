package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.JournalAdapter;
import com.example.final_project.data.Journal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;


public class JournalMainActivity extends AppCompatActivity {
    private TextView tvMonthYear;
    private GridLayout calendarGrid;
    private RecyclerView rvRecentJournals;
    private JournalAdapter adapter;
    private List<Journal> journalList;
    private CollectionReference journalCollection;
    private Calendar currentCalendar;
    private HashMap<String, Boolean> journalDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal);

        // Get logged in user
        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize Firestore collection for user
        journalCollection = FirebaseFirestore.getInstance().collection("journals").document(userId).collection("entries");

        // Initialize views
        tvMonthYear = findViewById(R.id.tvMonthYear);
        calendarGrid = findViewById(R.id.calendarGrid);
        rvRecentJournals = findViewById(R.id.rvRecentJournals);
        ImageButton btnPrevMonth = findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = findViewById(R.id.btnNextMonth);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        currentCalendar = Calendar.getInstance();
        journalDates = new HashMap<>();

        // Setup RecyclerView
        journalList = new ArrayList<>();
        adapter = new JournalAdapter(this, journalList);
        rvRecentJournals.setLayoutManager(new LinearLayoutManager(this));
        rvRecentJournals.setAdapter(adapter);

        // Load data
        loadJournals();

        // Setup calendar
        updateCalendar();

        // Month navigation
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        fabAdd.setOnClickListener(v -> startActivity(new Intent(JournalMainActivity.this, AddJournalActivity.class)));
    }

    private void loadJournals() {
        journalCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) return;

                        journalList.clear();
                        journalDates.clear();

                        for (QueryDocumentSnapshot doc : snapshots) {
                            Journal journal = doc.toObject(Journal.class);
                            journal.setId(doc.getId());
                            journalList.add(journal);

                            // Store date for calendar dots
                            journalDates.put(journal.getDate(), true);
                        }

                        adapter.updateData(journalList);
                        updateCalendar(); // Refresh calendar with dots
                    }
                });
    }

    private void updateCalendar() {
        calendarGrid.removeAllViews();

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(monthYearFormat.format(currentCalendar.getTime()));

        // Day headers
        String[] days = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
        for (String day : days) {
            TextView tv = new TextView(this);
            tv.setText(day);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(12);
            tv.setTextColor(Color.parseColor("#666666"));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);
            calendarGrid.addView(tv);
        }

        // Calendar data
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startDay = firstDayOfWeek == 1 ? 6 : firstDayOfWeek - 2;

        for (int i = 0; i < startDay; i++) calendarGrid.addView(createDayView("", false, false));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar tempCal = (Calendar) currentCalendar.clone();

        for (int day = 1; day <= daysInMonth; day++) {
            tempCal.set(Calendar.DAY_OF_MONTH, day);
            String dateStr = dateFormat.format(tempCal.getTime());
            boolean hasJournal = journalDates.containsKey(dateStr);
            boolean isToday = isSameDay(tempCal, Calendar.getInstance());

            calendarGrid.addView(createDayView(String.valueOf(day), hasJournal, isToday));
        }
    }

    private TextView createDayView(String text, boolean hasDot, boolean isToday) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(0, 15, 0, 15);

        if (isToday) {
            tv.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
            tv.setTypeface(null, Typeface.BOLD);
        }

        if (hasDot && !text.isEmpty()) {
            tv.setText(text + "\n•");
            tv.setTextSize(12);
        }

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        tv.setLayoutParams(params);

        return tv;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJournals();
    }
}
