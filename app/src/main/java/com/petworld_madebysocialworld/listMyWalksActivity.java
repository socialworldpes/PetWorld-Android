package com.petworld_madebysocialworld;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class listMyWalksActivity extends AppCompatActivity {

    Context auxContext;
    Bundle savedAux;
    private static String TAG = "listMyWalksActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedAux = savedInstanceState;
        setContentView(R.layout.list_my_walks_activity);
        auxContext = this;
        String idU = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final ArrayList<Walk> result = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").document(idU).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<DocumentReference> aux = (ArrayList<DocumentReference>)documentSnapshot.get("walks");
                        final int total = aux.size();
                        for (DocumentReference dR : aux){
                            dR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    result.add(new Walk (documentSnapshot2.getData(), documentSnapshot2.getId()));
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

    private void initializeView(ArrayList<Walk> walks) {
        Collections.sort(walks);
        Collections.reverse(walks);
        Log.d(TAG, String.valueOf(walks.size()));
        ListMyWalksFragment res = new ListMyWalksFragment(auxContext, walks);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, res)
                .commitNow();
    }
}