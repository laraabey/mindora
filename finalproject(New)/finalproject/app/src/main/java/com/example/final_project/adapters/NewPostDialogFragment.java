package com.example.final_project.adapters;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.final_project.data.Comment;
import com.example.final_project.data.Post;
import com.example.final_project.databinding.DialogNewPostBinding;

import java.util.Collections;
import java.util.UUID;

public class NewPostDialogFragment extends DialogFragment {

    public interface OnPostCreated { void onCreated(Post post); }
    private static final String TAG = "NewPostDialog";

    public static void show(@NonNull androidx.fragment.app.FragmentManager fm, @NonNull OnPostCreated cb) {
        NewPostDialogFragment frag = new NewPostDialogFragment();
        frag.callback = cb;
        frag.show(fm, TAG);
    }

    private DialogNewPostBinding binding;
    private OnPostCreated callback;
    private Uri selectedMedia;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedMedia = uri;
                    binding.imgPreview.setVisibility(android.view.View.VISIBLE);
                    Glide.with(binding.imgPreview).load(uri).into(binding.imgPreview);
                }
            });

    private final ActivityResultLauncher<String> pickVideo = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedMedia = uri;
                    binding.imgPreview.setVisibility(android.view.View.VISIBLE);
                    Glide.with(binding.imgPreview).load(uri).into(binding.imgPreview); // video thumb
                }
            });

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogNewPostBinding.inflate(LayoutInflater.from(requireContext()));

        AlertDialog.Builder b = new AlertDialog.Builder(requireContext());
        b.setView(binding.getRoot());

        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnAddPhoto.setOnClickListener(v -> pickImage.launch("image/*"));
        binding.btnAddVideo.setOnClickListener(v -> pickVideo.launch("video/*"));
        binding.btnPost.setOnClickListener(v -> {
            String text = binding.etPostText.getText().toString().trim();
            if (text.isEmpty()) { binding.etPostText.setError("Say something…"); return; }
            String mediaUrl = selectedMedia != null ? selectedMedia.toString() : "";
            Post p = new Post(UUID.randomUUID().toString(), "You", "", text, mediaUrl, 0,
                    Collections.singletonList(new Comment("You", "First!", false)));
            if (callback != null) callback.onCreated(p);
            dismiss();
        });

        return b.create();
    }
}