package com.petworld_madebysocialworld.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.petworld_madebysocialworld.FriendsSingleton;
import com.petworld_madebysocialworld.R;
import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class ParticipantsFragment extends Fragment {

    private String id;
    private Context context;
    private String collection;
    private View view;
    private ArrayList<Friend> participantsListInfo;
    private ListView ListViewParticipants;
    private ParticipantsListAdapter customAdapter;
    private boolean visibilityFabButton;

    public ParticipantsFragment(Context context, String collection, String id, boolean visibilityFabButton) {
        this.context = context;
        this.id = id;
        this.collection = collection;
        this.participantsListInfo = new ArrayList<>();
        this.visibilityFabButton = visibilityFabButton;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        this.participantsListInfo = new ArrayList<>();

        if (visibilityFabButton)
            ((Activity)context).findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.VISIBLE);
        else
            ((Activity)context).findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.GONE);

        getParticipantsListAndSetAdapter();

        return view;
    }

    private void getParticipantsListAndSetAdapter() {
        FirebaseFirestore.getInstance().collection(collection).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<DocumentReference> aux = (ArrayList<DocumentReference>)documentSnapshot.get("participants");
                final int numDone = 0;
                participantsListInfo =  new ArrayList<>();
                if (aux != null)
                    for (DocumentReference dR : aux){
                        dR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, Object> aux = (Map<String, Object>)documentSnapshot.getData();
                                participantsListInfo.add(new Friend(documentSnapshot.getId(), (String) aux.get("name"), (String) aux.get("imageURL")));
                                if (numDone == 0) setViewAndAdapter(participantsListInfo);
                                else if (numDone == aux.size()) customAdapter.notifyDataSetChanged();
                            }
                        });
                    }
            }
        });
    }

    private void setViewAndAdapter(ArrayList<Friend> participantsListInfo) {
        ListViewParticipants = (ListView) view.findViewById(R.id.list);
        customAdapter = new ParticipantsListAdapter(context, R.layout.fragment_friends, participantsListInfo);
        ListViewParticipants.setAdapter(customAdapter);
    }

}

