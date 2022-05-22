package com.example.trash_scan.violation;

public class ViolationsAct {
    String title;
    String body;

    public ViolationsAct() {
    }

    public ViolationsAct(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
