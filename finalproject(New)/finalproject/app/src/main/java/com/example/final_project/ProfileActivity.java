package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout contactListLayout;
    private SharedPreferences preferences;
    private static final int MAX_CONTACTS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        contactListLayout = findViewById(R.id.contactListLayout);
        preferences = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);

        loadContacts();

        // logout button
        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> showLogoutPopup());
    }

    // ==================== EMERGENCY CONTACTS ====================
    public void addContact(View view) {
        Set<String> contacts = preferences.getStringSet("contacts", new HashSet<>());
        if (contacts.size() >= MAX_CONTACTS) {
            Toast.makeText(this, "You can only add up to 5 contacts.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.contact_list, null);
        EditText nameInput = dialogView.findViewById(R.id.contactNameInput);
        EditText numberInput = dialogView.findViewById(R.id.contactNumberInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Emergency Contact")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString();
                    String number = numberInput.getText().toString();
                    if (!name.isEmpty() && !number.isEmpty()) {
                        addContactToLayout(name, number);
                        saveContact(name, number);
                    } else {
                        Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveContact(String name, String number) {
        Set<String> contacts = preferences.getStringSet("contacts", new HashSet<>());
        contacts = new HashSet<>(contacts);
        contacts.add(name + ":" + number);
        preferences.edit().putStringSet("contacts", contacts).apply();
    }

    private void loadContacts() {
        Set<String> contacts = preferences.getStringSet("contacts", new HashSet<>());
        for (String contact : contacts) {
            String[] parts = contact.split(":");
            if (parts.length == 2) addContactToLayout(parts[0], parts[1]);
        }
    }

    private void addContactToLayout(String name, String number) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(0, 8, 0, 8);
        itemLayout.setWeightSum(10);

        TextView nameText = new TextView(this);
        nameText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 6));
        nameText.setText(name + " - " + number);
        nameText.setTextSize(14);

        Button callButton = new Button(this);
        callButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        callButton.setText("📞");
        callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            startActivity(intent);
        });

        Button deleteButton = new Button(this);
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        deleteButton.setText("🗑️");
        deleteButton.setOnClickListener(v -> confirmDeleteContact(name, number, itemLayout));

        itemLayout.addView(nameText);
        itemLayout.addView(callButton);
        itemLayout.addView(deleteButton);
        contactListLayout.addView(itemLayout);
    }

    private void confirmDeleteContact(String name, String number, LinearLayout itemLayout) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Remove " + name + " from contacts?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    removeContact(name, number);
                    contactListLayout.removeView(itemLayout);
                    Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void removeContact(String name, String number) {
        Set<String> contacts = preferences.getStringSet("contacts", new HashSet<>());
        contacts = new HashSet<>(contacts);
        contacts.remove(name + ":" + number);
        preferences.edit().putStringSet("contacts", contacts).apply();
    }

    public void privacySettings(View view) {
        startActivity(new Intent(this, PrivacyActivity.class));
    }


    // ==================== NAVIGATION ====================
    public void navhome(View view) { startActivity(new Intent(this, MainActivity.class)); }
    public void navdiscover(View view) { startActivity(new Intent(this, DiscoverActivity.class)); }
    public void navcommunity(View view) { startActivity(new Intent(this, Community.class)); }
    public void navjournal(View view) { startActivity(new Intent(this, JournalMainActivity.class)); }
    public void navprofile(View view) { /* Already here */ }
    public void editprofile(View view) { startActivity(new Intent(this, EditProfile.class)); }

    // ==================== LOGOUT ====================
    private void showLogoutPopup() {
        ViewGroup rootView = findViewById(android.R.id.content);
        View blurView = new View(this);
        blurView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            blurView.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(
                    20f, 20f, android.graphics.Shader.TileMode.CLAMP));
            blurView.setBackgroundColor(0x55FFFFFF);
        } else {
            blurView.setBackgroundColor(0xAA000000);
        }
        rootView.addView(blurView);

        View popupView = getLayoutInflater().inflate(R.layout.logout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = popupView.findViewById(R.id.btnNo);
        Button btnYes = popupView.findViewById(R.id.btnYes);

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            rootView.removeView(blurView);
        });

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            rootView.removeView(blurView);
            finish();
        });
    }
}
