package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard); // replace with your layout name

        // Logout button
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears back stack
            startActivity(intent);
            finish(); // closes current activity
        });

    }

    // Button click methods referenced in XML
    public void btnquotes(View view) {
        Intent intent = new Intent(this, AdminAddQuoteActivity.class);
        startActivity(intent);
    }

    public void btnmusic(View view) {
        Intent intent = new Intent(this, AdminAddMusicActivity.class);
        startActivity(intent);
    }

    public void btnaudio(View view) {
        Intent intent = new Intent(this, AdminAddAudioActivity.class);
        startActivity(intent);
    }

    public void btnvideo(View view) {
        Intent intent = new Intent(this, AdminAddVideoActivity.class);
        startActivity(intent);
    }
}
