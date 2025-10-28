package com.example.final_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.AudioAdapter;
import com.example.final_project.data.AudioItem;

import java.util.ArrayList;
import java.util.List;

public class AudioMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_activity_main);

        RecyclerView recycler = findViewById(R.id.recyclerAudios);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<AudioItem> audioList = new ArrayList<>();
        audioList.add(new AudioItem("Audio Sample 1", R.raw.sample1, Color.parseColor("#6C63FF")));
        audioList.add(new AudioItem("Audio Sample 2", R.raw.sample2, Color.parseColor("#00BFA6")));
        audioList.add(new AudioItem("Audio Sample 3", R.raw.sample3, Color.parseColor("#FF6F61")));
        audioList.add(new AudioItem("Audio Sample 4", R.raw.sample4, Color.parseColor("#FFD54F")));

        AudioAdapter adapter = new AudioAdapter(this, audioList);
        recycler.setAdapter(adapter);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());
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

    // Community tab click
    /*public void navcommunity(android.view.View view) {
        Intent intent = new Intent(this, CommunityActivity.class);
        startActivity(intent);
    }*/

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
