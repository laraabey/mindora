package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.final_project.data.Comment;
import com.example.final_project.data.Post;
import com.example.final_project.databinding.CommunityActivityMainBinding;
import com.example.final_project.adapters.CommentsBottomSheet;
import com.example.final_project.adapters.NewPostDialogFragment;
import com.example.final_project.adapters.PostAdapter;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Community extends AppCompatActivity {
    private CommunityActivityMainBinding binding;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CommunityActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.discover);

        binding.recyclerPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(fakeData(), new PostAdapter.PostActionListener() {
            @Override public void onLike(Post p) {
                p.likes++;
                adapter.notifyDataSetChanged();
            }
            @Override public void onComment(Post p) {
                CommentsBottomSheet.show(getSupportFragmentManager(), p);
            }
            @Override public void onShare(Post p) {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_TEXT, p.username + ": " + p.text);
                startActivity(Intent.createChooser(send, "Share post"));
            }
            @Override public void onRepost(Post p) {
                Post repost = new Post(
                        UUID.randomUUID().toString(),
                        "You", "",
                        "Repost from @" + p.username + ":\n" + p.text,
                        p.mediaUrl, 0, new ArrayList<>());
                adapter.addAtTop(repost);
                binding.recyclerPosts.smoothScrollToPosition(0);
                Toast.makeText(Community.this, "Reposted", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerPosts.setAdapter(adapter);

        binding.fabNewPost.setOnClickListener(v ->
                NewPostDialogFragment.show(getSupportFragmentManager(), post -> {
                    adapter.addAtTop(post);
                    binding.recyclerPosts.smoothScrollToPosition(0);
                }));


    }


    // Home tab click
    public void navhome(android.view.View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    // Discover tab click
    public void navdiscover(android.view.View view) {
        Intent intent = new Intent(this, DiscoverActivity.class);
        startActivity(intent);
    }

    // Community tab click
    public void navcommunity(android.view.View view) {
        Intent intent = new Intent(this, Community.class);
        startActivity(intent);
    }

    // Journal tab click (current activity)
    public void navjournal(android.view.View view) {
        Intent intent = new Intent(this,JournalMainActivity.class);
        startActivity(intent);
    }

    // Profile tab click
    public void navprofile(android.view.View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    private List<Post> fakeData() {
        List<Post> list = new ArrayList<>();
        list.add(new Post(
                "1","Shuaib","",
                "Sometimes all you need is a pause. Sharing today’s thought.",
                "", 5,
                Arrays.asList(
                        new Comment("Rtr. Hiruni","Love this!", false),
                        new Comment("Dr. Maya","Thank you for opening up — you’re not alone. 💛", true)
                )
        ));
        list.add(new Post(
                "2","Alex","",
                "Morning walk snaps!",
                "https://picsum.photos/800/400", 12,
                Arrays.asList(new Comment("Therapist John","Great grounding activity — keep at it.", true))
        ));
        return list;
    }
}