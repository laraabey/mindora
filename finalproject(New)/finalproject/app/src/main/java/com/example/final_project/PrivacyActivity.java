package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider;

public class PrivacyActivity extends AppCompatActivity {

    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_privacy);

        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        btnBack = findViewById(R.id.btnBack);

        // 🔙 Go back to ProfileActivity when Back button is clicked
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PrivacyActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Save Password
    public void savePasswordClicked(android.view.View view) {
        String current = currentPasswordInput.getText().toString().trim();
        String newPass = newPasswordInput.getText().toString().trim();
        String confirm = confirmPasswordInput.getText().toString().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirm)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getEmail() != null) {
            user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), current))
                    .addOnSuccessListener(aVoid -> {
                        user.updatePassword(newPass)
                                .addOnSuccessListener(aVoid1 ->
                                        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to change password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Open phone's notification settings
    public void openNotificationSettings(android.view.View view) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }

    // Delete Account
    public void deleteAccountClicked(android.view.View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (user.getEmail() != null) {
                        EditText input = new EditText(this);
                        input.setHint("Enter your password");

                        new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("Confirm Password")
                                .setView(input)
                                .setPositiveButton("Confirm", (d, w) -> {
                                    String password = input.getText().toString().trim();
                                    user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), password))
                                            .addOnSuccessListener(aVoid -> {
                                                user.delete()
                                                        .addOnSuccessListener(aVoid2 -> {
                                                            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            startActivity(new Intent(this, RegisterActivity.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e ->
                                                                Toast.makeText(this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show());
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
