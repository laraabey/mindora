package com.example.final_project;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.final_project.model.Audio;

import java.io.IOException;

public class AudioPlayerDialog extends Dialog {

    private Audio audio;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable updateSeekBar;

    private TextView tvAudioName, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private ImageButton btnPlayPause, btnForward, btnBackward, btnClose;
    private boolean isPlaying = false;

    public AudioPlayerDialog(@NonNull Context context, Audio audio) {
        super(context);
        this.audio = audio;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_audio_player);

        // Initialize views
        tvAudioName = findViewById(R.id.tvPlayerAudioName);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.seekBar);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnForward = findViewById(R.id.btnForward);
        btnBackward = findViewById(R.id.btnBackward);
        btnClose = findViewById(R.id.btnClosePlayer);

        // Set audio name
        tvAudioName.setText(audio.getName());
        tvTotalTime.setText(audio.getDuration());

        // Initialize MediaPlayer
        initializeMediaPlayer();

        // Button listeners
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnForward.setOnClickListener(v -> seekForward());
        btnBackward.setOnClickListener(v -> seekBackward());
        btnClose.setOnClickListener(v -> dismiss());

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Setup handler for updating seekbar
        handler = new Handler();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tvCurrentTime.setText(formatTime(currentPosition));
                    handler.postDelayed(this, 100);
                }
            }
        };
    }

    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            mediaPlayer.setDataSource(audio.getAudioUrl());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mediaPlayer.getDuration());
                tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnPlayPause.setImageResource(R.drawable.ic_play);
                seekBar.setProgress(0);
                tvCurrentTime.setText("0:00");
            });

        } catch (IOException e) {
            Toast.makeText(getContext(), "Error loading audio", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play);
                isPlaying = false;
            } else {
                mediaPlayer.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                isPlaying = true;
                handler.post(updateSeekBar);
            }
        }
    }

    private void seekForward() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            int newPosition = Math.min(currentPosition + 10000, duration); // Forward 10 seconds
            mediaPlayer.seekTo(newPosition);
        }
    }

    private void seekBackward() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int newPosition = Math.max(currentPosition - 10000, 0); // Backward 10 seconds
            mediaPlayer.seekTo(newPosition);
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void dismiss() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (handler != null) {
            handler.removeCallbacks(updateSeekBar);
        }
        super.dismiss();
    }
}