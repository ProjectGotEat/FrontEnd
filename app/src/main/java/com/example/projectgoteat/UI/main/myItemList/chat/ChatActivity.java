package com.example.projectgoteat.UI.main.myItemList.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projectgoteat.R;
import com.example.projectgoteat.model.Item;
import com.example.projectgoteat.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText messageInput;
    private Button sendButton;
    private Button refreshButton;
    private int uid;
    private int receiverId; // 클래스 필드로 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        refreshButton = findViewById(R.id.refreshButton);

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        uid = sharedPreferences.getInt("uid", -1);

        messageList = new ArrayList<>();

        // 인텐트에서 Item 객체를 받아옴
        Item item = getIntent().getParcelableExtra("item");
        if (item != null) {
            int participantId = item.getParticipantId();
            receiverId = item.getUserId() != uid ? item.getUserId() : item.getOrganizerId(); // receiverId 설정
            String chatRoomTitle = item.getTitle(); // 채팅방 이름 설정

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(chatRoomTitle);
            }

            Log.d(TAG, "Participant ID: " + participantId + ", Receiver ID: " + receiverId + ", UID: " + uid);

            chatAdapter = new ChatAdapter(this, uid, participantId, receiverId, messageList);

            recyclerView.setAdapter(chatAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            sendButton.setOnClickListener(v -> sendMessage());

            chatAdapter.fetchMessages(() -> swipeRefreshLayout.setRefreshing(false));

            swipeRefreshLayout.setOnRefreshListener(() -> chatAdapter.fetchMessages(() -> swipeRefreshLayout.setRefreshing(false)));

            refreshButton.setOnClickListener(v -> {
                swipeRefreshLayout.setRefreshing(true);
                chatAdapter.fetchMessages(() -> swipeRefreshLayout.setRefreshing(false));
            });
        } else {
            Toast.makeText(this, "채팅방 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString();
        if (!messageText.isEmpty()) {
            java.util.HashMap<String, Object> message = new java.util.HashMap<>();
            message.put("content", messageText);
            message.put("receiver_id", receiverId); // receiverId 사용

            chatAdapter.sendMessage(message, () -> {
                messageInput.setText("");
                chatAdapter.fetchMessages(() -> recyclerView.scrollToPosition(messageList.size() - 1));
            });

            messageInput.setText("");
            swipeRefreshLayout.setRefreshing(true);
            chatAdapter.fetchMessages(() -> swipeRefreshLayout.setRefreshing(false));
        } else {
            Toast.makeText(this, "메시지를 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        chatAdapter = null;
        messageList.clear();
    }
}
