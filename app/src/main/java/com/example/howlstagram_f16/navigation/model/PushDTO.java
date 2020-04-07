package com.example.howlstagram_f16.navigation.model;

public class PushDTO {
    // Push를 받는사람
    String to = null;
    Notification notification = new Notification();

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setNotification(String title, String message) {
        notification.setTitle(title);
        notification.setBody(message);
    }
}

class Notification {
    // Push 메시지
    String body = null;
    // Push 메시지의 제목
    String title = null;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}



