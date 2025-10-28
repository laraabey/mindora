package com.example.final_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.CommentsAdapter;
import com.example.final_project.model.Comment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerComments;
    private CommentsAdapter adapter;
    private List<Comment> commentList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private EditText etComment;
    private Button btnPostComment;
    private TextView tvPostContent;
    private ImageView btnBack;

    private String postId;
    private String currentUserId;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        db = FirebaseFirestore.getInstance();

        // Get current user
        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("userId", null);
        currentUsername = sharedPreferences.getString("email", "Anonymous");

        // Get post info
        postId = getIntent().getStringExtra("postId");
        String postContent = getIntent().getStringExtra("postContent");

        // Initialize views
        recyclerComments = findViewById(R.id.recyclerComments);
        progressBar = findViewById(R.id.progressBar);
        etComment = findViewById(R.id.etComment);
        btnPostComment = findViewById(R.id.btnPostComment);
        tvPostContent = findViewById(R.id.tvPostContent);
        btnBack = findViewById(R.id.btnBack);

        tvPostContent.setText(postContent);
        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        commentList = new ArrayList<>();
        adapter = new CommentsAdapter(this, commentList);
        recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerComments.setAdapter(adapter);

        // Load comments
        loadComments();

        // Post comment
        btnPostComment.setOnClickListener(v -> postComment());
    }

    private void loadComments() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error loading comments", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    commentList.clear();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Comment comment = document.toObject(Comment.class);
                            comment.setId(document.getId());
                            commentList.add(comment);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void postComment() {
        String commentText = etComment.getText().toString().trim();

        if (commentText.isEmpty()) {
            etComment.setError("Write a comment");
            return;
        }

        Timestamp timestamp = new Timestamp(new Date());
        Comment comment = new Comment(postId, currentUserId, currentUsername, commentText, timestamp);

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    // Update comment count
                    db.collection("posts")
                            .document(postId)
                            .update("commentCount", commentList.size() + 1);

                    etComment.setText("");
                    Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error posting comment", Toast.LENGTH_SHORT).show();
                });
    }
}