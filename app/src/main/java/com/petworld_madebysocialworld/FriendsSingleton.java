package com.petworld_madebysocialworld;

import Models.Friend;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petworld_madebysocialworld.ui.main.FriendsFragment;
import com.petworld_madebysocialworld.ui.main.RequestsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsSingleton {

    private static FriendsSingleton friendsSingleton;

    private FirebaseFirestore db;

    private boolean friendsFragmentIni, noFriends, requestsFragmentIni, noRequests;

    private FriendsFragment friendsFragment;
    private RequestsFragment requestsFragment;

    private ArrayList<Friend> friendsListInfo, requestsListInfo;
    private ArrayList<Friend> addFriendsSnapshots, removeFriendsSnapshots, addRequestsSnapshots;

    private FriendsSingleton() {
        db = FirebaseFirestore.getInstance();

        friendsListInfo = new ArrayList<Friend>();
        requestsListInfo = new ArrayList<Friend>();

        addFriendsSnapshots = new ArrayList<Friend>();
        removeFriendsSnapshots = new ArrayList<Friend>();
        addRequestsSnapshots = new ArrayList<Friend>();

        friendsFragmentIni = requestsFragmentIni = false;
        noFriends = noRequests = true;
    }

    public static FriendsSingleton getInstance() {
        if (friendsSingleton == null) friendsSingleton = new FriendsSingleton();

        return friendsSingleton;
    }

    public void setFriendsFragment(FriendsFragment fragment) {
        friendsFragment = fragment;
        friendsFragmentIni = true;
    }

    public void setRequestsFragment(RequestsFragment fragment) {
        requestsFragment = fragment;
        requestsFragmentIni = true;
    }

    public boolean friendsFragmentIni() {
        return friendsFragmentIni;
    }

    public boolean requestsFragmentIni() {
        return requestsFragmentIni;
    }

    public void setNoFriends(boolean noFriends) {
        this.noFriends = noFriends;
    }

    public void setNoRequests(boolean noRequests) {
        this.noRequests = noRequests;
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

    public void addFriendSnapshot(String id, Map<String, Object> data) {
        Friend friend = new Friend(id, (String) data.get("name"), (String) data.get("imageURL"));
        addFriendsSnapshots.add(friend);
    }

    public void addRequestSnapshot(String id, Map<String, Object> data) {
        Friend friend = new Friend(id, (String) data.get("name"), (String) data.get("imageURL"));
        addRequestsSnapshots.add(friend);
    }

    // This method does NOT update the listView
    private boolean loadFriendToList(Friend friend) {

        boolean added;

        if (friendsListInfo.contains(friend)) added = false;
        else {
            // Add friend to friendsList
            added = friendsListInfo.add(friend);
        }

        return added;
    }

    public boolean addFriend(Friend friend, boolean request) {

        if (noFriends) removeNoFriends();

        // Adds the friend to the listView
        boolean loaded = loadFriendToList(friend);

        if (loaded) {
            friendsFragment.setViewAndAdapter();

            // Only occurs when a friend is accepted from a request
            if (request) {
                // Therefore it adds the friend to its Firebase and his Firebase list of friends
                String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                connect2Friends(myId, friend.getId());
                deleteRequestFromFirebase(myId, friend.getId());

                // Remove friend from requestsList
                requestsListInfo.remove(friend);

                if (requestsListInfo.size() == 0) addNoRequests();

                if (requestsFragmentIni) requestsFragment.setViewAndAdapter();
            }

        }

        return loaded;
    }

    public void deleteFriendSnapshot(String id, Map<String, Object> data) {
        Friend friend = new Friend(id, (String) data.get("name"), (String) data.get("imageURL"));
        removeFriendsSnapshots.add(friend);
    }

    public boolean deleteFriend(Friend friend, boolean me) {
        // Remove friend from friendsList
        boolean deleted = friendsListInfo.remove(friend);

        if (deleted) {
            // TODO - Not implemented yet
            if (me) deleteFriendFromFirebase(FirebaseAuth.getInstance().getCurrentUser().getUid(), friend.getId());

            if (friendsListInfo.size() == 0) addNoFriends();

            // So that it knows that something has changed
            friendsFragment.setViewAndAdapter();

            // The other friend will also delete the friend by receiving an onSnapshot
        }

        return deleted;
    }

    public boolean deleteRequest(Friend friend) {
        // Remove friend from requestsList
        boolean deleted = requestsListInfo.remove(friend);

        if (deleted) {
            // TODO - Not implemented yet
            // Deletes pendingRequest from Firebase
            deleteRequestFromFirebase(FirebaseAuth.getInstance().getCurrentUser().getUid(), friend.getId());

            if (requestsListInfo.size() == 0) addNoRequests();

            // So that it knows that something has changed
            requestsFragment.setViewAndAdapter();

            // The other friend will also delete the friend by receiving an onSnapshot
        }

        return deleted;
    }

    private void deleteFriendFromFirebase(String myId, String friendsId) {
        db.collection("users").document(myId).collection("friends").document(friendsId).delete();
        db.collection("users").document(friendsId).collection("friends").document(myId).delete();
    }

    private void deleteRequestFromFirebase(String myId, String friendsId) {
        db.collection("users").document(myId).collection("pendingFriends").document(friendsId).delete();
    }

    public void updateFriendsSnapshots() {
        for (Friend friend : addFriendsSnapshots) {
            if (!friendsListInfo.contains(friend)) addFriend(friend, false);
        }

        for (Friend friend : removeFriendsSnapshots) {
            deleteFriend(friend, false);
        }

        addFriendsSnapshots.clear();
        removeFriendsSnapshots.clear();

        if (noFriends || friendsListInfo.size() == 0) addNoFriends();
    }

    public void updateRequestsSnapshots() {
        for (Friend friend : addRequestsSnapshots) {
            if (!requestsListInfo.contains(friend)) addRequest(friend);
        }

        addRequestsSnapshots.clear();

        if (noRequests || requestsListInfo.size() == 0) addNoRequests();
    }

    // This method does NOT update the listView
    public boolean loadRequestToList(Friend friend) {

        boolean added;

        if (requestsListInfo.contains(friend)) added = false;
        else {
            // Add request to requestsList
            added = requestsListInfo.add(friend);
        }

        return added;
    }

    public boolean addRequest(Friend friend) {

        if (noRequests) {
            removeNoRequests();
            noRequests = false;
        }

        boolean loaded = loadRequestToList(friend);

        if (loaded) requestsFragment.setViewAndAdapter();

        return loaded;
    }

    public void acceptRequest(Friend friend) {
        addFriend(friend, true);
    }

    public void refuseRequest(Friend friend) {
        deleteRequest(friend);
    }

    public void addNoFriends() {
        if (!friendsListInfo.contains(Friend.NoFriends)) {
            friendsListInfo.add(Friend.NoFriends);
            friendsFragment.setViewAndAdapter();
            noFriends = true;
        }
    }

    public void removeNoFriends() {
        friendsListInfo.remove(Friend.NoFriends);
        noFriends = false;
    }

    public void addNoRequests() {
        if (!requestsListInfo.contains(Friend.NoPendingRequests)) {
            requestsListInfo.add(Friend.NoPendingRequests);
            requestsFragment.setViewAndAdapter();
            noRequests = true;
        }
    }

    public void removeNoRequests() {
        requestsListInfo.remove(Friend.NoPendingRequests);
        noRequests = false;
    }

    // WHEN IT ACCEPTS THE PETITION, THIS FUNCTION DOES THE JOB OF CONNECTING
    public void connect2Friends (String idUser, String idFriend){
        // TODO – TESTING IF IT REALLY ADDS FRIENDS OR JUST OVERWRITES
        Map<String, Object> pF = new HashMap<>();
        Map<String, Object> pU = new HashMap<>();
        pF.put("reference", db.collection("users").document(idFriend));
        pU.put("reference", db.collection("users").document(idUser));
        db.collection("users").document(idUser).collection("friends").document(idFriend).set(pF);
        db.collection("users").document(idFriend).collection("friends").document(idUser).set(pU);
    }

}