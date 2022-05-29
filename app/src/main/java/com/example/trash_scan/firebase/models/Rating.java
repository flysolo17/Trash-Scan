package com.example.trash_scan.firebase.models;

public class Rating {
    String userID;
    float rating;
    String description;

    public Rating() {
    }

    public Rating(String userID,float rating, String description) {
        this.userID = userID;
        this.rating = rating;
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
