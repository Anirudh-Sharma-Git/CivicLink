package com.book.civiclink2o;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

// THE FIX: This class is now "Parcelable" (shippable between activities)
public class Issue implements Parcelable {

    private int id;
    private String category;
    private String description;
    private String status;
    private int upvotes;
    private String createdAt;

    // THE FIX: Added the missing location fields
    private double latitude;
    private double longitude;

    @SerializedName("reportedByName")
    private String reportedByName;

    // --- Start of Parcelable Implementation ---
    protected Issue(Parcel in) {
        id = in.readInt();
        category = in.readString();
        description = in.readString();
        status = in.readString();
        upvotes = in.readInt();
        createdAt = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        reportedByName = in.readString();
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeInt(upvotes);
        dest.writeString(createdAt);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(reportedByName);
    }
    // --- End of Parcelable Implementation ---


    // --- Getters ---
    public int getId() { return id; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public int getUpvotes() { return upvotes; }
    public String getCreatedAt() { return createdAt; }
    public String getReportedByName() { return reportedByName; }

    // THE FIX: Added the missing getter methods
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
