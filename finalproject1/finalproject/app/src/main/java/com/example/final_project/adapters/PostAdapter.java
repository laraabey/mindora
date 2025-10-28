package com.example.final_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.example.final_project.model.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private String currentUserId;
    private OnPostActionListener listener;

    public interface OnPostActionListener {
        void onCommentClick(Post post);
        void onShareClick(Post post);
    }

    public PostAdapter(Context context, List<Post> postList, String currentUserId, OnPostActionListener listener) {
        this.context = context;
        this.postList = postList;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.tvUsername.setText(post.getUsername());
        holder.tvContent.setText(post.getContent());

        // Load image if available
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.imgMedia.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.imgMedia);
        } else {
            holder.imgMedia.setVisibility(View.GONE);
        }

        // Comment button
        holder.btnComment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCommentClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvContent;
        ImageView imgAvatar, imgMedia;
        Button btnComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvContent = itemView.findViewById(R.id.tvContent);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgMedia = itemView.findViewById(R.id.imgMedia);
            btnComment = itemView.findViewById(R.id.btnComment);
        }
    }
}