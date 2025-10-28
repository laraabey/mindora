package com.example.final_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.model.Audio;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private Context context;
    private List<Audio> audioList;
    private OnAudioClickListener listener;

    public interface OnAudioClickListener {
        void onAudioClick(Audio audio);
    }

    public AudioAdapter(Context context, List<Audio> audioList, OnAudioClickListener listener) {
        this.context = context;
        this.audioList = audioList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        Audio audio = audioList.get(position);

        holder.tvName.setText(audio.getName());
        holder.tvCategory.setText(audio.getCategory());
        holder.tvDuration.setText(audio.getDuration());

        holder.itemView.setOnClickListener(v -> listener.onAudioClick(audio));
    }

    @Override
    public int getItemCount() {
        return audioList != null ? audioList.size() : 0;
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvDuration;
        ImageView ivPlay;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAudioName);
            tvCategory = itemView.findViewById(R.id.tvAudioCategory);
            tvDuration = itemView.findViewById(R.id.tvAudioDuration);
            ivPlay = itemView.findViewById(R.id.ivPlayIcon);
        }
    }
}