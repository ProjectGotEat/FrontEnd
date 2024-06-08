package com.example.projectgoteat;
//이벤트나 모임의 정보를 저장
//소분의 상세 내용을 받아옴
//세진
public class Item {
    private String title;
    private String meetingTime;
    private String message;
    private int id;
    private int revieweeId;
    private int organizerId;
    private int userId;

    public Item(String title, String meetingTime, String message, int id, int revieweeId, int organizerId, int userId) {
        this.title = title;
        this.meetingTime = meetingTime;
        this.message = message;
        this.id = id;
        this.revieweeId = revieweeId;
        this.organizerId = organizerId;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public int getRevieweeId() {
        return revieweeId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public int getUserId() {
        return userId;
    }

    public int getParticipantId() {
        return id;
    }
}