package com.example.projectgoteat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
    private int participantId;
    private int receiverId;

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
                getSupportActionBar().setTitle(chatRoomTitle);  // 인텐트로 전달받은 채팅방 이름 설정
            } else {
                getSupportActionBar().setTitle("Chat Room");  // 기본 채팅방 이름 설정
            }
        }

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        participantId = getIntent().getIntExtra("participantId", -1);
        receiverId = getIntent().getIntExtra("receiverId", -1);

        Log.d(TAG, "participantId: " + participantId + ", receiverId: " + receiverId); // 로그 추가

        chatAdapter = new ChatAdapter(participantId, receiverId, messageList);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendButton.setOnClickListener(v -> sendMessage());

        swipeRefreshLayout.setOnRefreshListener(chatAdapter::fetchMessages);
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString();
        if (!messageText.isEmpty()) {
            java.util.HashMap<String, Object> message = new java.util.HashMap<>();
            message.put("content", messageText);
            message.put("receiver_id", receiverId);

            chatAdapter.sendMessage(message, () -> {
                messageInput.setText(""); // Clear the input field
                // 메시지 전송 후 메시지 리스트에 추가하고 화면 업데이트
                Message newMessage = new Message("Me", null, messageText, 1, receiverId);
                messageList.add(newMessage);
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1); // 스크롤을 가장 최근 메시지로 이동
            });
        } else {
            Toast.makeText(this, "메시지를 입력하세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 리소스를 정리합니다.
        recyclerView.setAdapter(null);
        chatAdapter = null;
        messageList.clear();
    }
}
