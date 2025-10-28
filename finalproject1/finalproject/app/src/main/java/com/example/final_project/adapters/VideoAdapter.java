package com.example.final_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.example.final_project.model.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<Video> videoList;
    private OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    public VideoAdapter(Context context, List<Video> videoList, OnVideoClickListener listener) {
        this.context = context;
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);

        holder.tvTitle.setText(video.getTitle());
        holder.tvCategory.setText(video.getCategory());

        // Short description (max 100 chars)
        String shortDesc = video.getDescription();
        if (shortDesc.length() > 100) {
            shortDesc = shortDesc.substring(0, 100) + "...";
        }
        holder.tvDescription.setText(shortDesc);

        // Load image with Glide
        Glide.with(context)
                .load(video.getImageUrl())
                .placeholder(R.drawable.placeholder_video)
                .error(R.drawable.placeholder_video)
                .into(holder.ivThumbnail);

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videoList != null ? videoList.size() : 0;
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvDescription, tvCategory;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivVideoThumbnail);
            tvTitle = itemView.findViewById(R.id.tvVideoTitle);
            tvDescription = itemView.findViewById(R.id.tvVideoDescription);
            tvCategory = itemView.findViewById(R.id.tvVideoCategory);
        }
    }
}