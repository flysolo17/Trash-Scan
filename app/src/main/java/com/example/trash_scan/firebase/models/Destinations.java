package com.example.trash_scan.firebase.models;

import java.util.List;

public class Destinations {
    String destinationID;
    String collectorsName;
    List<String> listAddresses;
    String nowCollecting;
    long timestamp;

    public Destinations() {

    }

    public Destinations(String destinationID, String collectorsName, List<String> listAddresses, String nowCollecting, long timestamp) {
        this.destinationID = destinationID;
        this.collectorsName = collectorsName;
        this.listAddresses = listAddresses;
        this.nowCollecting = nowCollecting;
        this.timestamp = timestamp;
    }

    public String getDestinationID() {
        return destinationID;
    }

    public void setDestinationID(String destinationID) {
        this.destinationID = destinationID;
    }

    public String getCollectorsName() {
        return collectorsName;
    }

    public void setCollectorsName(String collectorsName) {
        this.collectorsName = collectorsName;
    }

    public List<String> getListAddresses() {
        return listAddresses;
    }

    public void setListAddresses(List<String> listAddresses) {
        this.listAddresses = listAddresses;
    }

    public String getNowCollecting() {
        return nowCollecting;
    }

    public void setNowCollecting(String nowCollecting) {
        this.nowCollecting = nowCollecting;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}