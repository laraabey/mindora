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

import com.example.final_project.model.Video;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class AdminAddVideoActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etImageUrl, etYoutubeUrl, etDuration;
    private Spinner spinnerCategory;
    private Button btnSubmit;
    private ImageButton btnBack;
    private FirebaseFirestore db;
    private String selectedCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_video);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        etTitle = findViewById(R.id.etVideoTitle);
        etDescription = findViewById(R.id.etVideoDescription);
        etImageUrl = findViewById(R.id.etVideoImageUrl);
        etYoutubeUrl = findViewById(R.id.etYoutubeUrl);
        etDuration = findViewById(R.id.etVideoDuration);
        spinnerCategory = findViewById(R.id.spinnerVideoCategory);
        btnSubmit = findViewById(R.id.btnSubmitVideo);
        btnBack = findViewById(R.id.btnBackAdmin);

        setupCategorySpinner();

        btnBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> addVideo());
    }

    private void setupCategorySpinner() {
        List<String> categories = Arrays.asList(
                "Select Category",
                "Anxiety",
                "Depression",
                "Stress Management",
                "Sleep",
                "Meditation",
                "Self-Care",
                "Mindfulness",
                "Breathing Exercises"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void addVideo() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String youtubeUrl = etYoutubeUrl.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title required");
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Description required");
            return;
        }

        if (selectedCategory.equals("Select Category")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUrl.isEmpty()) {
            etImageUrl.setError("Image URL required");
            return;
        }

        if (youtubeUrl.isEmpty()) {
            etYoutubeUrl.setError("YouTube URL required");
            return;
        }

        if (duration.isEmpty()) {
            etDuration.setError("Duration required");
            return;
        }

        long timestamp = System.currentTimeMillis();
        Video video = new Video(title, description, selectedCategory, imageUrl,
                youtubeUrl, duration, timestamp);

        db.collection("videos")
                .add(video)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Video added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        etTitle.setText("");
        etDescription.setText("");
        etImageUrl.setText("");
        etYoutubeUrl.setText("");
        etDuration.setText("");
        spinnerCategory.setSelection(0);
    }
}