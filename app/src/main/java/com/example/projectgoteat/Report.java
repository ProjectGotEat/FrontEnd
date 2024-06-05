package com.example.projectgoteat;

public class Report {
    private int reporteeId;  // 신고하려는 유저 ID
    private int categoryId;  // 신고 사유 카테고리 ID
    private String content;  // 신고 내용

    public Report(int reporteeId, int categoryId, String content) {
        this.reporteeId = reporteeId;
        this.categoryId = categoryId;
        this.content = content;
    }

    public int getReporteeId() {
        return reporteeId;
    }

    public void setReporteeId(int reporteeId) {
        this.reporteeId = reporteeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reporteeId=" + reporteeId +
                ", categoryId=" + categoryId +
                ", content='" + content + '\'' +
                '}';
    }
}
