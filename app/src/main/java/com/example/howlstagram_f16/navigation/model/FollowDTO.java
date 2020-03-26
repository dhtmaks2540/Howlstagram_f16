package com.example.howlstagram_f16.navigation.model;

import java.util.HashMap;
import java.util.Map;

public class FollowDTO {
    int followerCount = 0;
    Map<String, Boolean> followers = new HashMap<>();

    int followingCount = 0;
    Map<String, Boolean> followings = new HashMap<>();

    public int getFollowerCount() {
        return followerCount;
    }

    public Map<String, Boolean> getFollowers() {
        return followers;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public Map<String, Boolean> getFollowings() {
        return followings;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = followers;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public void setFollowings(Map<String, Boolean> followings) {
        this.followings = followings;
    }
}
