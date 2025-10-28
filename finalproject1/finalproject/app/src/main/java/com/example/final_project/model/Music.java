package com.example.final_project.model;

public class Music {
    private String id;
    private String name;
    private String genre;
    private String duration;
    private String audioUrl;
    private long timestamp;

    public Music() {}

    public Music(String id, String name, String genre, String duration, String audioUrl, long timestamp) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
