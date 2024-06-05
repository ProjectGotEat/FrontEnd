package com.example.projectgoteat;

// ChatMessage 클래스: 채팅 메시지를 나타내는 클래스
public class ChatMessage {
    private String text; // 메시지 내용
    private boolean isSent; // 메시지가 보낸 것인지 여부

    // 생성자: 메시지 내용과 보낸 여부를 초기화
    public ChatMessage(String text, boolean isSent) {
        this.text = text;
        this.isSent = isSent;
    }

    // 메시지 내용을 반환하는 메서드
    public String getText() {
        return text;
    }

    // 메시지가 보낸 것인지 여부를 반환하는 메서드
    public boolean isSent() {
        return isSent;
    }
}