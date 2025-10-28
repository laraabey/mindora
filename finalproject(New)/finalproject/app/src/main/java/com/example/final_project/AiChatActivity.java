package com.example.final_project;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AiChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_chat);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());

    }
}




