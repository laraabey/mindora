package com.example.final_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.model.Mood;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MoodHistoryAdapter extends RecyclerView.Adapter<MoodHistoryAdapter.MoodViewHolder> {

    private List<Mood> moodList;

    public MoodHistoryAdapter(List<Mood> moodList) {
        this.moodList = moodList;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood_history, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        Mood mood = moodList.get(position);
        holder.tvDate.setText(mood.getDate());

        int moodDrawable = 0;
        switch (mood.getMoodType()) {
            case "happy": moodDrawable = R.drawable.happy; break;
            case "calm": moodDrawable = R.drawable.calm; break;
            case "sad": moodDrawable = R.drawable.sad; break;
            case "angry": moodDrawable = R.drawable.angry; break;
            case "irritated": moodDrawable = R.drawable.irritated; break;
            case "anxious": moodDrawable = R.drawable.anxious; break;
        }

        holder.ivMood.setImageResource(moodDrawable);
        holder.tvMoodType.setText(mood.getMoodType().substring(0, 1).toUpperCase() + mood.getMoodType().substring(1));
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvMoodType;
        ImageView ivMood;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvMoodDate);
            tvMoodType = itemView.findViewById(R.id.tvMoodType);
            ivMood = itemView.findViewById(R.id.ivMoodIcon);
        }
    }
}