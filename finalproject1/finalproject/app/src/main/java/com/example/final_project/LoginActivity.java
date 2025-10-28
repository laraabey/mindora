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
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.usernameInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginButton);
        btnGoToRegister = findViewById(R.id.registerButton);

        btnLogin.setOnClickListener(v -> loginUser());

        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hardcoded admin login
        if (email.equals("admin@gmail.com") && password.equals("admin")) {
            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // stop further execution
        }

        // Normal user login from Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        boolean validUser = false;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String storedPassword = document.getString("password");
                            String username = document.getString("username");
                            String name = document.getString("name");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                validUser = true;
                                saveUserSession(document.getId(), email, username, name);
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                goToDiscoverActivity();
                                break;
                            }
                        }

                        if (!validUser) {
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "User not found with that email", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveUserSession(String userId, String email, String username, String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.putString("username", username != null ? username : email.split("@")[0]); // Use username or fallback to email prefix
        editor.putString("name", name);
        editor.apply();
    }

    private void goToDiscoverActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}