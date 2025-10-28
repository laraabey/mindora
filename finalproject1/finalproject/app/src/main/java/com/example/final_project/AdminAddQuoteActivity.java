package com.example.final_project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.model.Quote;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class AdminAddQuoteActivity extends AppCompatActivity {

    private EditText etQuoteText, etAuthor;
    private Spinner spinnerTag;
    private Button btnSubmit;
    private ImageButton btnBack;
    private FirebaseFirestore db;
    private String selectedTag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_quote);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        etQuoteText = findViewById(R.id.etQuoteText);
        etAuthor = findViewById(R.id.etAuthor);
        spinnerTag = findViewById(R.id.spinnerQuoteTag);
        btnSubmit = findViewById(R.id.btnSubmitQuote);
        btnBack = findViewById(R.id.btnBackAdminQuote);

        setupTagSpinner();

        btnBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> addQuote());
    }

    private void setupTagSpinner() {
        List<String> tags = Arrays.asList(
                "Select Tag",
                "Healing",
                "Mindfulness",
                "Self-care",
                "Hope",
                "Rest",
                "Self-love",
                "Motivation",
                "Resilience",
                "Peace",
                "Gratitude"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tags
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTag.setAdapter(adapter);

        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTag = tags.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void addQuote() {
        String text = etQuoteText.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();

        if (text.isEmpty()) {
            etQuoteText.setError("Quote text required");
            etQuoteText.requestFocus();
            return;
        }

        if (author.isEmpty()) {
            etAuthor.setError("Author required");
            etAuthor.requestFocus();
            return;
        }

        if (selectedTag.equals("Select Tag")) {
            Toast.makeText(this, "Please select a tag", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        Quote quote = new Quote(text, author, selectedTag, timestamp);

        db.collection("quotes")
                .add(quote)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Quote added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        etQuoteText.setText("");
        etAuthor.setText("");
        spinnerTag.setSelection(0);
    }
}