package com.petworld_madebysocialworld;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.petworld_madebysocialworld.ui.Meetings.MeetingsPagerAdapter;
import com.petworld_madebysocialworld.ui.Walks.WalksPagerAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class ViewWalkActivity extends AppCompatActivity {

    //things for UI
    private boolean alreadyInvited;
    private boolean alreadyJoinedBoolean;
    private ViewPager viewPager;
    private TabLayout tabs;
    private WalksPagerAdapter meetingsPagerAdapter;
    Activity actAux;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_walk);
        alreadyJoinedBoolean = false;
        alreadyInvited = false;
        id = getIntent().getStringExtra("idWalk");
        actAux = this;
        setupToolbar();
        alreadyJoined();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Información paseo");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }

    private void initializeAndListenPageChanged() {
        final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                switch (i) {
                    case 0:
                        if (!alreadyJoinedBoolean) {
                            actAux.findViewById(R.id.JoinWalks).setVisibility(View.VISIBLE);
                        }
                        actAux.findViewById(R.id.inviteParticipantsWalks).setVisibility(View.GONE);
                        break;
                    case 1:
                        actAux.findViewById(R.id.JoinWalks).setVisibility(View.GONE);
                        if (!alreadyInvitedThisSession())
                            actAux.findViewById(R.id.inviteParticipantsWalks).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        if (!alreadyJoinedBoolean)
                            actAux.findViewById(R.id.JoinWalks).setVisibility(View.VISIBLE);
                        actAux.findViewById(R.id.inviteParticipantsWalks).setVisibility(View.GONE);
                        break;
                    case 1:
                        actAux.findViewById(R.id.JoinWalks).setVisibility(View.GONE);
                        if (!alreadyInvitedThisSession())
                            actAux.findViewById(R.id.inviteParticipantsWalks).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);

        // do this in a runnable to make sure the viewPager's views are already instantiated before triggering the onPageSelected call
        viewPager.post(new Runnable()
        {
            @Override
            public void run()
            {
                pageChangeListener.onPageSelected(viewPager.getCurrentItem());
            }
        });
    }

    private void alreadyJoined() {

        if (!alreadyJoinedBoolean) {
            FirebaseFirestore.getInstance().collection("walks")
                    .document(getIntent().getStringExtra("idWalk"))
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    List<DocumentReference> aux = (List<DocumentReference>)documentSnapshot.get("participants");
                    DocumentReference auxMyUser = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    secondPartAlreadyJoined(aux.contains(auxMyUser));
                }
            });
        }

    }

    private void secondPartAlreadyJoined(boolean b) {
        alreadyJoinedBoolean = b;
        initializeSyncWithFirebase();
    }

    private void initializeSyncWithFirebase() {
        if (!alreadyJoinedBoolean)
            actAux.findViewById(R.id.JoinWalks).setVisibility(View.VISIBLE);
        meetingsPagerAdapter = new WalksPagerAdapter(actAux, getSupportFragmentManager(), id, actAux);
        viewPager = actAux.findViewById(R.id.view_pager);
        initializeAndListenPageChanged();
        viewPager.setAdapter(meetingsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private boolean alreadyInvitedThisSession() {
        return alreadyInvited;
    }

    public void inviteToWalk (View view) {
        view.findViewById(R.id.inviteParticipantsWalks).setVisibility(View.GONE);
        Toast.makeText(actAux, "Se ha enviado una invitación a tus amigos", Toast.LENGTH_SHORT).show();
        alreadyInvited = true;
        final DocumentReference myUser = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myUser.collection("friends").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot p : queryDocumentSnapshots) {
                    final DocumentReference reference = (DocumentReference) p.get("reference");
                    final Map<String,Object> aux = new HashMap<>();
                    reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<DocumentReference> meetings = (ArrayList<DocumentReference>)documentSnapshot.get("meetings");
                            if (!meetings.contains(FirebaseFirestore.getInstance().collection("walks").document(id))){
                                aux.put("reference", FirebaseFirestore.getInstance().collection("walks")
                                        .document(id));
                                aux.put("nameUser", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                reference.collection("pendingWalks").add(aux);
                            }
                        }
                    });
                }
            }
        });
    }

    public void joinToWalk (View view) {
        view.findViewById(R.id.JoinWalks).setVisibility(View.GONE);
        Toast.makeText(actAux, "Te has unido al paseo", Toast.LENGTH_SHORT).show();
        if (!alreadyJoinedBoolean) {
            alreadyJoinedBoolean = true;
            final DocumentReference myUser = FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final DocumentReference myMeeting = FirebaseFirestore.getInstance().collection("walks")
                    .document((id));
            myUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<DocumentReference> auxList = (ArrayList<DocumentReference>) documentSnapshot.get("walks");
                    auxList.add(myMeeting);
                    myUser.update("walks", auxList);
                }
            });
            myMeeting.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<DocumentReference> auxList = (ArrayList<DocumentReference>) documentSnapshot.get("participants");
                    auxList.add(myUser);
                    myMeeting.update("participants", auxList);
                    viewPager.setAdapter(meetingsPagerAdapter);
                    viewPager.setCurrentItem(0);
                }
            });
        }
    }
}
