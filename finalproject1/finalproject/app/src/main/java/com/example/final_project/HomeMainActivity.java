package com.example.final_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.final_project.model.Mood;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class HomeMainActivity extends AppCompatActivity {

    // --- Mood tracking ---
    private FirebaseFirestore db;
    private ImageButton btnHappy, btnCalm, btnSad, btnAngry, btnIrritated, btnAnxious;

    // --- Task management ---
    private LinearLayout tasksContainer;
    private ImageButton btnAddTask;
    private SharedPreferences prefs;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // --- Edge-to-edge ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Initialize ---
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Set Hello message ---
        TextView tvHelloUser = findViewById(R.id.tvHelloUser);
        String name = prefs.getString("name", null);
        String username = prefs.getString("username", null);

        if (name != null && !name.isEmpty()) {
            tvHelloUser.setText("Hello, " + name + " 🌷");
        } else if (username != null && !username.isEmpty()) {
            tvHelloUser.setText("Hello, " + username + " 🌷");
        } else {
            tvHelloUser.setText("Hello, User 🌷");
        }

        // --- Initialize Mood Buttons ---
        initializeMoodButtons();
        loadTodaysMood();

        // --- Initialize Task Section ---
        tasksContainer = findViewById(R.id.tasks_container);
        btnAddTask = findViewById(R.id.btnAddTask);

        checkAndResetTasksIfNewDay();
        loadTasks();

        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    // ---------------- MOOD TRACKING ----------------

    private void initializeMoodButtons() {
        btnHappy = findViewById(R.id.btnHappy);
        btnCalm = findViewById(R.id.btnCalm);
        btnSad = findViewById(R.id.btnSad);
        btnAngry = findViewById(R.id.btnAngry);
        btnIrritated = findViewById(R.id.btnIrritated);
        btnAnxious = findViewById(R.id.btnAnxious);

        btnHappy.setOnClickListener(v -> saveMood("happy"));
        btnCalm.setOnClickListener(v -> saveMood("calm"));
        btnSad.setOnClickListener(v -> saveMood("sad"));
        btnAngry.setOnClickListener(v -> saveMood("angry"));
        btnIrritated.setOnClickListener(v -> saveMood("irritated"));
        btnAnxious.setOnClickListener(v -> saveMood("anxious"));
    }

    private void saveMood(String moodType) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Timestamp timestamp = Timestamp.now();
        Mood mood = new Mood(moodType, timestamp, userId, currentDate);

        db.collection("users")
                .document(userId)
                .collection("moods")
                .document(currentDate)
                .set(mood)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(HomeMainActivity.this, "Mood saved: " + getMoodEmoji(moodType) + " " + capitalize(moodType), Toast.LENGTH_SHORT).show();
                    highlightSelectedMood(moodType);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(HomeMainActivity.this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void loadTodaysMood() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.collection("users")
                .document(userId)
                .collection("moods")
                .document(currentDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Mood mood = documentSnapshot.toObject(Mood.class);
                        if (mood != null) highlightSelectedMood(mood.getMoodType());
                    } else resetMoodButtons();
                });
    }

    private void highlightSelectedMood(String moodType) {
        resetMoodButtons();
        ImageButton selectedButton = null;
        switch (moodType) {
            case "happy": selectedButton = btnHappy; break;
            case "calm": selectedButton = btnCalm; break;
            case "sad": selectedButton = btnSad; break;
            case "angry": selectedButton = btnAngry; break;
            case "irritated": selectedButton = btnIrritated; break;
            case "anxious": selectedButton = btnAnxious; break;
        }
        if (selectedButton != null) {
            selectedButton.setAlpha(1.0f);
            selectedButton.setScaleX(1.2f);
            selectedButton.setScaleY(1.2f);
        }
    }

    private void resetMoodButtons() {
        ImageButton[] buttons = {btnHappy, btnCalm, btnSad, btnAngry, btnIrritated, btnAnxious};
        for (ImageButton b : buttons) {
            b.setAlpha(0.6f);
            b.setScaleX(1.0f);
            b.setScaleY(1.0f);
        }
    }

    private String getMoodEmoji(String moodType) {
        switch (moodType) {
            case "happy": return "😊";
            case "calm": return "😌";
            case "sad": return "😢";
            case "angry": return "😠";
            case "irritated": return "😤";
            case "anxious": return "😰";
            default: return "😊";
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    // ---------------- TASK MANAGEMENT ----------------

    private void checkAndResetTasksIfNewDay() {
        String lastDate = prefs.getString("lastResetDate_" + userId, null);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (lastDate == null || !lastDate.equals(today)) {
            Set<String> savedTasks = prefs.getStringSet("tasks_" + userId, new HashSet<>());
            for (String task : savedTasks) {
                prefs.edit().putBoolean("task_checked_" + userId + "_" + task, false).apply();
            }
            prefs.edit().putString("lastResetDate_" + userId, today).apply();
            Toast.makeText(this, "Tasks reset for today!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTasks() {
        tasksContainer.removeAllViews();
        Set<String> savedTasks = prefs.getStringSet("tasks_" + userId, new HashSet<>());
        for (String task : savedTasks) {
            boolean checked = prefs.getBoolean("task_checked_" + userId + "_" + task, false);
            addTaskToUI(task, checked);
        }
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        final EditText input = new EditText(this);
        input.setHint("Enter your task");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskText = input.getText().toString().trim();
            if (!taskText.isEmpty()) {
                Set<String> tasksSet = prefs.getStringSet("tasks_" + userId, new HashSet<>());
                tasksSet.add(taskText);
                prefs.edit().putStringSet("tasks_" + userId, tasksSet).apply();
                prefs.edit().putBoolean("task_checked_" + userId + "_" + taskText, false).apply();
                addTaskToUI(taskText, false);
            } else {
                Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addTaskToUI(String taskText, boolean checked) {
        LinearLayout taskLayout = new LinearLayout(this);
        taskLayout.setOrientation(LinearLayout.HORIZONTAL);
        taskLayout.setPadding(8, 8, 8, 8);
        taskLayout.setGravity(Gravity.CENTER_VERTICAL);
        taskLayout.setBackgroundResource(android.R.drawable.list_selector_background);

        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(android.R.drawable.ic_delete);
        deleteButton.setBackgroundColor(android.graphics.Color.TRANSPARENT);

        TextView taskTextView = new TextView(this);
        taskTextView.setText(taskText);
        taskTextView.setTextSize(16);
        taskTextView.setTextColor(android.graphics.Color.BLACK);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        taskTextView.setLayoutParams(textParams);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(checked);

        taskLayout.addView(deleteButton);
        taskLayout.addView(taskTextView);
        taskLayout.addView(checkBox);
        tasksContainer.addView(taskLayout);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("task_checked_" + userId + "_" + taskText, isChecked).apply()
        );

        deleteButton.setOnClickListener(v -> {
            Set<String> tasksSet = prefs.getStringSet("tasks_" + userId, new HashSet<>());
            tasksSet.remove(taskText);
            prefs.edit().putStringSet("tasks_" + userId, tasksSet).apply();
            prefs.edit().remove("task_checked_" + userId + "_" + taskText).apply();
            tasksContainer.removeView(taskLayout);
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
        });
    }

    // ---------------- NAVIGATION ----------------

    public void viewMoodBoard(View view) {
        startActivity(new Intent(this, MoodBoardActivity.class));
    }

    public void navhome(View view) {}
    public void aichat(View view) { startActivity(new Intent(this, AiChatActivity.class)); }
    public void navdiscover(View view) { startActivity(new Intent(this, DiscoverActivity.class)); }
    public void navcommunity(View view) { startActivity(new Intent(this, CommunityActivity.class)); }
    public void navjournal(View view) { startActivity(new Intent(this, JournalMainActivity.class)); }
    public void navprofile(View view) { startActivity(new Intent(this, ProfileActivity.class)); }
}
 