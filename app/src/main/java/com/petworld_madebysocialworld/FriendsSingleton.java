package com.petworld_madebysocialworld;

import Models.Friend;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petworld_madebysocialworld.ui.main.FriendsListAdapter;
import com.petworld_madebysocialworld.ui.main.RequestsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public boolean addFriend(String id, Map<String, Object> data) {
        Friend friend = new Friend(id, (String) data.get("name"), (String) data.get("imageURL"));
        return addFriend(friend);
    }

    public boolean addFriend(Friend friend) {
        // Add friend to friendsList
        boolean added = friendsListInfo.add(friend);
        friendsCustomAdapter.notifyDataSetChanged();

        // Remove friend from requestsList
        // Eventhough this will not make sense for the friend that initially sends the request
        requestsListInfo.remove(friend);
        requestsCustomAdapter.notifyDataSetChanged();

        return added;
    }

    public boolean deleteFriend(String id, Map<String, Object> data) {
        Friend friend = new Friend(id, (String) data.get("name"), (String) data.get("imageURL"));
        return deleteFriend(friend);
    }

    public boolean deleteFriend(Friend friend) {
        // Remove friend from friendsList
        boolean deleted = friendsListInfo.remove(friend);
        friendsCustomAdapter.notifyDataSetChanged();

        // The other friend will also delete the friend by receiving an onSnapshot

        return deleted;
    }

    //WHEN IT ACCEPTS THE PETITION, THIS FUNCTION DOES THE JOB OF CONNECTING
    public void connect2Friends (String idUser, String idFriend){
        //TODO TESTING IF IT REALLY ADDS FRIENDS OR JUST OVERWRITES
        Map<String, Object> pF = new HashMap<>();
        Map<String, Object> pU = new HashMap<>();
        pF.put("reference", FirebaseFirestore.getInstance().collection("users").document(idFriend));
        pU.put("reference", FirebaseFirestore.getInstance().collection("users").document(idUser));
        FirebaseFirestore.getInstance().collection("users").document(idUser).collection("friends").add(pF);
        FirebaseFirestore.getInstance().collection("users").document(idFriend).collection("friends").add(pU);
    }
}