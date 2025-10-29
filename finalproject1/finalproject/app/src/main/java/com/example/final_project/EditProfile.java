package com.example.final_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText editName, editUsername, editEmail;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;
    private String userDocId; // Firestore document ID
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        editName = findViewById(R.id.editName);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);

        // Get stored data
        userDocId = sharedPreferences.getString("userId", ""); // Firestore doc ID saved at login
        String name = sharedPreferences.getString("name", "");
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");

        editName.setText(name);
        editUsername.setText(username);
        editEmail.setText(email);

        btnSave.setOnClickListener(v -> updateProfile());
        btnCancel.setOnClickListener(v -> finish());
    }


    private void updateProfile() {
        String newName = editName.getText().toString().trim();
        String newUsername = editUsername.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();

        if (newName.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDocId.isEmpty()) {
            Toast.makeText(this, "User ID missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("users").document(userDocId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("username", newUsername);
        updates.put("email", newEmail);

        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", newName);
                    editor.putString("username", newUsername);
                    editor.putString("email", newEmail);
                    editor.apply();

                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

}
