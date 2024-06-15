package com.example.projectgoteat.model;

public class BoardItem {
    private String imageUrl;
    private String title;
    private String eachPrice;
    private String location;
    private String participants;
    private String category;
    private boolean isBookmarked;
    private boolean isFinished;
    private String bid;

    // Constructor, getters, and setters
    public BoardItem(String bid, String imageUrl, String title, String eachPrice, String location, String participants, String category, boolean isBookmarked, boolean isFinished) {
        this.bid = bid;
        this.imageUrl = imageUrl;
        this.title = title;
        this.eachPrice = eachPrice;
        this.location = location;
        this.participants = participants;
        this.category = category;
        this.isBookmarked = isBookmarked;
        this.isFinished = isFinished;
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

    public String getParticipants() {
        return participants;
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
}
