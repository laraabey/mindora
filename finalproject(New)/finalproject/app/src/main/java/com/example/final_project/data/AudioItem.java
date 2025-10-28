package com.example.final_project.data;

public class AudioItem {
    private String name;
    private int resId;
    private int color;

    public AudioItem(String name, int resId, int color) {
        this.name = name;
        this.resId = resId;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }

    public int getColor() {
        return color;
    }
}
