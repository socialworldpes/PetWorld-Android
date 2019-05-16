package com.petworld_madebysocialworld;

import Models.Friend;

import java.util.ArrayList;

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

}