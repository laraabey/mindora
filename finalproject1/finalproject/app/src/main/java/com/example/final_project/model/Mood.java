package com.example.final_project.model;

import com.google.firebase.Timestamp;

public class Mood {
    private String moodType;
    private Timestamp timestamp;
    private String userId;
    private String date;

    public Mood() {

    }

    public Mood(String moodType, Timestamp timestamp, String userId, String date) {
        this.moodType = moodType;
        this.timestamp = timestamp;
        this.userId = userId;
        this.date = date;
    }

    public String getMoodType() { return moodType; }
    public void setMoodType(String moodType) { this.moodType = moodType; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
