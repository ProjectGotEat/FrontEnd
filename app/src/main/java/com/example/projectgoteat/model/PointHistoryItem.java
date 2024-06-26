package com.example.projectgoteat.model;

public class PointHistoryItem {
    private String changeReason;
    private String changePoint;
    private String createdAt;

    // Constructor, getters, and setters
    public PointHistoryItem(String changeReason, String changePoint, String createdAt) {
        this.changeReason = changeReason;
        this.changePoint = changePoint;
        this.createdAt = createdAt;
    }
    public String getChangeReason() { return changeReason; }

    public String getChangePoint() {
        return changePoint;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
