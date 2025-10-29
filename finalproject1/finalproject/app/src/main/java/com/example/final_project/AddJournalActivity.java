package com.example.final_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.model.Journal;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddJournalActivity extends AppCompatActivity {

    private EditText etTitle, etStory;
    private Button btnSubmit;
    private ImageButton btnBack;
    private FirebaseFirestore db;
    private String journalId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        db = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etStory = findViewById(R.id.etStory);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        journalId = getIntent().getStringExtra("journalId");
        if (journalId != null) {
            String title = getIntent().getStringExtra("title");
            String content = getIntent().getStringExtra("content");
            etTitle.setText(title);
            etStory.setText(content);
            btnSubmit.setText("Update");
        }

        btnBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> saveJournal());
    }

    private void saveJournal() {
        String title = etTitle.getText().toString().trim();
        String content = etStory.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            etStory.setError("Content is required");
            etStory.requestFocus();
            return;
        }

        // get the user ID from login
        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new android.content.Intent(this, LoginActivity.class));
            finish();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String date = dateFormat.format(new Date());
        long timestamp = System.currentTimeMillis();

        // create journal
        Journal journal = new Journal(title, content, date, timestamp);

        if (journalId != null) {
            // Update existing journal
            db.collection("journals")
                    .document(userId)
                    .collection("entries")
                    .document(journalId)
                    .set(journal)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Journal updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // new journal
            db.collection("journals")
                    .document(userId)
                    .collection("entries")
                    .add(journal)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Journal saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}