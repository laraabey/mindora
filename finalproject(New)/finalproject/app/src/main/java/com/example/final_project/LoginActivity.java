package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etPassword;
    private Button btnLogin, btnGoToRegister;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        etUsername = findViewById(R.id.usernameInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginButton);
        btnGoToRegister = findViewById(R.id.registerButton);

        // Login button
        btnLogin.setOnClickListener(v -> loginUser());

        // Go to register
        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check user in Firestore
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        boolean validUser = false;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String storedPassword = document.getString("password");
                            String fullName = document.getString("name");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                validUser = true;
                                saveUserSession(document.getId(), username);
                                Toast.makeText(LoginActivity.this, "Welcome " + fullName, Toast.LENGTH_SHORT).show();
                                goToMainActivity();
                                break;
                            }
                        }

                        if (!validUser) {
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveUserSession(String userId, String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("username", username);
        editor.apply();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, JournalMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
