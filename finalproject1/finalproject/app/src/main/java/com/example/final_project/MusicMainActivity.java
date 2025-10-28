package com.example.final_project;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.MusicAdapter;
import com.example.final_project.model.Music;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicMainActivity extends AppCompatActivity {

    private RecyclerView recyclerMusic;
    private MusicAdapter adapter;
    private List<Music> musicList;
    private List<Music> allMusic;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private EditText searchMusic;
    private Spinner spinnerGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity_main);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        ImageView backArrow = findViewById(R.id.backArrow);
        recyclerMusic = findViewById(R.id.recyclerMusic);
        progressBar = findViewById(R.id.progressBar);
        searchMusic = findViewById(R.id.searchMusic);
        spinnerGenre = findViewById(R.id.spinnerGenre);

        backArrow.setOnClickListener(v -> finish());

        // Setup RecyclerView
        musicList = new ArrayList<>();
        allMusic = new ArrayList<>();
        adapter = new MusicAdapter(this, musicList, this::showMusicPlayer);
        recyclerMusic.setLayoutManager(new LinearLayoutManager(this));
        recyclerMusic.setAdapter(adapter);

        // Setup genre spinner
        setupGenreSpinner();

        // Setup search
        setupSearch();

        // Load music
        loadMusic();
    }

    private void setupGenreSpinner() {
        List<String> genres = Arrays.asList(
                "All Genres",
                "Pop",
                "Rock",
                "Hip-Hop",
                "Classical",
                "Jazz",
                "Meditation",
                "Electronic",
                "Instrumental"
        );

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genres
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(spinnerAdapter);

        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGenre = genres.get(position);
                filterByGenre(selectedGenre);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupSearch() {
        searchMusic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMusic(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterByGenre(String genre) {
        String searchText = searchMusic.getText().toString().toLowerCase();
        musicList.clear();

        for (Music music : allMusic) {
            boolean matchesGenre = genre.equals("All Genres") || music.getGenre().equals(genre);
            boolean matchesSearch = searchText.isEmpty() || music.getName().toLowerCase().contains(searchText);

            if (matchesGenre && matchesSearch) {
                musicList.add(music);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterMusic(String searchText) {
        String selectedGenre = spinnerGenre.getSelectedItem().toString();
        musicList.clear();

        for (Music music : allMusic) {
            boolean matchesGenre = selectedGenre.equals("All Genres") || music.getGenre().equals(selectedGenre);
            boolean matchesSearch = searchText.isEmpty() || music.getName().toLowerCase().contains(searchText.toLowerCase());

            if (matchesGenre && matchesSearch) {
                musicList.add(music);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadMusic() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("music")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allMusic.clear();
                    musicList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Music music = document.toObject(Music.class);
                        music.setId(document.getId());
                        allMusic.add(music);
                        musicList.add(music);
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (musicList.isEmpty()) {
                        Toast.makeText(this, "No music available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading music: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showMusicPlayer(Music music) {
        MusicPlayerDialog dialog = new MusicPlayerDialog(this, music);
        dialog.show();
    }
}
