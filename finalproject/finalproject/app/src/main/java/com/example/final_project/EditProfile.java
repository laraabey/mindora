package com.example.final_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfile extends AppCompatActivity {

    private EditText editName, editEmail;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Initialize views
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Load saved data
        loadProfileData();

        // Save button click
        btnSave.setOnClickListener(v -> {
            saveProfileData();
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Cancel button click
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveProfileData() {
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();

        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.apply();
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String email = prefs.getString("email", "");

        editName.setText(name);
        editEmail.setText(email);
    }
}
