package com.petworld_madebysocialworld;

import Models.Friend;

import java.util.ArrayList;
import java.util.Map;

public class FriendsSingleton {

    private static FriendsSingleton friendsSingleton;

    private ArrayList<Friend> friendsListInfo;

    private FriendsSingleton() {
        friendsListInfo = new ArrayList<Friend>();
    }

    public static FriendsSingleton getInstance() {
        if (friendsSingleton == null) friendsSingleton = new FriendsSingleton();

        return friendsSingleton;
    }

    public ArrayList<Friend> getFriendsListInfo() {
        return friendsListInfo;
    }

    public ArrayList<String> getFriendsListId() {
        ArrayList<String> friendsListId = new ArrayList<String>();
        for (Friend friend : friendsListInfo) {
            friendsListId.add(friend.getId());
        }
        return friendsListId;
    }

    public void addFriend(String id, Map<String, Object> data) {

    }

    public void deleteFriend(String id, Map<String, Object> data) {
    }
}