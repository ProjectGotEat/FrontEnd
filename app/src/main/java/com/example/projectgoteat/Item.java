package com.example.projectgoteat;

public class Item {
    private String title;
    private String meetingTime;
    private String message;
    private int participantId;
    private int userId;
    private int organizerId;

    public Item(String title, String meetingTime, String message, int participantId, int userId, int organizerId) {
        this.title = title;
        this.meetingTime = meetingTime;
        this.message = message;
        this.participantId = participantId;
        this.userId = userId;
        this.organizerId = organizerId;
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

    public int getParticipantId() {
        return participantId;
    }

    public int getUserId() {
        return userId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public int getRevieweeId(int uid) {
        return uid == organizerId ? userId : organizerId;
    }
}
