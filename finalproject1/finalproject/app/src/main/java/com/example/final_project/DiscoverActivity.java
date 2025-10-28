package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class DiscoverActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover);
    }


    // Home tab click
    public void navhome(android.view.View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    // Discover tab click
    public void navdiscover(android.view.View view) {
        Intent intent = new Intent(this, DiscoverActivity.class);
        startActivity(intent);
    }

    public void btnaudio(android.view.View view) {
        Intent intent = new Intent(this, AudioMainActivity.class);
        startActivity(intent);
    }

    public void btnvideo(android.view.View view) {
        Intent intent = new Intent(this, VideoMainActivity.class);
        startActivity(intent);
    }

    public void btnquotes(android.view.View view) {
        Intent intent = new Intent(this, QuotesMainActivity.class);
        startActivity(intent);
    }

    public void btnmusic(android.view.View view) {
        Intent intent = new Intent(this, MusicMainActivity.class);
        startActivity(intent);
    }

    // Community tab click
    public void navcommunity(android.view.View view) {
        Intent intent = new Intent(this, CommunityActivity.class);
        startActivity(intent);
    }

    // Journal tab click (current activity)
    public void navjournal(android.view.View view) {
        Intent intent = new Intent(this,JournalMainActivity.class);
        startActivity(intent);
    }

    // Profile tab click
    public void navprofile(android.view.View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}
