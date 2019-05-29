package com.petworld_madebysocialworld;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.view.View;
import com.petworld_madebysocialworld.ui.Meetings.MeetingsPagerAdapter;

public class ViewMeetingActivity extends AppCompatActivity {

    private boolean alreadyInvited;
    private boolean alreadyJoinedBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alreadyJoinedBoolean = false;
        alreadyInvited = false;
        Log.d("VIEW MEETINGPRIMERA VEZ", "already joines?" + String.valueOf(alreadyJoinedBoolean));
        alreadyJoined();
        Log.d("VIEW MEETINGSEGUNDA VEZ", "already joines?" + String.valueOf(alreadyJoinedBoolean));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meting);
        final Activity actAux = this;
        actAux.findViewById(R.id.JoinMeeting).setVisibility(View.GONE);
        if (alreadyJoinedBoolean) {
            Log.d("VIEW MEETINGSEGUNDA VEZ", "he entrado por gone");
            actAux.findViewById(R.id.JoinMeeting).setVisibility(View.GONE);
        }
        MeetingsPagerAdapter meetingsPagerAdapter = new MeetingsPagerAdapter(this, getSupportFragmentManager(), getIntent().getStringExtra("id"), this, !alreadyJoinedBoolean, !alreadyInvited);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                switch (i) {
                    case 0:
                        if (!alreadyJoinedBoolean) {
                            if (!alreadyJoined()) {
                                Log.d("VIEW MEETINGSEGUNDA VEZ", "he entrado por gone");
                                actAux.findViewById(R.id.JoinMeeting).setVisibility(View.VISIBLE);
                            }
                        }
                        actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.GONE);
                        break;
                    case 1:
                        actAux.findViewById(R.id.JoinMeeting).setVisibility(View.GONE);
                        if (!alreadyInvitedThisSession()){
                            actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        if (!alreadyJoinedBoolean) {
                            if (!alreadyJoined()) {
                                Log.d("VIEW MEETINGSEGUNDA VEZ", "he entrado por gone");
                                actAux.findViewById(R.id.JoinMeeting).setVisibility(View.VISIBLE);
                            }                        }
                        actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.GONE);
                        break;
                    case 1:
                        actAux.findViewById(R.id.JoinMeeting).setVisibility(View.GONE);
                        if (!alreadyInvitedThisSession()){
                            actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        boolean b = meetingsPagerAdapter == null;
        Log.d("ViewMeetingActivity", "Es nulo?  " + b);
        viewPager.setAdapter(meetingsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private boolean alreadyJoined() {

        Query q = null;
        if (!alreadyJoinedBoolean) {
            q = FirebaseFirestore.getInstance().collection("meetings")
                    .document(getIntent().getStringExtra("id"))
                    .collection("participants").whereEqualTo("reference", FirebaseAuth.getInstance().getCurrentUser().getUid());
            String s = String.valueOf(q == null);
            Log.d("EYYY", "La q es nulo?" + s);
        }
        return (alreadyJoinedBoolean = (q != null));
    }

    private boolean alreadyInvitedThisSession() {
        return alreadyInvited;
    }

    public void inviteToMeeting (View view) {
        alreadyInvited = true;
        DocumentReference myUser = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myUser.collection("friends").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot p : queryDocumentSnapshots) {
                    DocumentReference reference = (DocumentReference) p.get("reference");
                    Map<String,Object> aux = new HashMap<>();
                    aux.put("reference", FirebaseFirestore.getInstance().collection("meetings")
                            .document(getIntent().getStringExtra("id")));
                    aux.put("nameUser", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    reference.collection("pendentMeetings").add(aux);
                }
            }
        });
    }

    public void joinToMeeting (View view) {
        alreadyJoinedBoolean = true;
        final DocumentReference myUser = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DocumentReference myMeeting = FirebaseFirestore.getInstance().collection("meetings")
                .document((getIntent().getStringExtra("id")));
        myUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<DocumentReference> auxList = (ArrayList<DocumentReference>)documentSnapshot.get("meetings");
                auxList.add(myMeeting);
                myUser.update("meetings", auxList);
            }
        });
        myMeeting.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<DocumentReference> auxList = (ArrayList<DocumentReference>)documentSnapshot.get("participants");
                auxList.add(myUser);
                myMeeting.update("participants", auxList);
            }
        });
    }

}
