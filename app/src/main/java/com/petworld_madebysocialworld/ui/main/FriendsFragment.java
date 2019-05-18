package com.petworld_madebysocialworld.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import Models.Friend;
import com.google.firebase.firestore.QuerySnapshot;
import com.petworld_madebysocialworld.FriendsSingleton;
import com.petworld_madebysocialworld.R;

import java.util.ArrayList;
import java.util.Map;

@SuppressLint("ValidFragment")
public class FriendsFragment extends Fragment {

    private View view;
    private ListView friendsList;
    private Context context;
    private FriendsSingleton friendsSingleton;
    private FriendsListAdapter customAdapter;

    public FriendsFragment(Context context) {
        this.context = context;
        friendsSingleton = FriendsSingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Updates snapshots
        if (!friendsSingleton.friendsFragmentIni()) {
            friendsSingleton.setFriendsFragment(this);
            friendsSingleton.updateFriendsSnapshots();
        } else {
            friendsSingleton.setFriendsFragment(this);
            setViewAndAdapter();
        }

        return view;
    }

    public void setViewAndAdapter() {
        friendsList = (ListView) view.findViewById(R.id.list);
        customAdapter = new FriendsListAdapter(context, R.layout.fragment_friends, friendsSingleton.getFriendsListInfo());
        friendsList.setAdapter(customAdapter);
    }

}

