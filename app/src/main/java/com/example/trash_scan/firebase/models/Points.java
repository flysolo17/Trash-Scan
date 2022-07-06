package com.example.trash_scan.firebase.models;

public class Points {
    public static final String TABLE_NAME = "Points";
    String pointInfo;
    float points;
    Long timestamp;

    public Points() {
    }

    public Points(String pointInfo, float points) {
        this.pointInfo = pointInfo;
        this.points = points;
        this.timestamp = System.currentTimeMillis();
    }

    public String getPointInfo() {
        return pointInfo;
    }

    public void setPointInfo(String pointInfo) {
        this.pointInfo = pointInfo;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
