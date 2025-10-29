package com.example.final_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.adapters.ChatAdapter; // Fixed import
import com.example.final_project.model.ChatMessage;

public class AiChatActivity extends AppCompatActivity {
    private RecyclerView recyclerChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter; // Fixed declaration
    private LocalMentalHealthAI mentalHealthAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_chat);

        ImageView backArrow = findViewById(R.id.backArrow);
        recyclerChat = findViewById(R.id.recyclerChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        mentalHealthAI = new LocalMentalHealthAI();

        chatAdapter = new ChatAdapter(); // Fixed instantiation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setAdapter(chatAdapter);

        addBotMessage("Hello! I'm your mental health support companion. 🌟\n\n" +
                "I'm here to listen and support you. Feel free to share what's on your mind - " +
                "whether you're feeling anxious, stressed, down, or just need someone to talk to.\n\n" +
                "How are you feeling today?");

        backArrow.setOnClickListener(v -> onBackPressed());
        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            return;
        }

        btnSend.setEnabled(false);
        addUserMessage(message);
        etMessage.setText("");
        chatAdapter.showLoading(true);
        scrollToBottom();

        mentalHealthAI.sendMessage(message, new LocalMentalHealthAI.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                chatAdapter.showLoading(false);
                addBotMessage(response);
                btnSend.setEnabled(true);
            }

            @Override
            public void onError(String error) {
                chatAdapter.showLoading(false);
                addBotMessage("I'm having trouble responding. Please try again. 🔄");
                btnSend.setEnabled(true);
                Toast.makeText(AiChatActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true);
        chatAdapter.addMessage(chatMessage);
        scrollToBottom();
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false);
        chatAdapter.addMessage(chatMessage);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }
}