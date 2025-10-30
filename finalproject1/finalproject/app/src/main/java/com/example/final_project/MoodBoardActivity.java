package com.example.final_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.MoodHistoryAdapter;
import com.example.final_project.model.Mood;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MoodBoardActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvSelectedDate, tvSelectedMood, tvNoMood;
    private ImageView ivSelectedMood;
    private RecyclerView rvMoodHistory;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String selectedDate;
    private MoodHistoryAdapter moodHistoryAdapter;
    private List<Mood> moodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_board);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);

        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedMood = findViewById(R.id.tvSelectedMood);
        tvNoMood = findViewById(R.id.tvNoMood);
        ivSelectedMood = findViewById(R.id.ivSelectedMood);
        rvMoodHistory = findViewById(R.id.rvMoodHistory);

        moodList = new ArrayList<>();
        moodHistoryAdapter = new MoodHistoryAdapter(moodList);
        rvMoodHistory.setLayoutManager(new LinearLayoutManager(this));
        rvMoodHistory.setAdapter(moodHistoryAdapter);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        tvSelectedDate.setText(selectedDate);

        // load todays mood
        loadMoodForDate(selectedDate);
        loadMoodHistory();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                tvSelectedDate.setText(selectedDate);
                loadMoodForDate(selectedDate);
            }
        });
    }

    private void loadMoodForDate(String date) {
        String userId = sharedPreferences.getString("userId", null);

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("moods")
                .document(date)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Mood mood = documentSnapshot.toObject(Mood.class);
                        if (mood != null) {
                            displayMood(mood.getMoodType());
                        }
                    } else {
                        displayNoMood();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading mood", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadMoodHistory() {
        String userId = sharedPreferences.getString("userId", null);
        if (userId == null || userId.isEmpty()) return;

        db.collection("users")
                .document(userId)
                .collection("moods")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    moodList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Mood mood = document.toObject(Mood.class);
                        moodList.add(mood);
                    }
                    moodHistoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading mood history", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayMood(String moodType) {
        tvNoMood.setVisibility(View.GONE);
        ivSelectedMood.setVisibility(View.VISIBLE);
        tvSelectedMood.setVisibility(View.VISIBLE);

        int moodDrawable = 0;
        String moodText = "";

        switch (moodType) {
            case "happy":
                moodDrawable = R.drawable.happy;
                moodText = "Happy";
                break;
            case "calm":
                moodDrawable = R.drawable.calm;
                moodText = "Calm";
                break;
            case "sad":
                moodDrawable = R.drawable.sad;
                moodText = "Sad";
                break;
            case "angry":
                moodDrawable = R.drawable.angry;
                moodText = "Angry";
                break;
            case "irritated":
                moodDrawable = R.drawable.irritated;
                moodText = "Irritated";
                break;
            case "anxious":
                moodDrawable = R.drawable.anxious;
                moodText = "Anxious";
                break;
        }

        ivSelectedMood.setImageResource(moodDrawable);
        tvSelectedMood.setText("You felt " + moodText);
    }

    private void displayNoMood() {
        tvNoMood.setVisibility(View.VISIBLE);
        ivSelectedMood.setVisibility(View.GONE);
        tvSelectedMood.setVisibility(View.GONE);
    }

    public void goBack(View view) {
        finish();
    }
}