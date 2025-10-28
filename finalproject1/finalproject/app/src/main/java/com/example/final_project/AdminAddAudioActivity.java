package com.example.final_project;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.model.Audio;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AdminAddAudioActivity extends AppCompatActivity {

    private EditText etName;
    private TextView tvFileName, tvDuration;
    private Spinner spinnerCategory;
    private Button btnSelectFile, btnSubmit;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private String selectedCategory = "";
    private Uri selectedAudioUri = null;
    private String audioDuration = "";

    private ActivityResultLauncher<Intent> audioPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_audio);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize views
        etName = findViewById(R.id.etAudioName);
        tvFileName = findViewById(R.id.tvFileName);
        tvDuration = findViewById(R.id.tvDuration);
        spinnerCategory = findViewById(R.id.spinnerAudioCategory);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSubmit = findViewById(R.id.btnSubmitAudio);
        btnBack = findViewById(R.id.btnBackAdminAudio);
        progressBar = findViewById(R.id.progressBarUpload);

        setupCategorySpinner();
        setupAudioPicker();

        btnBack.setOnClickListener(v -> finish());
        btnSelectFile.setOnClickListener(v -> openAudioPicker());
        btnSubmit.setOnClickListener(v -> uploadAudio());
    }

    private void setupAudioPicker() {
        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedAudioUri = result.getData().getData();
                        if (selectedAudioUri != null) {
                            displayAudioInfo(selectedAudioUri);
                        }
                    }
                }
        );
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        audioPickerLauncher.launch(Intent.createChooser(intent, "Select Audio File"));
    }

    private void displayAudioInfo(Uri audioUri) {
        try {
            // Get file name
            String fileName = getFileName(audioUri);
            tvFileName.setText(fileName);
            tvFileName.setVisibility(View.VISIBLE);

            // Get audio duration
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, audioUri);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();

            if (durationStr != null) {
                long durationMs = Long.parseLong(durationStr);
                audioDuration = formatDuration(durationMs);
                tvDuration.setText("Duration: " + audioDuration);
                tvDuration.setVisibility(View.VISIBLE);
            }

            Toast.makeText(this, "Audio file selected", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error reading audio file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int index = path.lastIndexOf('/');
            return path.substring(index + 1);
        }
        return "audio_file.mp3";
    }

    private String formatDuration(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    private void setupCategorySpinner() {
        List<String> categories = Arrays.asList(
                "Select Category",
                "Meditation",
                "Sleep",
                "Anxiety Relief",
                "Stress Management",
                "Mindfulness",
                "Breathing Exercises",
                "Relaxation",
                "Focus & Concentration"
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

    private void uploadAudio() {
        String name = etName.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name required");
            etName.requestFocus();
            return;
        }

        if (selectedCategory.equals("Select Category")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAudioUri == null) {
            Toast.makeText(this, "Please select an audio file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        btnSelectFile.setEnabled(false);

        // Create unique filename
        String fileName = "audios/" + UUID.randomUUID().toString() + ".mp3";
        StorageReference audioRef = storageRef.child(fileName);

        // Upload file
        UploadTask uploadTask = audioRef.putFile(selectedAudioUri);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            // Calculate progress percentage
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressBar.setProgress((int) progress);
        }).addOnSuccessListener(taskSnapshot -> {
            // Get download URL
            audioRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                // Save to Firestore
                saveAudioToFirestore(name, downloadUri.toString());
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                btnSelectFile.setEnabled(true);
                Toast.makeText(this, "Error getting download URL: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
            btnSelectFile.setEnabled(true);
            Toast.makeText(this, "Upload failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void saveAudioToFirestore(String name, String audioUrl) {
        long timestamp = System.currentTimeMillis();
        Audio audio = new Audio(name, selectedCategory, audioDuration, audioUrl, timestamp);

        db.collection("audios")
                .add(audio)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    btnSelectFile.setEnabled(true);
                    Toast.makeText(this, "Audio uploaded successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    btnSelectFile.setEnabled(true);
                    Toast.makeText(this, "Error saving to database: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        etName.setText("");
        spinnerCategory.setSelection(0);
        tvFileName.setVisibility(View.GONE);
        tvDuration.setVisibility(View.GONE);
        selectedAudioUri = null;
        audioDuration = "";
        progressBar.setProgress(0);
    }
}