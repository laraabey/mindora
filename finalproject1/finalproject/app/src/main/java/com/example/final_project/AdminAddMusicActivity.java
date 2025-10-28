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

import com.example.final_project.model.Music;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AdminAddMusicActivity extends AppCompatActivity {

    private EditText etName;
    private TextView tvFileName, tvDuration;
    private Spinner spinnerGenre;
    private Button btnSelectFile, btnSubmit;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private String selectedGenre = "";
    private Uri selectedMusicUri = null;
    private String musicDuration = "";

    private ActivityResultLauncher<Intent> musicPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_music);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize views
        etName = findViewById(R.id.etMusicName);
        tvFileName = findViewById(R.id.tvMusicFileName);
        tvDuration = findViewById(R.id.tvMusicDuration);
        spinnerGenre = findViewById(R.id.spinnerMusicGenre);
        btnSelectFile = findViewById(R.id.btnSelectMusicFile);
        btnSubmit = findViewById(R.id.btnSubmitMusic);
        btnBack = findViewById(R.id.btnBackAdminMusic);
        progressBar = findViewById(R.id.progressBarMusicUpload);

        setupGenreSpinner();
        setupMusicPicker();

        btnBack.setOnClickListener(v -> finish());
        btnSelectFile.setOnClickListener(v -> openMusicPicker());
        btnSubmit.setOnClickListener(v -> uploadMusic());
    }

    private void setupMusicPicker() {
        musicPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedMusicUri = result.getData().getData();
                        if (selectedMusicUri != null) {
                            displayMusicInfo(selectedMusicUri);
                        }
                    }
                }
        );
    }

    private void openMusicPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        musicPickerLauncher.launch(Intent.createChooser(intent, "Select Music File"));
    }

    private void displayMusicInfo(Uri musicUri) {
        try {
            // Get file name
            String fileName = getFileName(musicUri);
            tvFileName.setText(fileName);
            tvFileName.setVisibility(View.VISIBLE);

            // Get music duration
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, musicUri);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();

            if (durationStr != null) {
                long durationMs = Long.parseLong(durationStr);
                musicDuration = formatDuration(durationMs);
                tvDuration.setText("Duration: " + musicDuration);
                tvDuration.setVisibility(View.VISIBLE);
            }

            Toast.makeText(this, "Music file selected", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error reading music file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int index = path.lastIndexOf('/');
            return path.substring(index + 1);
        }
        return "music_file.mp3";
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

    private void setupGenreSpinner() {
        List<String> genres = Arrays.asList(
                "Select Genre",
                "Pop",
                "Rock",
                "Jazz",
                "Classical",
                "Hip-Hop",
                "Electronic",
                "Meditation",
                "Relaxation"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genres
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);

        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = genres.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void uploadMusic() {
        String name = etName.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name required");
            etName.requestFocus();
            return;
        }

        if (selectedGenre.equals("Select Genre")) {
            Toast.makeText(this, "Please select a genre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMusicUri == null) {
            Toast.makeText(this, "Please select a music file", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        btnSelectFile.setEnabled(false);

        String fileName = "music/" + UUID.randomUUID().toString() + ".mp3";
        StorageReference musicRef = storageRef.child(fileName);

        UploadTask uploadTask = musicRef.putFile(selectedMusicUri);

        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressBar.setProgress((int) progress);
        }).addOnSuccessListener(taskSnapshot -> {
            musicRef.getDownloadUrl().addOnSuccessListener(downloadUri ->
                            saveMusicToFirestore(name, downloadUri.toString()))
                    .addOnFailureListener(e -> resetUpload("Error getting download URL: " + e.getMessage()));
        }).addOnFailureListener(e -> resetUpload("Upload failed: " + e.getMessage()));
    }

    private void saveMusicToFirestore(String name, String musicUrl) {
        long timestamp = System.currentTimeMillis();
        Music music = new Music(null, name, selectedGenre, musicDuration, musicUrl, timestamp);

        db.collection("music")
                .add(music)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    btnSelectFile.setEnabled(true);
                    Toast.makeText(this, "Music uploaded successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> resetUpload("Error saving to database: " + e.getMessage()));
    }

    private void resetUpload(String message) {
        progressBar.setVisibility(View.GONE);
        btnSubmit.setEnabled(true);
        btnSelectFile.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearFields() {
        etName.setText("");
        spinnerGenre.setSelection(0);
        tvFileName.setVisibility(View.GONE);
        tvDuration.setVisibility(View.GONE);
        selectedMusicUri = null;
        musicDuration = "";
        progressBar.setProgress(0);
    }
}
