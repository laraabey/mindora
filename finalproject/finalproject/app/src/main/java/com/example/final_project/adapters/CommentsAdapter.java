package com.example.final_project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.data.Comment;

import java.util.List;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.VH> {
    private final List<Comment> data;

    public CommentsAdapter(List<Comment> data) { this.data = data; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Comment c = data.get(position);
        h.user.setText(c.user);
        h.body.setText(c.body);
        h.badge.setVisibility(c.isTherapist ? View.VISIBLE : View.GONE);
        if (c.isTherapist) {
            h.body.setBackgroundResource(R.color.therapist_comment_bg);
        } else {
            h.body.setBackground(null);
        }
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView user, body, badge;
        VH(@NonNull View v) {
            super(v);
            user = v.findViewById(R.id.tvCommentUser);
            body = v.findViewById(R.id.tvCommentBody);
            badge = v.findViewById(R.id.tvTherapistBadge);
        }
    }
}
