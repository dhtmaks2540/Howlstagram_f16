package com.example.howlstagram_f16.navigation.model;

import java.util.HashMap;
import java.util.Map;

public class ContentDTO {
    String explain = null;
    String imageUrl = null;
    String uid = null;
    String userId = null;
    long timestamp = 0;
    int favoriteCount = 0;
    Map<String, Boolean> favorites = new HashMap<>();

    public String getExplain() {
        return explain;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public Map<String, Boolean> getFavorites() {
        return favorites;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setFavorites(Map<String, Boolean> favorites) {
        this.favorites = favorites;
    }

    class Comment {
        String uid = null;
        String userId = null;
        String comment = null;
        long timestamp = 0;
    }
}
