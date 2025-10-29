package com.example.final_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.model.JournalEntry;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class WriteJournalActivity extends AppCompatActivity {

    private EditText etTitle, etStory;
    private Button btnSubmit;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String journalId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etStory = findViewById(R.id.etStory);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        journalId = getIntent().getStringExtra("journalId");
        if (journalId != null) {
            loadJournalForEdit();
        }

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitJournal());
    }

    private void loadJournalForEdit() {
        db.collection("journalEntries")
                .document(journalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        JournalEntry entry = documentSnapshot.toObject(JournalEntry.class);
                        if (entry != null) {
                            etTitle.setText(entry.getTitle());
                            etStory.setText(entry.getStory());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading journal", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void submitJournal() {
        String title = etTitle.getText().toString().trim();
        String story = etStory.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (story.isEmpty()) {
            etStory.setError("Story is required");
            etStory.requestFocus();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Timestamp timestamp = new Timestamp(new Date());

        JournalEntry entry = new JournalEntry(title, story, timestamp, userId);

        if (journalId != null) {
            db.collection("journalEntries")
                    .document(journalId)
                    .set(entry)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Journal updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating journal: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("journalEntries")
                    .add(entry)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Journal saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error saving journal: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
