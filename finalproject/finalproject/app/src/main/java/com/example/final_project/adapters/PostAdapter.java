package com.example.final_project.adapters;

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
import com.example.final_project.data.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    public interface PostActionListener {
        void onLike(Post p);
        void onComment(Post p);
        void onShare(Post p);
        void onRepost(Post p);
    }

    private final List<Post> data = new ArrayList<>();
    private final PostActionListener listener;

    public PostAdapter(List<Post> initial, PostActionListener listener) {
        if (initial != null) data.addAll(initial);
        this.listener = listener;
    }

    public void addAtTop(Post p) { data.add(0, p); notifyItemInserted(0); }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Post p = data.get(pos);
        h.tvUsername.setText(p.username);
        h.tvContent.setText(p.text);

        if (p.mediaUrl != null && !p.mediaUrl.isEmpty()) {
            h.imgMedia.setVisibility(View.VISIBLE);
            Glide.with(h.imgMedia.getContext()).load(p.mediaUrl).into(h.imgMedia);
        } else {
            h.imgMedia.setVisibility(View.GONE);
        }

        h.btnLike.setText("❤ " + p.likes);
        h.btnLike.setOnClickListener(v -> listener.onLike(p));
        h.btnComment.setOnClickListener(v -> listener.onComment(p));
        h.btnShare.setOnClickListener(v -> listener.onShare(p));
        h.btnRepost.setOnClickListener(v -> listener.onRepost(p));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgAvatar, imgMedia; TextView tvUsername, tvContent; Button btnLike, btnComment, btnShare, btnRepost;
        VH(@NonNull View v) {
            super(v);
            imgAvatar = v.findViewById(R.id.imgAvatar);
            imgMedia = v.findViewById(R.id.imgMedia);
            tvUsername = v.findViewById(R.id.tvUsername);
            tvContent = v.findViewById(R.id.tvContent);
            btnLike = v.findViewById(R.id.btnLike);
            btnComment = v.findViewById(R.id.btnComment);
            btnShare = v.findViewById(R.id.btnShare);
            btnRepost = v.findViewById(R.id.btnRepost);
        }
    }
}
