package com.example.final_project.model;

public class Journal {
    private String id;
    private String title;
    private String content;
    private String date;
    private long timestamp;


    public Journal() {
    }

    // constructorfor fields
    public Journal(String title, String content, String date, long timestamp) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.timestamp = timestamp;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}