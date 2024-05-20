package com.example.addpost;

public class Post {
    private String imageUrl;
    private String category;
    private String productName;
    private int totalAmount;
    private int numberOfPeople;
    private int costPerPerson;
    private String meetingPlace;
    private String meetingTime;
    private boolean isFeatured;

    private boolean isContainer;

    public Post(String imageUrl, String category, String productName, int totalAmount, int numberOfPeople, int costPerPerson, String meetingPlace, String meetingTime, boolean isFeatured,boolean isContainer) {
        this.imageUrl = imageUrl;
        this.category = category;
        this.productName = productName;
        this.totalAmount = totalAmount;
        this.numberOfPeople = numberOfPeople;
        this.costPerPerson = costPerPerson;
        this.meetingPlace = meetingPlace;
        this.meetingTime = meetingTime;
        this.isFeatured = isFeatured;
        this.isContainer= isContainer;
    }

    // Getter methods
    public String getImageUrl() {
        return imageUrl;
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

    public boolean isContainer() { return isContainer; }
}
