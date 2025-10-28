package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private EditText mediaNameInput, mediaLinkInput;
    private EditText groundNameInput, groundLinkInput;
    private EditText quoteInput;
    private EditText musicNameInput;
    private EditText audioNameInput;
    private EditText videoNameInput;

    private Button addMediaButton, addGroundButton, addQuoteButton;
    private Button selectMusicImageButton, selectMusicAudioButton, addMusicButton;
    private Button selectAudioFileButton, addAudioButton;
    private Button selectVideoImageButton, selectVideoFileButton, addVideoButton;

    private Uri musicImageUri, musicAudioUri, audioUri, videoThumbnailUri, videoFileUri;

    private FirebaseFirestore db;
    private StorageReference storageRef;

    private final int PICK_IMAGE = 101;
    private final int PICK_AUDIO = 102;
    private final int PICK_VIDEO = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // 🔹 Inputs
        mediaNameInput = findViewById(R.id.mediaNameInput);
        mediaLinkInput = findViewById(R.id.mediaLinkInput);
        groundNameInput = findViewById(R.id.groundNameInput);
        groundLinkInput = findViewById(R.id.groundLinkInput);
        quoteInput = findViewById(R.id.quoteInput);
        musicNameInput = findViewById(R.id.musicNameInput);
        audioNameInput = findViewById(R.id.audioNameInput);
        videoNameInput = findViewById(R.id.videoNameInput);

        // 🔹 Buttons
        addMediaButton = findViewById(R.id.addMediaButton);
        addGroundButton = findViewById(R.id.addGroundButton);
        addQuoteButton = findViewById(R.id.addQuoteButton);

        selectMusicImageButton = findViewById(R.id.selectMusicImageButton);
        selectMusicAudioButton = findViewById(R.id.selectMusicAudioButton);
        addMusicButton = findViewById(R.id.addMusicButton);

        selectAudioFileButton = findViewById(R.id.selectAudioFileButton);
        addAudioButton = findViewById(R.id.addAudioButton);

        selectVideoImageButton = findViewById(R.id.selectVideoImageButton);
        selectVideoFileButton = findViewById(R.id.selectVideoFileButton);
        addVideoButton = findViewById(R.id.addVideoButton);

        // 🔹 Media
        addMediaButton.setOnClickListener(v -> addTopMedia());

        // 🔹 Grounding
        addGroundButton.setOnClickListener(v -> addGroundingExercise());

        // 🔹 Quote
        addQuoteButton.setOnClickListener(v -> addQuote());

        // 🔹 Music pick
        selectMusicImageButton.setOnClickListener(v -> pickFile("image/*", PICK_IMAGE));
        selectMusicAudioButton.setOnClickListener(v -> pickFile("audio/*", PICK_AUDIO));
        addMusicButton.setOnClickListener(v -> uploadMusic());

        // 🔹 Audio pick
        selectAudioFileButton.setOnClickListener(v -> pickFile("audio/*", PICK_AUDIO));
        addAudioButton.setOnClickListener(v -> uploadAudio());

        // 🔹 Video pick
        selectVideoImageButton.setOnClickListener(v -> pickFile("image/*", PICK_IMAGE));
        selectVideoFileButton.setOnClickListener(v -> pickFile("video/*", PICK_VIDEO));
        addVideoButton.setOnClickListener(v -> uploadVideo());
    }

    private void pickFile(String type, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selected = data.getData();

            switch (requestCode) {
                case PICK_IMAGE:
                    if (selectMusicImageButton.isPressed()) musicImageUri = selected;
                    else if (selectVideoImageButton.isPressed()) videoThumbnailUri = selected;
                    break;
                case PICK_AUDIO:
                    if (selectMusicAudioButton.isPressed()) musicAudioUri = selected;
                    else if (selectAudioFileButton.isPressed()) audioUri = selected;
                    break;
                case PICK_VIDEO:
                    videoFileUri = selected;
                    break;
            }
        }
    }

    // 🔹 Media
    private void addTopMedia() {
        String name = mediaNameInput.getText().toString().trim();
        String link = mediaLinkInput.getText().toString().trim();
        if(name.isEmpty() || link.isEmpty()) { toast("Fill all fields"); return; }

        Map<String,Object> data = new HashMap<>();
        data.put("name", name);
        data.put("link", link);

        db.collection("admin_dashboard").document("top_media").collection("items")
                .add(data).addOnSuccessListener(a -> toast("Top Media added"))
                .addOnFailureListener(e -> toast(e.getMessage()));
    }

    // 🔹 Grounding
    private void addGroundingExercise() {
        String name = groundNameInput.getText().toString().trim();
        String link = groundLinkInput.getText().toString().trim();
        if(name.isEmpty() || link.isEmpty()) { toast("Fill all fields"); return; }

        Map<String,Object> data = new HashMap<>();
        data.put("name", name);
        data.put("link", link);

        db.collection("admin_dashboard").document("grounding_exercises").collection("items")
                .add(data).addOnSuccessListener(a -> toast("Exercise added"))
                .addOnFailureListener(e -> toast(e.getMessage()));
    }

    // 🔹 Quote
    private void addQuote() {
        String text = quoteInput.getText().toString().trim();
        if(text.isEmpty()){ toast("Enter quote"); return; }

        Map<String,Object> data = new HashMap<>();
        data.put("text", text);

        db.collection("admin_dashboard").document("quotes").collection("items")
                .add(data).addOnSuccessListener(a -> toast("Quote added"))
                .addOnFailureListener(e -> toast(e.getMessage()));
    }

    // 🔹 Music
    private void uploadMusic() {
        String name = musicNameInput.getText().toString().trim();
        if(name.isEmpty() || musicImageUri == null || musicAudioUri == null){ toast("Fill all fields"); return; }

        StorageReference imgRef = storageRef.child("music/images/" + System.currentTimeMillis());
        imgRef.putFile(musicImageUri).addOnSuccessListener(taskSnapshot ->
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    StorageReference audioRef = storageRef.child("music/audio/" + System.currentTimeMillis());
                    audioRef.putFile(musicAudioUri).addOnSuccessListener(ts ->
                            audioRef.getDownloadUrl().addOnSuccessListener(audioUri -> {
                                Map<String,Object> data = new HashMap<>();
                                data.put("name", name);
                                data.put("image", uri.toString());
                                data.put("audio", audioUri.toString());

                                db.collection("admin_dashboard").document("music").collection("items")
                                        .add(data).addOnSuccessListener(a -> toast("Music uploaded"))
                                        .addOnFailureListener(e -> toast(e.getMessage()));
                            })
                    );
                })
        );
    }

    // 🔹 Audio
    private void uploadAudio() {
        String name = audioNameInput.getText().toString().trim();
        if(name.isEmpty() || audioUri == null){ toast("Fill all fields"); return; }

        StorageReference audioRef = storageRef.child("audio/files/" + System.currentTimeMillis());
        audioRef.putFile(audioUri).addOnSuccessListener(taskSnapshot ->
                audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Map<String,Object> data = new HashMap<>();
                    data.put("name", name);
                    data.put("file", uri.toString());

                    db.collection("admin_dashboard").document("audio").collection("items")
                            .add(data).addOnSuccessListener(a -> toast("Audio uploaded"))
                            .addOnFailureListener(e -> toast(e.getMessage()));
                })
        );
    }

    // 🔹 Video
    private void uploadVideo() {
        String name = videoNameInput.getText().toString().trim();
        if(name.isEmpty() || videoFileUri == null || videoThumbnailUri == null){ toast("Fill all fields"); return; }

        StorageReference thumbRef = storageRef.child("video/thumbnails/" + System.currentTimeMillis());
        thumbRef.putFile(videoThumbnailUri).addOnSuccessListener(taskSnapshot ->
                thumbRef.getDownloadUrl().addOnSuccessListener(thumbUri -> {
                    StorageReference vidRef = storageRef.child("video/files/" + System.currentTimeMillis());
                    vidRef.putFile(videoFileUri).addOnSuccessListener(ts ->
                            vidRef.getDownloadUrl().addOnSuccessListener(vidUri -> {
                                Map<String,Object> data = new HashMap<>();
                                data.put("name", name);
                                data.put("thumbnail", thumbUri.toString());
                                data.put("file", vidUri.toString());

                                db.collection("admin_dashboard").document("video").collection("items")
                                        .add(data).addOnSuccessListener(a -> toast("Video uploaded"))
                                        .addOnFailureListener(e -> toast(e.getMessage()));
                            })
                    );
                })
        );
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
