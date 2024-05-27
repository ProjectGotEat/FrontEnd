package com.example.addpost;

import com.google.gson.annotations.SerializedName;

public class Board {
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("category")
    private String category;
    @SerializedName("productName")
    private String productName;
    @SerializedName("totalAmount")
    private int totalAmount;
    @SerializedName("numberOfPeople")
    private int numberOfPeople;
    @SerializedName("costPerPerson")
    private int costPerPerson;
    @SerializedName("meetingPlace")
    private String meetingPlace;
    @SerializedName("meetingTime")
    private String meetingTime;
    @SerializedName("isFeatured")
    private boolean isFeatured;
    @SerializedName("isContainer")
    private boolean isContainer;
    @SerializedName("unit")
    private String unit;

    public Board(String imageUrl, String category, String productName, int totalAmount, int numberOfPeople, int costPerPerson, String meetingPlace, String meetingTime, boolean isFeatured, boolean isContainer, String unit) {
        this.imageUrl = imageUrl;
        this.category = category;
        this.productName = productName;
        this.totalAmount = totalAmount;
        this.numberOfPeople = numberOfPeople;
        this.costPerPerson = costPerPerson;
        this.meetingPlace = meetingPlace;
        this.meetingTime = meetingTime;
        this.isFeatured = isFeatured;
        this.isContainer = isContainer;
        this.unit = unit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public int getCostPerPerson() {
        return costPerPerson;
    }

    public void setCostPerPerson(int costPerPerson) {
        this.costPerPerson = costPerPerson;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean container) {
        isContainer = container;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
