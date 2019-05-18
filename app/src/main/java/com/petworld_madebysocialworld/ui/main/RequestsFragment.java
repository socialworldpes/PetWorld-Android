package com.petworld_madebysocialworld.ui.main;

import Models.Friend;
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
import com.google.firebase.firestore.*;
import com.petworld_madebysocialworld.FriendsSingleton;
import com.petworld_madebysocialworld.R;

import java.util.ArrayList;
import java.util.Map;

@SuppressLint("ValidFragment")
public class RequestsFragment extends Fragment {

    private View view;
    private ListView requestsList;
    private FriendsSingleton friendsSingleton;
    private RequestsListAdapter customAdapter;
    private Context context;
    private int numDone;

    public RequestsFragment(Context context) {
        this.context = context;
        friendsSingleton = FriendsSingleton.getInstance();
        friendsSingleton.setRequestsFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends_requests, container, false);

        if (friendsSingleton.requestsListFirst()) getRequestsListAndSetAdapter();
        else setViewAndAdapter();

        return view;
    }

    private void getRequestsListAndSetAdapter() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userID).collection("pendingFriends").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<DocumentSnapshot> friendsRef = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                numDone = 0;
                for (DocumentSnapshot document : friendsRef) {
                    Log.d("OMG123-Requests", String.valueOf(document.getData()));
                    db.document(String.valueOf(document.getDocumentReference("reference").getPath())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            friendsSingleton.loadRequestToList(new Friend(documentSnapshot.getId(), (String) documentSnapshot.get("name"), (String) documentSnapshot.get("imageURL")));
                            if (numDone == 0) {
                                setViewAndAdapter();
                                ++numDone;
                            } else if (numDone == friendsRef.size()) setViewAndAdapter();
                        }
                    });
                }
            }
        });
    }

    public void setViewAndAdapter() {
        requestsList = (ListView) view.findViewById(R.id.list);
        customAdapter = new RequestsListAdapter(context, R.layout.fragment_friends_requests, friendsSingleton.getRequestsListInfo());
        requestsList.setAdapter(customAdapter);
    }

}
