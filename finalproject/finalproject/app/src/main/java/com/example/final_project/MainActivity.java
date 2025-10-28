package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Inside JournalMainActivity.java

    // Home tab click
    public void navhome(android.view.View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void aichat(android.view.View view) {
        Intent intent = new Intent(this,AiChatActivity.class);
        startActivity(intent);
    }

    // Discover tab click
    public void navdiscover(android.view.View view) {
        Intent intent = new Intent(this, DiscoverActivity.class);
        startActivity(intent);
    }

    // Community tab click
    public void navcommunity(android.view.View view) {
        Intent intent = new Intent(this, Community.class);
        startActivity(intent);
    }

    // Journal tab click (current activity)
    public void navjournal(android.view.View view) {
        Intent intent = new Intent(this,JournalMainActivity.class);
        startActivity(intent);
    }

    // Profile tab click
    public void navprofile(android.view.View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}