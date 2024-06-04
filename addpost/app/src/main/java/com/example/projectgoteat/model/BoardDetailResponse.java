package com.example.projectgoteat.model;

import android.net.Uri;

public class BoardDetailResponse {
    private Uri item_image1;
    private Uri item_image2;
    private Uri receipt_image;
    private String created_at;
    private Uri user_image;
    private String user_nickname;
    private String user_rank;
    private String category;
    private String item_name;
    private String scale;
    private int each_quantity;
    private int each_price;
    private String meeting_location;
    private String meeting_time;
    private double latitude;
    private double longitude;
    private boolean is_finished;
    private boolean is_reusable;

    // Getters and Setters

    public Uri getItem_image1() {
        return item_image1;
    }

    public void setItem_image1(Uri item_image1) {
        this.item_image1 = item_image1;
    }

    public Uri getItem_image2() {
        return item_image2;
    }

    public void setItem_image2(Uri item_image2) {
        this.item_image2 = item_image2;
    }

    public Uri getReceipt_image() {
        return receipt_image;
    }

    public void setReceipt_image(Uri receipt_image) {
        this.receipt_image = receipt_image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Uri getUser_image() {
        return user_image;
    }

    public void setUser_image(Uri user_image) {
        this.user_image = user_image;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_rank() {
        return user_rank;
    }

    public void setUser_rank(String user_rank) {
        this.user_rank = user_rank;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public int getEach_quantity() {
        return each_quantity;
    }

    public void setEach_quantity(int each_quantity) {
        this.each_quantity = each_quantity;
    }

    public int getEach_price() {
        return each_price;
    }

    public void setEach_price(int each_price) {
        this.each_price = each_price;
    }

    public String getMeeting_location() {
        return meeting_location;
    }

    public void setMeeting_location(String meeting_location) {
        this.meeting_location = meeting_location;
    }

    public String getMeeting_time() {
        return meeting_time;
    }

    public void setMeeting_time(String meeting_time) {
        this.meeting_time = meeting_time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isIs_finished() { // 변경됨
        return is_finished;
    }

    public void setIs_finished(boolean is_finished) { // 변경됨
        this.is_finished = is_finished;
    }

    public boolean isIs_reusable() { // 변경됨
        return is_reusable;
    }

    public void setIs_reusable(boolean is_reusable) { // 변경됨
        this.is_reusable = is_reusable;
    }
}
