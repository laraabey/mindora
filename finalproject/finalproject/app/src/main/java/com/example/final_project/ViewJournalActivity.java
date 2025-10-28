package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewJournalActivity extends AppCompatActivity {

    private TextView tvDate, tvTitle, tvStory;
    private ImageButton btnBack;
    private FloatingActionButton fabEdit;
    private FirebaseFirestore db;
    private String journalId;
    private String title, content, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        db = FirebaseFirestore.getInstance();

        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);
        tvStory = findViewById(R.id.tvStory);
        btnBack = findViewById(R.id.btnBack);
        fabEdit = findViewById(R.id.fabEdit);

        journalId = getIntent().getStringExtra("journalId");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        date = getIntent().getStringExtra("date");

        displayJournal();

        btnBack.setOnClickListener(v -> finish());

        fabEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddJournalActivity.class);
            intent.putExtra("journalId", journalId);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("date", date);
            startActivity(intent);
        });

        //long press for delete
        fabEdit.setOnLongClickListener(v -> {
            showDeleteDialog();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (journalId != null) {
            loadJournalFromFirestore();
        }
    }

    private void displayJournal() {
        tvTitle.setText(title);
        tvStory.setText(content);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                tvDate.setText(outputFormat.format(parsedDate));
            } else {
                tvDate.setText(date);
            }
        } catch (ParseException e) {
            tvDate.setText(date);
        }
    }

    private void loadJournalFromFirestore() {
        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null || journalId == null) return;

        db.collection("journals")
                .document(userId)
                .collection("entries")
                .document(journalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        title = documentSnapshot.getString("title");
                        content = documentSnapshot.getString("content");
                        date = documentSnapshot.getString("date");
                        displayJournal();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading journal", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Journal")
                .setMessage("Are you sure you want to delete this journal entry?")
                .setPositiveButton("Delete", (dialog, which) -> deleteJournal())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteJournal() {
        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null || journalId == null) return;

        db.collection("journals")
                .document(userId)
                .collection("entries")
                .document(journalId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Journal deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting journal", Toast.LENGTH_SHORT).show();
                });
    }
}