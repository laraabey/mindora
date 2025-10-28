package com.example.final_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class AddJournalActivity extends AppCompatActivity {

    private EditText etTitle, etStory;
    private Button btnSubmit;
    private CollectionReference journalCollection;
    private boolean isEdit = false;
    private String journalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        // Get logged in user
        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        // Firestore collection for this user
        journalCollection = FirebaseFirestore.getInstance()
                .collection("journals")
                .document(userId)
                .collection("entries");

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etStory = findViewById(R.id.etStory);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Check if editing existing journal
        if (getIntent().hasExtra("isEdit")) {
            isEdit = getIntent().getBooleanExtra("isEdit", false);
            journalId = getIntent().getStringExtra("journalId");
            String title = getIntent().getStringExtra("title");
            String story = getIntent().getStringExtra("story");

            etTitle.setText(title);
            etStory.setText(story);
            btnSubmit.setText("Update");
        }

        // Button listeners
        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> saveJournal());
    }

    private void saveJournal() {
        String title = etTitle.getText().toString();
        String story = etStory.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (story.isEmpty()) {
            Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestampMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(new Date(timestampMillis));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String date = dateFormat.format(new Date(timestampMillis));

        // Prepare data
        Map<String, Object> journalData = new HashMap<>();
        journalData.put("title", title);
        journalData.put("content", story);
        journalData.put("date", date);
        journalData.put("timestamp", timestamp);

        if (isEdit && journalId != null) {
            // Update existing document
            journalCollection.document(journalId)
                    .set(journalData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Journal updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update journal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Add new document
            journalCollection.add(journalData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Journal saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save journal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
