package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.VideoAdapter;
import com.example.final_project.model.Video;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoMainActivity extends AppCompatActivity {

    private RecyclerView recyclerVideos;
    private VideoAdapter adapter;
    private List<Video> videoList;
    private List<Video> allVideos;
    private FirebaseFirestore db;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        ImageView backArrow = findViewById(R.id.backArrow);
        recyclerVideos = findViewById(R.id.recyclerVideos);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        backArrow.setOnClickListener(v -> finish());

        // Setup RecyclerView
        videoList = new ArrayList<>();
        allVideos = new ArrayList<>();
        adapter = new VideoAdapter(this, videoList, this::openVideoDetail);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setAdapter(adapter);

        // Setup category spinner
        setupCategorySpinner();

        // Load videos
        loadVideos();
    }

    private void setupCategorySpinner() {
        List<String> categories = Arrays.asList(
                "All Categories",
                "Anxiety",
                "Depression",
                "Stress Management",
                "Sleep",
                "Meditation",
                "Self-Care",
                "Mindfulness",
                "Breathing Exercises"
        );

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);
                filterByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filterByCategory(String category) {
        if (category.equals("All Categories")) {
            videoList.clear();
            videoList.addAll(allVideos);
        } else {
            videoList.clear();
            for (Video video : allVideos) {
                if (video.getCategory().equals(category)) {
                    videoList.add(video);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadVideos() {
        db.collection("videos")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allVideos.clear();
                    videoList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Video video = document.toObject(Video.class);
                        video.setId(document.getId());
                        allVideos.add(video);
                        videoList.add(video);
                    }

                    adapter.notifyDataSetChanged();

                    if (videoList.isEmpty()) {
                        Toast.makeText(this, "No videos available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading videos: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void openVideoDetail(Video video) {
        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtra("videoId", video.getId());
        intent.putExtra("title", video.getTitle());
        intent.putExtra("description", video.getDescription());
        intent.putExtra("category", video.getCategory());
        intent.putExtra("imageUrl", video.getImageUrl());
        intent.putExtra("youtubeUrl", video.getYoutubeUrl());
        intent.putExtra("duration", video.getDuration());
        startActivity(intent);
    }
}