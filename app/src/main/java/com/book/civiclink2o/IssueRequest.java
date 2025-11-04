package com.book.civiclink2o;

public class IssueRequest {
    private String category;
    private String description;
    private double latitude;
    private double longitude;
    private int reportedBy;

    public IssueRequest(String category, String description, double latitude, double longitude, int reportedBy) {
        this.category = category;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reportedBy = reportedBy;
    }
}