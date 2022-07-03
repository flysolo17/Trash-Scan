package com.example.trash_scan.firebase.models;

public class Recycables {
    public static final String TABLE_NAME= "Recycables";
    public static final String JUNKSHOP_ID= "junkshopID";
    public static final String RECYCLABLE_NAME= "recycableItemName";
    String recycableID;
    String junkshopID;
    String recycalbleImage;
    String recycableItemName;
    String recycableInformation;
    int recycablePrice;
    public Recycables() {
    }

    public Recycables(String recycableID,String junkshopID, String recycalbleImage, String recycableItemName, String recycableInformation,int recycablePrice) {
        this.recycableID = recycableID;
        this.junkshopID = junkshopID;
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

    public String getJunkshopID() {
        return junkshopID;
    }

    public void setJunkshopID(String junkshopID) {
        this.junkshopID = junkshopID;
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
