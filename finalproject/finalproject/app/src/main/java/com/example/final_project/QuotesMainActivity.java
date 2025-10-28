package com.example.final_project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.model.Quote;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuotesMainActivity extends AppCompatActivity {

    private TextView tvQuote, tvAuthor;
    private ChipGroup chips;
    private ProgressBar progressBar;
    private View cardQuote, controls, quickActions;

    private FirebaseFirestore db;
    private List<Quote> quotesList;
    private final Random random = new Random();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quotes_activit);

        db = FirebaseFirestore.getInstance();
        quotesList = new ArrayList<>();

        initializeViews();
        setupClickListeners();
        loadQuotes();
    }

    private void initializeViews() {
        tvQuote = findViewById(R.id.tvQuote);
        tvAuthor = findViewById(R.id.tvAuthor);
        chips = findViewById(R.id.chips);
        progressBar = findViewById(R.id.progressBar);
        cardQuote = findViewById(R.id.cardQuote);
        controls = findViewById(R.id.controls);
        quickActions = findViewById(R.id.quickActions);

        if (cardQuote != null) cardQuote.setVisibility(View.GONE);
        if (controls != null) controls.setVisibility(View.GONE);
        if (quickActions != null) quickActions.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        MaterialButton btnPrev = findViewById(R.id.btnPrev);
        MaterialButton btnNext = findViewById(R.id.btnNext);
        MaterialButton btnShuffle = findViewById(R.id.btnShuffle);
        MaterialButton btnShare = findViewById(R.id.btnShare);
        MaterialButton btnCopy = findViewById(R.id.btnCopy);
        ImageView backArrow = findViewById(R.id.backArrow);

        if (btnPrev != null) {
            btnPrev.setOnClickListener(v -> navigatePrevious());
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> navigateNext());
        }

        if (btnShuffle != null) {
            btnShuffle.setOnClickListener(v -> shuffleQuote());
        }

        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> copyQuote());
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> shareQuote());
        }

        if (backArrow != null) {
            backArrow.setOnClickListener(v -> finish());
        }
    }

    private void loadQuotes() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        db.collection("quotes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    quotesList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Quote quote = document.toObject(Quote.class);
                        quote.setId(document.getId());
                        quotesList.add(quote);
                    }

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (quotesList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showContent();
                        // show random quote on first load
                        Collections.shuffle(quotesList);
                        currentIndex = 0;
                        renderQuote();
                    }
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(this, "Error loading quotes: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showContent() {
        if (cardQuote != null) cardQuote.setVisibility(View.VISIBLE);
        if (controls != null) controls.setVisibility(View.VISIBLE);
        if (quickActions != null) quickActions.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        Toast.makeText(this, "No quotes available. Please add quotes from admin panel.",
                Toast.LENGTH_LONG).show();
        if (tvQuote != null) {
            tvQuote.setText("No quotes available");
        }
        if (tvAuthor != null) {
            tvAuthor.setText("");
        }
    }

    private void navigatePrevious() {
        if (!quotesList.isEmpty()) {
            currentIndex = (currentIndex - 1 + quotesList.size()) % quotesList.size();
            renderQuote();
        }
    }

    private void navigateNext() {
        if (!quotesList.isEmpty()) {
            currentIndex = (currentIndex + 1) % quotesList.size();
            renderQuote();
        }
    }

    private void shuffleQuote() {
        if (!quotesList.isEmpty()) {
            currentIndex = random.nextInt(quotesList.size());
            renderQuote();
        }
    }

    private void copyQuote() {
        if (!quotesList.isEmpty()) {
            String content = formatShare(quotesList.get(currentIndex));
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("Quote", content));
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No quote to copy", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQuote() {
        if (!quotesList.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, formatShare(quotesList.get(currentIndex)));
            startActivity(Intent.createChooser(intent, "Share quote via"));
        } else {
            Toast.makeText(this, "No quote to share", Toast.LENGTH_SHORT).show();
        }
    }

    private void renderQuote() {
        if (quotesList.isEmpty() || tvQuote == null || tvAuthor == null) return;

        Quote quote = quotesList.get(currentIndex);

        tvQuote.setText("\"" + quote.getText() + "\"");
        tvAuthor.setText("— " + quote.getAuthor());

        if (chips != null) {
            chips.removeAllViews();
            Chip tagChip = new Chip(this);
            tagChip.setText(quote.getTag());
            tagChip.setCheckable(false);
            tagChip.setClickable(false);
            chips.addView(tagChip);

            tagChip.setAlpha(0f);
            tagChip.animate().alpha(1f).setDuration(300).start();
        }

        tvQuote.setAlpha(0f);
        tvQuote.animate().alpha(1f).setDuration(300).start();

        tvAuthor.setAlpha(0f);
        tvAuthor.animate().alpha(1f).setDuration(300).start();
    }

    private String formatShare(Quote quote) {
        return "\"" + quote.getText() + "\"\n— " + quote.getAuthor();
    }
}