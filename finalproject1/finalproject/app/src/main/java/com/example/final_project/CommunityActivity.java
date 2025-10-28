package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.PostAdapter;
import com.example.final_project.model.Post;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommunityActivity extends AppCompatActivity implements PostAdapter.OnPostActionListener {

    private RecyclerView recyclerPosts;
    private PostAdapter adapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private String currentUserId;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_activity_main);

        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MindoraPrefs", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("userId", null);
        currentUsername = sharedPreferences.getString("username", "Anonymous");

        if (currentUserId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        recyclerPosts = findViewById(R.id.recyclerPosts);
        FloatingActionButton btnAddPost = findViewById(R.id.btnAddPost);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        postList = new ArrayList<>();
        adapter = new PostAdapter(this, postList, currentUserId, this);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerPosts.setAdapter(adapter);

        // Load posts
        loadPosts();

        // Add post button
        btnAddPost.setOnClickListener(v -> showNewPostBottomSheet());
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error loading posts: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    postList.clear();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Post post = document.toObject(Post.class);
                            post.setId(document.getId());
                            postList.add(post);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (postList.isEmpty()) {
                        Toast.makeText(this, "No posts yet. Be the first to post!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNewPostBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheet_new_post, null);

        EditText etNewPost = bottomSheetView.findViewById(R.id.etNewPost);
        Button btnSubmitPost = bottomSheetView.findViewById(R.id.btnSubmitPost);

        btnSubmitPost.setOnClickListener(v -> {
            String content = etNewPost.getText().toString().trim();
            if (!content.isEmpty()) {
                createPost(content);
                bottomSheetDialog.dismiss();
            } else {
                etNewPost.setError("Please write something");
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void createPost(String content) {
        Timestamp timestamp = new Timestamp(new Date());
        Post post = new Post(currentUserId, currentUsername, content, null, timestamp);

        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error creating post: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onCommentClick(Post post) {
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra("postId", post.getId());
        intent.putExtra("postContent", post.getContent());
        startActivity(intent);
    }

    @Override
    public void onShareClick(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, post.getContent() + "\n- " + post.getUsername());
        startActivity(Intent.createChooser(shareIntent, "Share post via"));
    }

    // Bottom Navigation
    public void navhome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void navdiscover(View view) {
        startActivity(new Intent(this, DiscoverActivity.class));
    }

    public void navcommunity(View view) {
        // Current screen
    }

    public void navjournal(View view) {
        startActivity(new Intent(this, JournalActivity.class));
    }

    public void navprofile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}