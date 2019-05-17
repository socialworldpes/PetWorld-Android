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
    private ArrayList<Friend> friendsListInfo;
    private FriendsListAdapter customAdapter;
    private int numDone;

    public FriendsFragment(Context context) {
        this.context = context;
        friendsSingleton = FriendsSingleton.getInstance();
        friendsListInfo = friendsSingleton.getFriendsListInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        if (friendsSingleton.friendsListFirst()) getFriendsListAndSetAdapter();
        else setViewAndAdapter();

        return view;
    }

    private void getFriendsListAndSetAdapter() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userID).collection("friends").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<DocumentSnapshot> friendsRef = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                if (friendsRef.size() == 0) {
                    friendsSingleton.addNoFriends();
                    setViewAndAdapter();
                } else numDone = 0;
                for (DocumentSnapshot document : friendsRef) {
                    Log.d("OMG123-Friends", String.valueOf(document.getData()));
                    db.document(String.valueOf(document.getDocumentReference("reference").getPath())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            friendsListInfo.add(new Friend(documentSnapshot.getId(), (String) documentSnapshot.get("name"), (String) documentSnapshot.get("imageURL")));
                            if (numDone == 0) setViewAndAdapter();
                            else if (numDone == friendsRef.size()) customAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void setViewAndAdapter() {
        friendsList = (ListView) view.findViewById(R.id.list);
        customAdapter = new FriendsListAdapter(context, R.layout.fragment_friends);
        friendsList.setAdapter(customAdapter);
        friendsSingleton.setFriendsListAdapter(customAdapter);
    }

    // TODO - LISTENER IF FRIEND_DELETES_ME

}

