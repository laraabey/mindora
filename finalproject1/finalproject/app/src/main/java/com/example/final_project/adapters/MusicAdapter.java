package com.example.final_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.model.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private Context context;
    private List<Music> musicList;
    private OnMusicClickListener listener;

    public interface OnMusicClickListener {
        void onMusicClick(Music music);
    }

    public MusicAdapter(Context context, List<Music> musicList, OnMusicClickListener listener) {
        this.context = context;
        this.musicList = musicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.tvName.setText(music.getName());
        holder.tvCategory.setText(music.getGenre());
        holder.tvDuration.setText(music.getDuration());

        holder.itemView.setOnClickListener(v -> listener.onMusicClick(music));
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvDuration;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAudioName);
            tvCategory = itemView.findViewById(R.id.tvAudioCategory);
            tvDuration = itemView.findViewById(R.id.tvAudioDuration);
        }
    }
}
