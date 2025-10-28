package com.example.final_project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.ChatAdapter;
import com.example.final_project.data.Message;

public class ChatActivity extends AppCompatActivity {

    private ChatAdapter adapter;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_chat);

        RecyclerView rv = findViewById(R.id.recyclerChat);
        et = findViewById(R.id.etMessage);
        ImageButton send = findViewById(R.id.btnSend);

        adapter = new ChatAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Initial bot greeting
        adapter.submit(new Message("Hi! I’m Mindora. Ask me anything 😊", Message.TYPE_IN));

        send.setOnClickListener(v -> sendMessage());
        et.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String text = et.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        adapter.submit(new Message(text, Message.TYPE_OUT));
        scrollToBottom();

        et.setText("");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String reply = "You said: \"" + text + "\"";
            adapter.submit(new Message(reply, Message.TYPE_IN));
            scrollToBottom();
        }, 600);
    }

    private void scrollToBottom() {
        RecyclerView rv = findViewById(R.id.recyclerChat);
        rv.scrollToPosition(adapter.getItemCount() - 1);
    }
}
