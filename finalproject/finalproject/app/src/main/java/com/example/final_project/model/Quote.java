package com.example.final_project.model;

public class Quote {
    private String id;
    private String text;
    private String author;
    private String tag;
    private long timestamp;

    public Quote() {

    }

    public Quote(String text, String author, String tag, long timestamp) {
        this.text = text;
        this.author = author;
        this.tag = tag;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}