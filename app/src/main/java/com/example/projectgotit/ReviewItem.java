package com.example.projectgotit;

public class ReviewItem {
    private String itemName;
    private String createdAt;
    private String rate;
    private String reviewerNickname;
    private String content;

    // Constructor, getters, and setters
    public ReviewItem(String itemName, String createdAt, String rate, String reviewerNickname, String content) {
        this.itemName = itemName;
        this.createdAt = createdAt;
        this.rate = rate;
        this.reviewerNickname = reviewerNickname;
        this.content = content;
    }

    public String getItemName() { return itemName; }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getRate() { return rate; }
    public String getReviewerNickname() { return reviewerNickname; }
    public String getContent() {
        return content;
    }
}
