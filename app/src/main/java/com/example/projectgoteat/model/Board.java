package com.example.projectgoteat.model;

public class Board {
    private String category;
    private String item_name;
    private int headcnt;
    private int remain_headcnt;
    private int total_price;
    private int quantity;
    private String meeting_location;
    private String meeting_time;
    private boolean is_up;
    private boolean is_reusable;
    private String scale;
    private double latitude;
    private double longitude;

    public Board() {
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

    public int getHeadcnt() {
        return headcnt;
    }

    public void setHeadcnt(int headcnt) {
        this.headcnt = headcnt;
    }

    public int getRemain_headcnt() {
        return remain_headcnt;
    }

    public void setRemain_headcnt(int remain_headcnt) {
        this.remain_headcnt = remain_headcnt;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public boolean isIs_up() {
        return is_up;
    }

    public void setIs_up(boolean is_up) {
        this.is_up = is_up;
    }

    public boolean isIs_reusable() {
        return is_reusable;
    }

    public void setIs_reusable(boolean is_reusable) {
        this.is_reusable = is_reusable;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
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
}
