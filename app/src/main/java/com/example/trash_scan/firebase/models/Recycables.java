package com.example.trash_scan.firebase.models;

public class Recycables {
    String recycableID;
    String recycalbleImage;
    String recycableItemName;
    String recycableInformation;
    int recycablePrice;
    public Recycables() {
    }

    public Recycables(String recycableID, String recycalbleImage, String recycableItemName, String recycableInformation,int recycablePrice) {
        this.recycableID = recycableID;
        this.recycalbleImage = recycalbleImage;
        this.recycableItemName = recycableItemName;
        this.recycableInformation = recycableInformation;
        this.recycablePrice = recycablePrice;
    }


    public String getRecycableID() {
        return recycableID;
    }

    public void setRecycableID(String recycableID) {
        this.recycableID = recycableID;
    }

    public String getRecycalbleImage() {
        return recycalbleImage;
    }

    public void setRecycalbleImage(String recycalbleImage) {
        this.recycalbleImage = recycalbleImage;
    }

    public String getRecycableItemName() {
        return recycableItemName;
    }

    public void setRecycableItemName(String recycableItemName) {
        this.recycableItemName = recycableItemName;
    }

    public String getRecycableInformation() {
        return recycableInformation;
    }

    public void setRecycableInformation(String recycableInformation) {
        this.recycableInformation = recycableInformation;
    }

    public int getRecycablePrice() {
        return recycablePrice;
    }

    public void setRecycablePrice(int recycablePrice) {
        this.recycablePrice = recycablePrice;
    }
}
