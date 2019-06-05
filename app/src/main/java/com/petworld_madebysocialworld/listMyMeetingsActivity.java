package com.petworld_madebysocialworld;

import Models.Meeting;
import Models.Walk;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petworld_madebysocialworld.ui.listmymeetings.ListMyMeetingsFragment;
import com.petworld_madebysocialworld.ui.listmywalks.ListMyWalksFragment;

import java.util.ArrayList;
import java.util.Collections;

public class listMyMeetingsActivity extends AppCompatActivity {

    Context auxContext;
    Bundle savedAux;
    private static String TAG = "listMyMeetingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedAux = savedInstanceState;
        setContentView(R.layout.list_my_meetings_activity);
        auxContext = this;
        String idU = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final ArrayList<Meeting> result = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").document(idU).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<DocumentReference> aux = (ArrayList<DocumentReference>)documentSnapshot.get("meetings");
                        final int total = aux.size();
                        for (DocumentReference dR : aux){
                            dR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    result.add(new Meeting (documentSnapshot2.getData(), documentSnapshot2.getId()));
                                    if (result.size() == total) {
                                        Log.d("listMyWalksActivity", "HE entrado en result == total");
                                        initializeView(result);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void initializeView(ArrayList<Meeting> meetings) {
        Collections.sort(meetings);
        Collections.reverse(meetings);
        Log.d(TAG, String.valueOf(meetings.size()));
        ListMyMeetingsFragment res = new ListMyMeetingsFragment(auxContext, meetings);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, res)
                .commitNow();
    }
}