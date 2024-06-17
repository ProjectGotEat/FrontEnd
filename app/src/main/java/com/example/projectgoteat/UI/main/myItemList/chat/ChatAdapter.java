package com.example.projectgoteat.UI.main.myItemList.chat;

import android.content.Context;
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
import com.example.projectgoteat.R;
import com.example.projectgoteat.model.Message;
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

    private static final String TAG = "ChatAdapter";
    private List<Message> messageList;
    private RetrofitService retrofitService;
    private int uid;
    private int participantId;
    private int receiverId;
    private Context context; // 추가된 부분

    public ChatAdapter(Context context, int uid, int participantId, int receiverId, List<Message> messageList) {
        this.uid = uid;
        this.participantId = participantId;
        this.receiverId = receiverId;
        this.messageList = messageList;

        Retrofit retrofit = RetrofitHelper.getRetrofitInstance(context);
        retrofitService = retrofit.create(RetrofitService.class);

        fetchMessages(() -> {});
    }


    public void fetchMessages(Runnable onComplete) {
        Log.d(TAG, "Fetching messages for participantId: " + participantId);
        retrofitService.getMessageDetails(participantId).enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response received: " + response.body().toString());
                    messageList.clear();

                    Gson gson = new Gson();
                    List<HashMap<String, Object>> messages = (List<HashMap<String, Object>>) response.body().get("messages");
                    if (messages == null) {
                        Log.e(TAG, "No messages found in response");
                        return;
                    }
                    Type messageType = new TypeToken<List<HashMap<String, Object>>>() {}.getType();
                    List<HashMap<String, Object>> messageObjects = gson.fromJson(gson.toJson(messages), messageType);

                    for (HashMap<String, Object> messageObject : messageObjects) {
                        String profileName = (String) messageObject.get("profile_name");
                        String profileImage = (String) messageObject.get("image");
                        String content = (String) messageObject.get("content");
                        int senderId = ((Double) messageObject.get("sender_id")).intValue();
                        int receiverId = ((Double) messageObject.get("receiver_id")).intValue();

                        Log.d(TAG, "Message received - profileName: " + profileName + ", content: " + content);

                        Message message = new Message(profileName, profileImage, content, senderId, receiverId);
                        messageList.add(message);
                    }

                    new Handler(Looper.getMainLooper()).post(() -> {
                        notifyDataSetChanged();
                        onComplete.run();
                    });
                } else {
                    Log.e(TAG, "Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(TAG, "Network error: ", t);
            }
        });
    }

    public void sendMessage(HashMap<String, Object> message, Runnable onSuccess) {
        Log.d(TAG, "Sending message: " + message.toString());
        retrofitService.sendMessage(participantId, message).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Message sent successfully");
                    onSuccess.run();
                    fetchMessages(onSuccess);
                } else {
                    Log.e(TAG, "Message send failed: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Network error: ", t);
            }
        });
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder.profileNameTextView != null) {
            holder.profileNameTextView.setText(message.getProfileName());
        }
        holder.messageTextView.setText(message.getContent());
        if (holder.profileImageView != null) {
            Glide.with(holder.profileImageView.getContext())
                    .load(message.getProfileImage())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(holder.profileImageView);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId() == uid) {
            return 1;
        } else {
            return 0;
        }
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView profileNameTextView;
        TextView messageTextView;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            profileNameTextView = itemView.findViewById(R.id.profileNameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
