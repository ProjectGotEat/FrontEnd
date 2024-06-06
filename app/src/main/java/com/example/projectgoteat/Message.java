package com.example.projectgoteat;

// Message 클래스: 채팅 메시지를 나타내는 클래스
public class Message {
    private String profileName;  // 프로필 이름
    private String profileImage; // 프로필 이미지
    private String content;  // 메시지 내용
    private int senderId;  // 발신자 ID
    private int receiverId;  // 수신자 ID

    // 생성자: 메시지 내용과 보낸 여부를 초기화
    public Message(String profileName, String profileImage, String content, int senderId, int receiverId) {
        this.profileName = profileName;
        this.profileImage = profileImage;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    // 메시지 내용을 반환하는 메소드
    public String getContent() {
        return content;
    }

    // 발신자 ID를 반환하는 메소드
    public int getSenderId() {
        return senderId;
    }

    // 수신자 ID를 반환하는 메소드
    public int getReceiverId() {
        return receiverId;
    }

    // 프로필 이름을 반환하는 메소드
    public String getProfileName() {
        return profileName;
    }

    // 프로필 이미지를 반환하는 메소드
    public String getProfileImage() {
        return profileImage;
    }
}
