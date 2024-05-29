package com.example.addpost;

import android.net.Uri;

public class Board {
    private Uri imageUri;
    private String category;
    private String productName;
    private int totalAmount;
    private int numberOfPeople;
    private int costPerPerson;
    private String meetingPlace;
    private String meetingTime;
    private boolean isFeatured;
    private boolean isContainer;
    private String unit;

    // 생성자
    public Board(Uri imageUri, String category, String productName, int totalAmount, int numberOfPeople, int costPerPerson, String meetingPlace, String meetingTime, boolean isFeatured, boolean isContainer, String unit) {
        this.imageUri = imageUri;
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

    // getter 메서드
    public Uri getImageUri() {
        return imageUri;
    }

    public String getCategory() {
        return category;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public int getCostPerPerson() {
        return costPerPerson;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public String getUnit() {
        return unit;
    }
}
