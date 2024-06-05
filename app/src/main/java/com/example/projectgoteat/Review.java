package com.example.projectgoteat;

public class Review {
    private int boardId;
    private int revieweeId;  // 리뷰를 당하는 사람의 ID
    private int rate;
    private String content;

    public Review(int boardId, int revieweeId, int rating, String content) {
        this.boardId = boardId;
        this.revieweeId = revieweeId;
        this.rate = rating;
        this.content = content;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public int getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(int revieweeId) {
        this.revieweeId = revieweeId;
    }

    public int getRating() {
        return rate;
    }

    public void setRating(int rating) {
        this.rate = rating;
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
                "boardId=" + boardId +
                ", revieweeId=" + revieweeId +
                ", rating=" + rate +
                ", content='" + content + '\'' +
                '}';
    }
}
