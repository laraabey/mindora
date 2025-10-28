package com.example.final_project.data;

import java.util.List;

public class Post {
    public String id;
    public String username;
    public String avatarUrl;
    public String text;
    public String mediaUrl;      // image/video (Glide will render a thumbnail)
    public int likes;
    public List<Comment> comments;

    public Post(String id, String username, String avatarUrl, String text, String mediaUrl, int likes, List<Comment> comments) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.text = text;
        this.mediaUrl = mediaUrl;
        this.likes = likes;
        this.comments = comments;
    }
}
