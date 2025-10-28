package com.example.final_project;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoActivity extends AppCompatActivity {

    // Titles, video resources, and colors
    private final String[] names = {
            "Video Sample 1", "Video Sample 2", "Video Sample 3"
    };
    private final int[] resIds = {
            R.raw.video1, R.raw.video2, R.raw.video3
    };
    private final int[] colors = {
            Color.parseColor("#6C63FF"),
            Color.parseColor("#00BFA6"),
            Color.parseColor("#FF6F61")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        RecyclerView rv = findViewById(R.id.recyclerVideos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new VideoAdapter());
    }

    // Inner adapter for RecyclerView
    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VH> {

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_video, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            h.title.setText(names[position]);

            // Rounded background tinted with per-item color
            Drawable bg = getResources().getDrawable(R.drawable.round).mutate();
            bg.setTint(colors[position]);
            h.container.setBackground(bg);

            h.itemView.setOnClickListener(v -> playVideo(resIds[position], names[position]));
        }

        @Override
        public int getItemCount() { return names.length; }

        class VH extends RecyclerView.ViewHolder {
            TextView title;
            LinearLayout container;
            VH(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.txtVideoName);
                container = (LinearLayout) itemView;
            }
        }
    }

    // Show video in a simple dialog player
    private void playVideo(int rawResId, String title) {
        VideoView vv = new VideoView(this);
        vv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        String uri = "android.resource://" + getPackageName() + "/" + rawResId;
        vv.setVideoURI(Uri.parse(uri));

        MediaController controller = new MediaController(this);
        controller.setAnchorView(vv);
        vv.setMediaController(controller);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(vv)
                .setOnDismissListener(d -> vv.stopPlayback())
                .create();

        dialog.show();
        vv.setOnPreparedListener(mp -> vv.start());
    }
}
