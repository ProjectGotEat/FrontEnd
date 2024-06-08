package com.example.projectgoteat.model;

public class RequestData {
    private int organizer_id;

    public RequestData(int organizer_id) {
        this.organizer_id = organizer_id;
    }

    public int getOrganizerId() {
        return organizer_id;
    }

    public void setOrganizerId(int organizer_id) {
        this.organizer_id = organizer_id;
    }
}
