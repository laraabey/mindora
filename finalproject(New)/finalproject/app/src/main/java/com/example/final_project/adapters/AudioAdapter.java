package com.example.final_project.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.data.AudioItem;
import com.example.final_project.R;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private List<AudioItem> audioList;
    private Context context;
    private MediaPlayer mediaPlayer;

    public AudioAdapter(Context context, List<AudioItem> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioItem item = audioList.get(position);
        holder.txtAudioName.setText(item.getName());
        holder.container.setBackgroundColor(item.getColor());

        holder.itemView.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(context, item.getResId());
            mediaPlayer.start();
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView txtAudioName;
        LinearLayout container;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAudioName = itemView.findViewById(R.id.txtAudioName);
            container = (LinearLayout) itemView;
        }
    }
}
