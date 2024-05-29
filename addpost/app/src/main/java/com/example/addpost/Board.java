package com.example.addpost;

import android.net.Uri;

public class Board {
    private Uri item_image1;
    private Uri receipt_image;
    private String category_id;
    private String item_name;
    private int headcnt;
    private int remain_headcnt;
    private int total_price;
    private String meeting_location;
    private String meeting_time;
    private boolean is_up;
    private boolean is_reusable;
    private String scale;
    private double latitude;
    private double longitude;

    // Constructor
    public Board(Uri item_image1, Uri receipt_image, String category_id, String item_name, int headcnt, int remain_headcnt, int total_price, String meeting_location, String meeting_time, boolean is_up, boolean is_reusable, String scale, double latitude, double longitude) {
        this.item_image1 = item_image1;
        this.receipt_image = receipt_image;
        this.category_id = category_id;
        this.item_name = item_name;
        this.headcnt = headcnt;
        this.remain_headcnt = remain_headcnt;
        this.total_price = total_price;
        this.meeting_location = meeting_location;
        this.meeting_time = meeting_time;
        this.is_up = is_up;
        this.is_reusable = is_reusable;
        this.scale = scale;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter methods
    public Uri getItem_image1() {
        return item_image1;
    }

    public Uri getReceipt_image() {
        return receipt_image;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public int getHeadcnt() {
        return headcnt;
    }

    public int getRemain_headcnt() {
        return remain_headcnt;
    }

    public int getTotal_price() {
        return total_price;
    }

    public String getMeeting_location() {
        return meeting_location;
    }

    public String getMeeting_time() {
        return meeting_time;
    }

    public boolean isIs_up() {
        return is_up;
    }

    public boolean isIs_reusable() {
        return is_reusable;
    }

    public String getScale() {
        return scale;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
