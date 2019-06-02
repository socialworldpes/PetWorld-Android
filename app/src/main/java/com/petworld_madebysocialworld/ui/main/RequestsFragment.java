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
    private Context context;
    private FriendsSingleton friendsSingleton;
    private RequestsListAdapter customAdapter;

    public RequestsFragment(Context context) {
        this.context = context;
        friendsSingleton = FriendsSingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends_requests, container, false);

        // Updates snapshots
        if (!friendsSingleton.requestsFragmentIni()) {
            friendsSingleton.setRequestsFragment(this);
            friendsSingleton.updateRequestsSnapshots();
            setViewAndAdapter();
        } else {
            friendsSingleton.setRequestsFragment(this);
            setViewAndAdapter();
        }

        return view;
    }

    public void setViewAndAdapter() {
        requestsList = (ListView) view.findViewById(R.id.list);
        customAdapter = new RequestsListAdapter(context, R.layout.fragment_friends_requests, friendsSingleton.getRequestsListInfo());
        requestsList.setAdapter(customAdapter);
    }

}
