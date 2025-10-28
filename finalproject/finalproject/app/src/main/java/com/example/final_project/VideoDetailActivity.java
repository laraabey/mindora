package com.example.final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class VideoDetailActivity extends AppCompatActivity {

    private ImageView ivVideoImage, btnBack;
    private TextView tvTitle, tvDescription, tvCategory, tvDuration;
    private Button btnWatchVideo;
    private String youtubeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        // Initialize views
        ivVideoImage = findViewById(R.id.ivVideoDetailImage);
        btnBack = findViewById(R.id.btnBackDetail);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDuration = findViewById(R.id.tvDetailDuration);
        btnWatchVideo = findViewById(R.id.btnWatchVideo);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String category = intent.getStringExtra("category");
        String imageUrl = intent.getStringExtra("imageUrl");
        youtubeUrl = intent.getStringExtra("youtubeUrl");
        String duration = intent.getStringExtra("duration");

        tvTitle.setText(title);
        tvDescription.setText(description);
        tvCategory.setText("Category: " + category);
        tvDuration.setText("Duration: " + duration);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_video)
                .error(R.drawable.placeholder_video)
                .into(ivVideoImage);

        btnBack.setOnClickListener(v -> finish());

        btnWatchVideo.setOnClickListener(v -> openYoutubeVideo());
    }

    private void openYoutubeVideo() {
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            startActivity(intent);
        }
    }
}