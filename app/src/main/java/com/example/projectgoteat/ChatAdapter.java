package com.example.projectgoteat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectgoteat.network.RetrofitHelper;
import com.example.projectgoteat.network.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final String TAG = "ChatAdapter"; // 로그를 위한 태그
    private List<Message> messageList; // 메시지 리스트
    private RetrofitService retrofitService; // 레트로핏 서비스
    private final int uid = 1; // 임시로 하드코딩된 사용자 ID
    private int participantId; // 참가자 ID
    private int receiverId; // 수신자 ID

    public ChatAdapter(int participantId, int receiverId, List<Message> messageList) {
        this.participantId = participantId; // 참가자 ID 설정
        this.receiverId = receiverId; // 수신자 ID 설정
        this.messageList = messageList; // 메시지 리스트 설정

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance(); // 레트로핏 인스턴스 가져오기
        retrofitService = retrofit.create(RetrofitService.class); // 레트로핏 서비스 생성

        fetchMessages(() -> {}); // 메시지 가져오기
    }

    public void fetchMessages(Runnable onComplete) {
        Log.d(TAG, "Fetching messages for participantId: " + participantId); // 요청 시작 로그 추가
        retrofitService.getMessageDetails(participantId).enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response received: " + response.body().toString()); // 응답 로그
                    messageList.clear(); // 메시지 리스트 초기화

                    Gson gson = new Gson();
                    List<HashMap<String, Object>> messages = (List<HashMap<String, Object>>) response.body().get("messages");
                    if (messages == null) {
                        Log.e(TAG, "No messages found in response"); // 메시지가 없는 경우 로그
                        return;
                    }
                    Type messageType = new TypeToken<List<HashMap<String, Object>>>() {}.getType();
                    List<HashMap<String, Object>> messageObjects = gson.fromJson(gson.toJson(messages), messageType); // JSON을 메시지 객체로 변환

                    for (HashMap<String, Object> messageObject : messageObjects) {
                        // 메시지 객체에서 필요한 정보 추출
                        String profileName = (String) messageObject.get("profile_name");
                        String profileImage = (String) messageObject.get("image");
                        String content = (String) messageObject.get("content");
                        int senderId = ((Double) messageObject.get("sender_id")).intValue();
                        int receiverId = ((Double) messageObject.get("receiver_id")).intValue();

                        Log.d(TAG, "Message received - profileName: " + profileName + ", content: " + content); // 메시지 로그

                        Message message = new Message(profileName, profileImage, content, senderId, receiverId); // 메시지 객체 생성
                        messageList.add(message); // 메시지 리스트에 추가
                    }
                    // UI 업데이트는 메인 스레드에서 실행
                    new Handler(Looper.getMainLooper()).post(() -> {
                        notifyDataSetChanged();
                        onComplete.run(); // 새로고침 완료 후 실행할 작업
                    });
                } else {
                    Log.e(TAG, "Response code: " + response.code()); // 실패 로그
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string()); // 에러 바디 로그
                        } catch (Exception e) {
                            e.printStackTrace(); // 예외 처리
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network error: ", t); // 네트워크 오류 로그
            }
        });
    }

    public void sendMessage(HashMap<String, Object> message, Runnable onSuccess) {
        Log.d(TAG, "Sending message: " + message.toString()); // 메시지 전송 로그
        retrofitService.sendMessage(participantId, message).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Message sent successfully"); // 메시지 전송 성공 로그
                    onSuccess.run(); // 성공 시 콜백 실행
                    fetchMessages(onSuccess); // 메시지 다시 불러오기
                } else {
                    Log.e(TAG, "Message send failed: " + response.code()); // 실패 로그 출력
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string()); // 에러 바디 로그 출력
                        } catch (Exception e) {
                            e.printStackTrace(); // 예외 처리
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Network error: ", t); // 네트워크 오류 로그 출력
            }
        });
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰 홀더 생성
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false); // 보낸 메시지 레이아웃
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false); // 받은 메시지 레이아웃
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        // 뷰 홀더 바인딩
        Message message = messageList.get(position); // 현재 위치의 메시지 가져오기
        if (holder.profileNameTextView != null) {
            holder.profileNameTextView.setText(message.getProfileName()); // 프로필 이름 설정
        }
        holder.messageTextView.setText(message.getContent()); // 메시지 내용을 텍스트뷰에 설정
        if (holder.profileImageView != null) {
            // 프로필 이미지 설정
            Glide.with(holder.profileImageView.getContext())
                    .load(message.getProfileImage())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(holder.profileImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size(); // 메시지 리스트의 크기 반환
    }

    @Override
    public int getItemViewType(int position) {
        // 메시지 발신자와 수신자에 따라 메시지 타입 결정
        if (messageList.get(position).getSenderId() == uid) {
            return 1; // 보낸 메시지 타입
        } else {
            return 0; // 받은 메시지 타입
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView; // 프로필 이미지뷰
        TextView profileNameTextView; // 프로필 이름 텍스트뷰
        TextView messageTextView; // 메시지 텍스트뷰

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView); // 이미지뷰 찾기
            profileNameTextView = itemView.findViewById(R.id.profileNameTextView); // 텍스트뷰 찾기
            messageTextView = itemView.findViewById(R.id.messageTextView); // 텍스트뷰 찾기
        }
    }
}
