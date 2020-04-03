package com.example.howlstagram_f16.navigation.model;

public class AlarmDTO {
    public String destinationUid = null;
    public String userId = null;
    public String uid = null;
    // 어떤 메시지의 종류인지 알수있는 변수
    // 0 : Like Alarm
    // 1 : Comment Alarm
    // 2 : Follow Alarm
    public int kind = 0;
    public String message = null;
    public long timestamp = 0;

    public AlarmDTO() {

    }

    public void setDestinationUid(String destinationUid) {
        this.destinationUid = destinationUid;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDestinationUid() {
        return destinationUid;
    }

    public String getUserId() {
        return userId;
    }

    public String getUid() {
        return uid;
    }

    public int getKind() {
        return kind;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
