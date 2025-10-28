package com.example.final_project.model;

public class Audio {
    private String id;
    private String name;
    private String category;
    private String duration;
    private String audioUrl;
    private long timestamp;

    public Audio() {

    }

    public Audio(String name, String category, String duration, String audioUrl, long timestamp) {
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}