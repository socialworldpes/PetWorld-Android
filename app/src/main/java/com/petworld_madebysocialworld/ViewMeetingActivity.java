package com.petworld_madebysocialworld;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import com.petworld_madebysocialworld.ui.Meetings.MeetingsPagerAdapter;

import javax.annotation.Nullable;

public class ViewMeetingActivity extends AppCompatActivity {

    private boolean alreadyInvited;
    private boolean alreadyJoinedBoolean;
    private ViewPager viewPager;
    private TabLayout tabs;
    private MeetingsPagerAdapter meetingsPagerAdapter;
    Activity actAux;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        alreadyJoinedBoolean = false;
        alreadyInvited = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meting);
        id = getIntent().getStringExtra("id");
        Log.d("ID1", id);
        actAux = this;
        alreadyJoined();
    }

    private void initializeAndListenPageChanged() {
        final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                switch (i) {
                    case 0:
                        if (!alreadyJoinedBoolean) {
                            Toast.makeText(actAux, "Ey, estoy en el visible al cambiar de page", Toast.LENGTH_LONG);
                            actAux.findViewById(R.id.JoinMeeting).setVisibility(View.VISIBLE);
                        }
                        actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.GONE);
                        break;
                    case 1:
                        actAux.findViewById(R.id.JoinMeeting).setVisibility(View.GONE);
                        if (!alreadyInvitedThisSession())
                            actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        if (!alreadyJoinedBoolean)
                            actAux.findViewById(R.id.JoinMeeting).setVisibility(View.VISIBLE);
                        actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.GONE);
                        break;
                    case 1:
                        actAux.findViewById(R.id.JoinMeeting).setVisibility(View.GONE);
                        if (!alreadyInvitedThisSession())
                            actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.VISIBLE);
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
            Query q = null;
            FirebaseFirestore.getInstance().collection("meetings")
                    .document(getIntent().getStringExtra("id"))
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
            actAux.findViewById(R.id.JoinMeeting).setVisibility(View.VISIBLE);
        Log.d("ID2", id);
        meetingsPagerAdapter = new MeetingsPagerAdapter(actAux, getSupportFragmentManager(), id, actAux);
        viewPager = actAux.findViewById(R.id.view_pager);
        initializeAndListenPageChanged();
        viewPager.setAdapter(meetingsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private boolean alreadyInvitedThisSession() {
        return alreadyInvited;
    }

    public void inviteToMeeting (View view) {
        view.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.GONE);
        Toast.makeText(view.getContext(), "Se ha enviado una invitación a tus amigos", Toast.LENGTH_SHORT);
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
                            if (!meetings.contains(FirebaseFirestore.getInstance().collection("meetings").document(getIntent().getStringExtra("id")))){
                                aux.put("reference", FirebaseFirestore.getInstance().collection("meetings")
                                        .document(getIntent().getStringExtra("id")));
                                aux.put("nameUser", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                reference.collection("pendingMeetings").add(aux);
                            }
                        }
                    });
                }
            }
        });
    }

    public void joinToMeeting (View view) {
        view.findViewById(R.id.JoinMeeting).setVisibility(View.GONE);
        Toast.makeText(view.getContext(), "Te has unido a la quedada", Toast.LENGTH_SHORT);
        if (!alreadyJoinedBoolean) {
            alreadyJoinedBoolean = true;
            final DocumentReference myUser = FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final DocumentReference myMeeting = FirebaseFirestore.getInstance().collection("meetings")
                    .document((getIntent().getStringExtra("id")));
            myUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<DocumentReference> auxList = (ArrayList<DocumentReference>) documentSnapshot.get("meetings");
                    auxList.add(myMeeting);
                    myUser.update("meetings", auxList);
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
