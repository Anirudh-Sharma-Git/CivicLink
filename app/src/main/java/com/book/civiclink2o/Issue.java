package com.book.civiclink2o;

import com.google.gson.annotations.SerializedName;

// This is the "data package" for an issue we RECEIVE from the server.
public class Issue {

    private int id;
    private String category;
    private String description;
    private String status;
    private int upvotes;
    private String createdAt;

    // This annotation tells our networking library (Gson) that when it sees
    // a field named "reportedByName" in the server's response, it should put
    // that value into our "reportedByName" variable here.
    @SerializedName("reportedByName")
    private String reportedByName;

    // Getters - These are methods that allow other parts of our app to safely read the data
    public int getId() { return id; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public int getUpvotes() { return upvotes; }
    public String getCreatedAt() { return createdAt; }
    public String getReportedByName() { return reportedByName; }
}