package com.example.projectgoteat;

public class Review {
    private int participantId;  // 참여자 ID
    private int revieweeId;     // 리뷰를 당하는 사람의 ID
    private int rate;           // 평점
    private String content;     // 리뷰 내용

    public Review(int participantId, int revieweeId, int rating, String content) {
        this.participantId = participantId;
        this.revieweeId = revieweeId;
        this.rate = rating;
        this.content = content;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public int getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(int revieweeId) {
        this.revieweeId = revieweeId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Review{" +
                "participantId=" + participantId +
                ", revieweeId=" + revieweeId +
                ", rate=" + rate +
                ", content='" + content + '\'' +
                '}';
    }
}
