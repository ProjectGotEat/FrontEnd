package com.example.projectgoteat;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String title;
    private String meetingTime;
    private String message;
    private int id;
    private int revieweeId;
    private int organizerId;
    private int userId;

    public Item(String title, String meetingTime, String message, int id, int revieweeId, int organizerId, int userId) {
        this.title = title;
        this.meetingTime = meetingTime;
        this.message = message;
        this.id = id;
        this.revieweeId = revieweeId;
        this.organizerId = organizerId;
        this.userId = userId;
    }

    protected Item(Parcel in) {
        title = in.readString();
        meetingTime = in.readString();
        message = in.readString();
        id = in.readInt();
        revieweeId = in.readInt();
        organizerId = in.readInt();
        userId = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public int getRevieweeId() {
        return revieweeId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public int getUserId() {
        return userId;
    }

    public int getParticipantId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(meetingTime);
        dest.writeString(message);
        dest.writeInt(id);
        dest.writeInt(revieweeId);
        dest.writeInt(organizerId);
        dest.writeInt(userId);
    }
}
