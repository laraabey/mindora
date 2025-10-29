package com.example.final_project;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.*;

public class HomeMainActivity extends AppCompatActivity {

    private LinearLayout tasksContainer;
    private ImageButton btnAddTask;
    private SharedPreferences prefs;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // --- Edge-to-edge support ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- SharedPreferences ---
        prefs = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Set Hello TextView ---
        TextView tvHelloUser = findViewById(R.id.tvHelloUser);

        String name = prefs.getString("name", null);
        String username = prefs.getString("username", null);

        if (name != null && !name.isEmpty()) {
            tvHelloUser.setText("Hello, " + name + " 🌷");
        } else if (username != null && !username.isEmpty()) {
            tvHelloUser.setText("Hello, " + username + " 🌷");
        } else {
            tvHelloUser.setText("Hello, User 🌷"); // fallback
        }

        // --- Tasks setup ---
        tasksContainer = findViewById(R.id.tasks_container);
        btnAddTask = findViewById(R.id.btnAddTask);

        checkAndResetTasksIfNewDay();
        loadTasks();

        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
    }


    // --- Daily reset ---
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

    // --- Load tasks from SharedPreferences ---
    private void loadTasks() {
        tasksContainer.removeAllViews();
        Set<String> savedTasks = prefs.getStringSet("tasks_" + userId, new HashSet<>());
        for (String task : savedTasks) {
            boolean checked = prefs.getBoolean("task_checked_" + userId + "_" + task, false);
            addTaskToUI(task, checked);
        }
    }

    // --- Show add task dialog ---
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        final EditText input = new EditText(this);
        input.setHint("Enter your task");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskText = input.getText().toString().trim();
            if (!taskText.isEmpty()) {
                // Save task locally
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

    // --- Add task to UI ---
    private void addTaskToUI(String taskText, boolean checked) {
        LinearLayout taskLayout = new LinearLayout(this);
        taskLayout.setOrientation(LinearLayout.HORIZONTAL);
        taskLayout.setPadding(8, 8, 8, 8);
        taskLayout.setBackgroundResource(android.R.drawable.list_selector_background);
        taskLayout.setGravity(Gravity.CENTER_VERTICAL);

        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(android.R.drawable.ic_delete);
        deleteButton.setBackgroundResource(android.R.color.transparent);

        TextView taskTextView = new TextView(this);
        taskTextView.setText(taskText);
        taskTextView.setTextSize(16);
        taskTextView.setTextColor(getResources().getColor(android.R.color.black));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        taskTextView.setLayoutParams(textParams);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setChecked(checked);

        taskLayout.addView(deleteButton);
        taskLayout.addView(taskTextView);
        taskLayout.addView(checkBox);
        tasksContainer.addView(taskLayout);

        // Checkbox listener
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("task_checked_" + userId + "_" + taskText, isChecked).apply();
        });

        // Delete button listener
        deleteButton.setOnClickListener(v -> {
            Set<String> tasksSet = prefs.getStringSet("tasks_" + userId, new HashSet<>());
            tasksSet.remove(taskText);
            prefs.edit().putStringSet("tasks_" + userId, tasksSet).apply();
            prefs.edit().remove("task_checked_" + userId + "_" + taskText).apply();
            tasksContainer.removeView(taskLayout);
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
        });
    }

    // --- Bottom Navigation Clicks ---
    public void navhome(android.view.View view) {
        startActivity(new Intent(this, HomeMainActivity.class));
    }

    public void aichat(android.view.View view) {
        startActivity(new Intent(this, AiChatActivity.class));
    }

    public void navdiscover(android.view.View view) {
        startActivity(new Intent(this, DiscoverActivity.class));
    }

    public void navcommunity(android.view.View view) {
        startActivity(new Intent(this, CommunityActivity.class));
    }

    public void navjournal(android.view.View view) {
        startActivity(new Intent(this, JournalMainActivity.class));
    }

    public void navprofile(android.view.View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
