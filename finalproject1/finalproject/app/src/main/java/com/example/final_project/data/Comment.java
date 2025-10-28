package com.example.final_project.data;

public class Comment {
    public String user;
    public String body;
    public boolean isTherapist;

    public Comment(String user, String body, boolean isTherapist) {
        this.user = user;
        this.body = body;
        this.isTherapist = isTherapist;
    }
}
