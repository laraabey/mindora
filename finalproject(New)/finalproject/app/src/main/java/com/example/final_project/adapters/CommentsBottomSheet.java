package com.example.final_project.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.final_project.data.Comment;
import com.example.final_project.data.Post;
import com.example.final_project.databinding.BottomSheetCommentsBinding;
import com.example.final_project.adapters.CommentsAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "CommentsBS";

    public static void show(@NonNull androidx.fragment.app.FragmentManager fm, @NonNull Post p) {
        CommentsBottomSheet bs = new CommentsBottomSheet();
        bs.post = p;
        bs.show(fm, TAG);
    }

    private BottomSheetCommentsBinding binding;
    private Post post;
    private CommentsAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCommentsBinding.inflate(inflater, container, false);

        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CommentsAdapter(post.comments);
        binding.recyclerComments.setAdapter(adapter);

        binding.btnSendComment.setOnClickListener(v -> {
            String body = binding.etNewComment.getText().toString().trim();
            if (body.isEmpty()) return;
            Comment c = new Comment("You", body, false);
            if (post.comments == null) post.comments = new ArrayList<>();
            post.comments.add(c);
            adapter.notifyItemInserted(post.comments.size()-1);
            binding.etNewComment.setText("");
        });

        return binding.getRoot();
    }
}