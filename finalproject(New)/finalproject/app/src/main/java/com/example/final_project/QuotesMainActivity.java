package com.example.final_project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Random;

public class QuotesMainActivity extends AppCompatActivity {

    private TextView tvQuote, tvAuthor;
    private ChipGroup chips;
    private final Random random = new Random();

    // Quote structure
    private static class Q {
        final String text, author, tag;
        Q(String t, String a, String g) { text = t; author = a; tag = g; }
    }

    // Mental-health quotes
    private final Q[] quotes = new Q[]{
            new Q("It’s okay not to be okay. Healing isn’t linear—take it one gentle step at a time.",
                    "Unknown", "Healing"),
            new Q("You don’t have to control your thoughts. You just have to stop letting them control you.",
                    "Dan Millman", "Mindfulness"),
            new Q("Self-care is how you take your power back.",
                    "Lalah Delia", "Self-care"),
            new Q("Happiness can be found even in the darkest of times, if one only remembers to turn on the light.",
                    "Albus Dumbledore", "Hope"),
            new Q("Rest is not idleness, and to lie sometimes on the grass under trees is by no means a waste of time.",
                    "John Lubbock", "Rest"),
            new Q("Talk to yourself like someone you love.",
                    "Brené Brown", "Self-love")
    };

    private int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quotes_activit);

        tvQuote = findViewById(R.id.tvQuote);
        tvAuthor = findViewById(R.id.tvAuthor);
        chips = findViewById(R.id.chips);

        MaterialButton btnPrev = findViewById(R.id.btnPrev);
        MaterialButton btnNext = findViewById(R.id.btnNext);
        MaterialButton btnShuffle = findViewById(R.id.btnShuffle);
        MaterialButton btnShare = findViewById(R.id.btnShare);
        MaterialButton btnCopy = findViewById(R.id.btnCopy);

        render();

        btnPrev.setOnClickListener(v -> { idx = (idx - 1 + quotes.length) % quotes.length; render(); });
        btnNext.setOnClickListener(v -> { idx = (idx + 1) % quotes.length; render(); });
        btnShuffle.setOnClickListener(v -> { idx = random.nextInt(quotes.length); render(); });

        btnCopy.setOnClickListener(v -> {
            String content = formatShare(quotes[idx]);
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("Quote", content));
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        btnShare.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, formatShare(quotes[idx]));
            startActivity(Intent.createChooser(i, "Share quote via"));
        });

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());
    }

    private void render() {
        Q q = quotes[idx];
        tvQuote.setText("“" + q.text + "”");
        tvAuthor.setText("— " + q.author);

        // Update first chip with the quote tag (if ChipGroup exists)
        if (chips != null) {
            Chip tagChip;
            if (chips.getChildCount() == 0) {
                tagChip = new Chip(this);
                tagChip.setCheckable(false);
                chips.addView(tagChip);
            } else {
                tagChip = (Chip) chips.getChildAt(0);
            }
            tagChip.setText(q.tag);
        }

        // subtle fade
        tvQuote.setAlpha(0f);
        tvQuote.animate().alpha(1f).setDuration(220).start();
        tvAuthor.setAlpha(0f);
        tvAuthor.animate().alpha(1f).setDuration(220).start();
        if (chips != null && chips.getChildCount() > 0) {
            chips.getChildAt(0).setAlpha(0f);
            chips.getChildAt(0).animate().alpha(1f).setDuration(220).start();
        }
    }

    private String formatShare(Q q) {
        return "“" + q.text + "”\n— " + q.author;
    }
}
