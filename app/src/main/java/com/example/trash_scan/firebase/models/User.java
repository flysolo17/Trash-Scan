package com.example.trash_scan.firebase.models;


public class User {
    public static final String TABLE_NAME = "Users";
    public static final String ARG_USER_TYPE = "userType";
    public static final String ARG_FIRST_NAME = "userFirstName";

    public static final String ARG_LAST_NAME = "userLastname";
    public static final String ARG_USER_ADDRESS = "userAddress";
    public static final String ARG_EMAIL = "userEmail";
    public static final String ARG_USER_ID = "userID";
    public static final String USER_PHONE_NUMBER = "userPhoneNumber";
    String userID,userFirstName,userLastName,userAddress,userEmail,userPhoneNumber,userType;

    public User() {
    }

    public User(String userID, String userFirstName, String userLastName, String userAddress, String userEmail, String userPhoneNumber, String userType) {
        this.userID = userID;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userAddress = userAddress;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userType = userType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
