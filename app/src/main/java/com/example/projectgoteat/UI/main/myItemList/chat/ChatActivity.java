package com.example.projectgoteat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
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
    private int participantId;
    private int receiverId;
    private int uid;

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String chatRoomTitle = getIntent().getStringExtra("chatRoomTitle");
            if (chatRoomTitle != null) {
                getSupportActionBar().setTitle(chatRoomTitle);
            } else {
                getSupportActionBar().setTitle("Chat Room");
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        uid = sharedPreferences.getInt("uid", -1);
        if (uid == -1) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        refreshButton = findViewById(R.id.refreshButton);

        messageList = new ArrayList<>();
        participantId = getIntent().getIntExtra("participantId", -1);
        receiverId = getIntent().getIntExtra("receiverId", -1);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMessageCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMessageCheck();
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString();
        if (!messageText.isEmpty()) {
            HashMap<String, Object> message = new HashMap<>();
            message.put("content", messageText);
            message.put("receiver_id", receiverId);

            Log.d(TAG, "Sending message: " + message.toString());

            chatAdapter.sendMessage(message, () -> {
                messageInput.setText("");
                chatAdapter.fetchMessages(() -> recyclerView.scrollToPosition(messageList.size() - 1));
            });
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

    private void startMessageCheck() {
        runnable = new Runnable() {
            @Override
            public void run() {
                chatAdapter.fetchMessages(() -> {
                    // 메시지 확인 후 UI 업데이트 등 추가 작업
                });
                handler.postDelayed(this, 5000); // 5초마다 메시지 확인
            }
        };
        handler.post(runnable);
    }

    private void stopMessageCheck() {
        handler.removeCallbacks(runnable);
    }
}
