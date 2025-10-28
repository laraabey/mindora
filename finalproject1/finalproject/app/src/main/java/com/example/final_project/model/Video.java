package com.example.final_project.model;

public class Video {
    private String id;
    private String title;
    private String description;
    private String category;
    private String imageUrl;
    private String youtubeUrl;
    private String duration;
    private long timestamp;

    public Video() {
        // Empty constructor for Firestore
    }

    public Video(String title, String description, String category, String imageUrl,
                 String youtubeUrl, String duration, long timestamp) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.youtubeUrl = youtubeUrl;
        this.duration = duration;
        this.timestamp = timestamp;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}