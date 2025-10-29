package com.example.final_project.model;

import com.google.firebase.Timestamp;

public class JournalEntry {
    private String id;
    private String title;
    private String story;
    private Timestamp date;
    private String userId;

    public JournalEntry() {

    }

    public JournalEntry(String title, String story, Timestamp date, String userId) {
        this.title = title;
        this.story = story;
        this.date = date;
        this.userId = userId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}