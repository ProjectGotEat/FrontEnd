package com.example.projectgoteat.model;

public class BoardItem {
    private String imageUrl;
    private String title;
    private String eachPrice;
    private String location;
    private String participantsStatus;
    private String category;
    private boolean isBookmarked;
    private boolean isFinished;
    private String bid;
    private String headcnt;
    private String participantsHeadcnt;

    // Constructor, getters, and setters
    public BoardItem(String bid, String imageUrl, String title, String eachPrice, String location, String participants, String category, boolean isBookmarked, boolean isFinished, String headcnt, String participantsHeadcnt) {
        this.bid = bid;
        this.imageUrl = imageUrl;
        this.title = title;
        this.eachPrice = eachPrice;
        this.location = location;
        this.participantsStatus = participants;
        this.category = category;
        this.isBookmarked = isBookmarked;
        this.isFinished = isFinished;
        this.headcnt = headcnt;
        this.participantsHeadcnt = participantsHeadcnt;
    }
    public String getImageUrl() { return imageUrl; }

    public String getTitle() {
        return title;
    }

    public String getEachPrice() {
        return eachPrice;
    }

    public String getLocation() {
        return location;
    }

    public String getParticipantsStatus() {
        return participantsStatus;
    }

    public String getCategory() {
        return category;
    }
    public String getBid() { return bid; }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public String getHeadcnt() { return headcnt; }

    public void setHeadcnt(String headcnt) { this.headcnt = headcnt; }

    public String getParticipantsHeadcnt() { return participantsHeadcnt; }

    public void setParticipantsHeadcnt(String participantsHeadcnt) { this.participantsHeadcnt = participantsHeadcnt; }
}
