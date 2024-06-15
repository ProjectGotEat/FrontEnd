package com.example.projectgoteat;

//리뷰
//세진

public class Review {
    private int revieweeId;     // 리뷰를 당하는 사람의 ID
    private int rate;           // 평점
    private String content;     // 리뷰 내용

    public Review(int revieweeId, int rating, String content) {
        this.revieweeId = revieweeId;
        this.rate = rating;
        this.content = content;
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
                "revieweeId=" + revieweeId +
                ", rate=" + rate +
                ", content='" + content + '\'' +
                '}';
    }
}
