package com.example.projectgoteat;

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
