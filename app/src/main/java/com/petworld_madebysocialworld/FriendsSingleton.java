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

    private boolean friendsListFirst, requestsListFirst;

    private FriendsFragment friendsFragment;
    private RequestsFragment requestsFragment;

    private ArrayList<Friend> friendsListInfo, requestsListInfo;
    private ArrayList<Friend> addFriendsSnapshots, removeFriendsSnapshots;

    private FriendsSingleton() {
        friendsListInfo = new ArrayList<Friend>();
        requestsListInfo = new ArrayList<Friend>();
        addFriendsSnapshots = new ArrayList<Friend>();
        removeFriendsSnapshots = new ArrayList<Friend>();
        friendsListFirst = requestsListFirst = true;
    }

    public static FriendsSingleton getInstance() {
        if (friendsSingleton == null) friendsSingleton = new FriendsSingleton();

        return friendsSingleton;
    }

    public boolean friendsListFirst() {
        if (friendsListFirst) {
            friendsListFirst = false;
            return true;
        }
        return friendsListFirst;
    }

    public boolean requestsListFirst() {
        if (requestsListFirst) {
            requestsListFirst = false;
            return true;
        }
        return requestsListFirst;
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

    public void setFriendsFragment(FriendsFragment fragment) {
        friendsFragment = fragment;
    }

    public void setRequestsFragment(RequestsFragment fragment) {
        requestsFragment = fragment;
    }

    public void addFriendSnapshot(String id, Map<String, Object> data) {
        Friend friend = new Friend(id, (String) data.get("name"), (String) data.get("imageURL"));
        addFriendsSnapshots.add(friend);
    }

    // This method does NOT update the listView
    public boolean loadFriendToList(Friend friend) {

        boolean added;

        if (friendsListInfo.contains(friend)) added = false;
        else {
            // Add friend to friendsList
            added = friendsListInfo.add(friend);
            // If there was only "1 friend" check if its real or delete the noFriend
            if (added && friendsListInfo.size() == 2) removeNoFriends();
        }

        return added;
    }

    public boolean addFriend(Friend friend) {

        boolean loaded = loadFriendToList(friend);

        if (loaded) {
            friendsFragment.setViewAndAdapter();

            // Remove friend from requestsList
            // Eventhough this will not make sense for the friend that initially sends the request
            requestsListInfo.remove(friend);

            if (requestsListInfo.size() == 0) addNoRequests();

            // TODO - creo q peta por esto
            //requestsFragment.setViewAndAdapter();
        }

        return loaded;
    }

    public void deleteFriendSnapshot(String id, Map<String, Object> data) {
        Friend friend = new Friend(id, (String) data.get("name"), (String) data.get("imageURL"));
        removeFriendsSnapshots.add(friend);
    }

    public boolean deleteFriend(Friend friend) {
        // Remove friend from friendsList
        boolean deleted = friendsListInfo.remove(friend);

        if (deleted) {
            // TODO - Not implemented yet
            deleteFriendFromFirebase(FirebaseAuth.getInstance().getCurrentUser().getUid(), friend.getId());

            if (friendsListInfo.size() == 0) addNoFriends();

            // So that it knows that something has changed
            friendsFragment.setViewAndAdapter();

            // The other friend will also delete the friend by receiving an onSnapshot
        }

        return deleted;
    }

    // TODO - Remove friend from FIREBASE!! From my collection AND their collection!!
    private void deleteFriendFromFirebase(String myId, String friendsId) {

    }

    public void updateFriendsSnapshots() {
        for (Friend friend : addFriendsSnapshots) {
            addFriend(friend);
            Log.d("ERROR_CREATE", friend.getId());
        }

        for (Friend friend : removeFriendsSnapshots) {
            deleteFriend(friend);
            Log.d("ERROR_DELETE", friend.getId());
        }
    }

    // This method does NOT update the listView
    public boolean loadRequestToList(Friend friend) {
        // Add request to requestsList
        boolean added = requestsListInfo.add(friend);
        // If there was only "1 request" check if its real or delete the noPendingRequest
        if (added && requestsListInfo.size() == 2) removeNoRequests();

        return added;
    }

    public void addNoFriends() {
        friendsListInfo.add(new Friend("NoFriends", "No tienes amigos",
                "https://cdn.pixabay.com/photo/2016/11/01/03/28/magnifier-1787362_960_720.png"));
    }

    public void removeNoFriends() {
        friendsListInfo.remove(new Friend("NoFriends", "No tienes amigos",
                "https://cdn.pixabay.com/photo/2016/11/01/03/28/magnifier-1787362_960_720.png"));
    }

    public void addNoRequests() {
        requestsListInfo.add(new Friend("NoPendingRequests", "No tienes solicitudes pendientes",
                "https://static.thenounproject.com/png/540056-200.png"));
    }

    public void removeNoRequests() {
        requestsListInfo.remove(new Friend("NoPendingRequests", "No tienes solicitudes pendientes",
                "https://static.thenounproject.com/png/540056-200.png"));
    }

    // WHEN IT ACCEPTS THE PETITION, THIS FUNCTION DOES THE JOB OF CONNECTING
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