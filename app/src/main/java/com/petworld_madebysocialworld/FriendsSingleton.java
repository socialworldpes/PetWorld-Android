package com.petworld_madebysocialworld;

import Models.Friend;
import com.petworld_madebysocialworld.ui.main.FriendsListAdapter;
import com.petworld_madebysocialworld.ui.main.RequestsListAdapter;

import java.util.ArrayList;

public class FriendsSingleton {

    private static FriendsSingleton friendsSingleton;

    private ArrayList<Friend> friendsListInfo;
    private ArrayList<Friend> requestsListInfo;
    private FriendsListAdapter friendsCustomAdapter;
    private RequestsListAdapter requestsCustomAdapter;

    private FriendsSingleton() {
        friendsListInfo = new ArrayList<Friend>();
        requestsListInfo = new ArrayList<Friend>();
    }

    public static FriendsSingleton getInstance() {
        if (friendsSingleton == null) friendsSingleton = new FriendsSingleton();

        return friendsSingleton;
    }

    public ArrayList<Friend> getFriendsListInfo() {
        return friendsListInfo;
    }

    public ArrayList<Friend> getRequestsListInfo() {
        return requestsListInfo;
    }

    public ArrayList<String> getFriendsListId() {
        ArrayList<String> friendsListId = new ArrayList<String>();
        for (Friend friend : friendsListInfo) {
            friendsListId.add(friend.getId());
        }
        return friendsListId;
    }

    public void setFriendsListAdapter(FriendsListAdapter customAdapter) {
        friendsCustomAdapter = customAdapter;
    }

    public void setRequestsListAdapter(RequestsListAdapter customAdapter) {
        requestsCustomAdapter = customAdapter;
    }

}