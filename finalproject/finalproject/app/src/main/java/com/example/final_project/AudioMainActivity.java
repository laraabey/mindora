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

import com.example.final_project.adapters.AudioAdapter;
import com.example.final_project.model.Audio;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AudioMainActivity extends AppCompatActivity {

    private RecyclerView recyclerAudios;
    private AudioAdapter adapter;
    private List<Audio> audioList;
    private List<Audio> allAudios;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private EditText searchBar;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_activity_main);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        ImageView backArrow = findViewById(R.id.backArrow);
        recyclerAudios = findViewById(R.id.recyclerAudios);
        progressBar = findViewById(R.id.progressBar);
        searchBar = findViewById(R.id.searchBar);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        backArrow.setOnClickListener(v -> finish());

        // Setup RecyclerView
        audioList = new ArrayList<>();
        allAudios = new ArrayList<>();
        adapter = new AudioAdapter(this, audioList, this::showAudioPlayer);
        recyclerAudios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAudios.setAdapter(adapter);

        // Setup category spinner
        setupCategorySpinner();

        // Setup search
        setupSearch();

        // Load audios
        loadAudios();
    }

    private void setupCategorySpinner() {
        List<String> categories = Arrays.asList(
                "All Categories",
                "Meditation",
                "Sleep",
                "Anxiety Relief",
                "Stress Management",
                "Mindfulness",
                "Breathing Exercises",
                "Relaxation",
                "Focus & Concentration"
        );

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);
                filterByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAudios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterByCategory(String category) {
        String searchText = searchBar.getText().toString().toLowerCase();
        audioList.clear();

        for (Audio audio : allAudios) {
            boolean matchesCategory = category.equals("All Categories") ||
                    audio.getCategory().equals(category);
            boolean matchesSearch = searchText.isEmpty() ||
                    audio.getName().toLowerCase().contains(searchText);

            if (matchesCategory && matchesSearch) {
                audioList.add(audio);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterAudios(String searchText) {
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        audioList.clear();

        for (Audio audio : allAudios) {
            boolean matchesCategory = selectedCategory.equals("All Categories") ||
                    audio.getCategory().equals(selectedCategory);
            boolean matchesSearch = searchText.isEmpty() ||
                    audio.getName().toLowerCase().contains(searchText.toLowerCase());

            if (matchesCategory && matchesSearch) {
                audioList.add(audio);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadAudios() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("audios")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allAudios.clear();
                    audioList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Audio audio = document.toObject(Audio.class);
                        audio.setId(document.getId());
                        allAudios.add(audio);
                        audioList.add(audio);
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (audioList.isEmpty()) {
                        Toast.makeText(this, "No audios available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading audios: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showAudioPlayer(Audio audio) {
        AudioPlayerDialog dialog = new AudioPlayerDialog(this, audio);
        dialog.show();
    }
}