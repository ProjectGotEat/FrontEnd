package com.example.projectgoteat;

public class Report {
    private int participantId;
    private int categoryId;
    private String content;

    public Report(int participantId, int categoryId, String content) {
        this.participantId = participantId;
        this.categoryId = categoryId;
        this.content = content;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
