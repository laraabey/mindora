package com.example.final_project.data;

public class Journal {
    private String id;
    private String title;
    private String content; // changed from story → content
    private String date;
    private Object timestamp; // Firestore uses Timestamp, not long

    public Journal() {
        // Required empty constructor for Firestore
    }

    public Journal(String id, String title, String content, String date, Object timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Object getTimestamp() { return timestamp; }
    public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
}
