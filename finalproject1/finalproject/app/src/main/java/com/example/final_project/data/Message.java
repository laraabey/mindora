package com.example.final_project.data;

public class Message {
    public static final int TYPE_IN = 0;
    public static final int TYPE_OUT = 1;

    private final String text;
    private final int type;

    public Message(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() { return text; }
    public int getType() { return type; }
}
