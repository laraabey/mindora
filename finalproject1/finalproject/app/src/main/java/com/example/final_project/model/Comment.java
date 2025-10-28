package com.example.final_project.model;

import com.google.firebase.Timestamp;

public class Comment {
    private String id;
    private String postId;
    private String userId;
    private String username;
    private String comment;
    private Timestamp timestamp;

    public Comment() {
        // Empty constructor for Firestore
    }

    public Comment(String postId, String userId, String username, String comment, Timestamp timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}