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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import Models.Friend;
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

        getFriendsListAndSetAdapter();

        return view;
    }

    private void getFriendsListAndSetAdapter() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final ArrayList<DocumentReference> friendsRef = (ArrayList<DocumentReference>) document.get("friends");
                        if (friendsRef.size() == 0) {
                            addNoFriends();
                            setViewAndAdapter();
                        }
                        else numDone = 0;
                        for (final DocumentReference dr: friendsRef) {
                            db.document(dr.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            Log.d("OMG123", (String) data.get("name") + " " + (String) data.get("imageURL"));
                                            friendsListInfo.add(new Friend(dr.getId(), (String) data.get("name"), (String) data.get("imageURL")));
                                            if (numDone == 0) setViewAndAdapter();
                                            else if (numDone == friendsRef.size()) customAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void addNoFriends() {
        friendsListInfo.add(new Friend("NoFriends", "No tienes amigos",
                "https://cdn.pixabay.com/photo/2016/11/01/03/28/magnifier-1787362_960_720.png"));
    }

    private void setViewAndAdapter() {
        friendsList = (ListView) view.findViewById(R.id.list);
        customAdapter = new FriendsListAdapter(context, R.layout.fragment_friends);
        friendsList.setAdapter(customAdapter);
        friendsSingleton.setFriendsListAdapter(customAdapter);
    }

    // TODO - LISTENER IF FRIEND_DELETES_ME

}

