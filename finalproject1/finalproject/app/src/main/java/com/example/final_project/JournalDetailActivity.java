package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class JournalDetailActivity extends AppCompatActivity {
    private TextView tvDate, tvTitle, tvStory;
    private String journalId, title, content, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        // Get data from intent
        journalId = getIntent().getStringExtra("journalId");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content"); // ✅ changed from "story" to "content"
        date = getIntent().getStringExtra("date");

        // Initialize views
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);
        tvStory = findViewById(R.id.tvStory);
        ImageButton btnBack = findViewById(R.id.btnBack);
        FloatingActionButton fabEdit = findViewById(R.id.fabEdit);

        // Set data
        tvDate.setText(formatDate(date));
        tvTitle.setText(title);
        tvStory.setText(content); // ✅ display content instead of story

        // Button listeners
        btnBack.setOnClickListener(v -> finish());

        fabEdit.setOnClickListener(v -> {
            Intent intent = new Intent(JournalDetailActivity.this, AddJournalActivity.class);
            intent.putExtra("journalId", journalId);
            intent.putExtra("title", title);
            intent.putExtra("content", content); // ✅ send content instead of story
            intent.putExtra("isEdit", true);
            startActivity(intent);
            finish();
        });
    }

    private String formatDate(String date) {
        try {
            String[] parts = date.split("/");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year = Integer.parseInt(parts[2]);

                String[] months = {"January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"};
                String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(year, month, day);

                return days[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1] + ", " +
                        day + " " + months[month] + " " + year;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
