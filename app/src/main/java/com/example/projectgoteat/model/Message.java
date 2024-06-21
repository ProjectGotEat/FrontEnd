package com.example.projectgoteat.model;

import java.sql.Timestamp;

public class Message {
    private String profileName;
    private String profileImage;
    private String content;
    private int senderId;
    private int receiverId;
    private String createdAt;

    public Message(String profileName, String profileImage, String content, int senderId, int receiverId, String createdAt) {
        this.profileName = profileName;
        this.profileImage = profileImage;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getCreatedAt() { return createdAt; }
}
