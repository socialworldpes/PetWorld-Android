package com.petworld_madebysocialworld;

import Models.Friend;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.petworld_madebysocialworld.FriendsSingleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFriendsActivity extends AppCompatActivity {

    private FriendsSingleton friendsSingleton;
    private ArrayList<String> friendsListId;
    private LinearLayout linearLayoutSheet;
    private String textTmp;
    private EditText text;
    private Context context;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        linearLayoutSheet = (LinearLayout) findViewById(R.id.LayoutMeetings);
        text = (EditText)findViewById(R.id.Search);
        context = this;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        friendsSingleton = FriendsSingleton.getInstance();
        friendsListId = friendsSingleton.getFriendsListId();
        friendsListId.add(user.getUid());
        Toast.makeText(context, "La meva id es " + user.getUid(), Toast.LENGTH_SHORT).show();
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchFriends();
                    return true;
                }
                return false;
            }
        });
    }
    public void searchFriends() {

        textTmp = text.getText().toString();
        if (textTmp != null) {
            linearLayoutSheet.removeAllViews();
            int size = textTmp.length();

            char c = textTmp.charAt(size - 1);//returns h
            String lastChr = String.valueOf((char) (c + 1));
            String newName = textTmp.substring(0, size - 1) + lastChr;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference meetingsRef = db.collection("users");
            Query meetingLocations = meetingsRef.whereGreaterThanOrEqualTo("name", textTmp).whereLessThanOrEqualTo("name", newName);
            meetingLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            if (!friendsListId.contains( document.getId())) {
                                String name = (String) document.get("name");
                                LinearLayout linearLayoutList = new LinearLayout(context);
                                TextView textViewDescreList = new TextView(context);
                                textViewDescreList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                textViewDescreList.setText(name);
                                textViewDescreList.setPadding(40, 20, 40, 20);

                                linearLayoutList.addView(textViewDescreList);

                                final Button friendButton = new Button(context);
                                friendButton.setText("Add Friend");

                                friendButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //when play is clicked show stop button and hide play button
                                        String id = (String) document.getId();
                                        Toast.makeText(context, "Solicitud enviada a " + document.get("name"), Toast.LENGTH_SHORT).show();
                                        sendNotificationToUser(id);
                                        friendButton.setVisibility(View.GONE);
                                    }
                                });

                                linearLayoutList.addView(friendButton);

                                linearLayoutSheet.addView(linearLayoutList);
                            } else {
                                //
                            }
                        }
                    }
                }
            });
        } else {
            Toast.makeText(context, "No Noms", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotificationToUser (final String to_idUser) {

        // See documentation on defining a message payload.
                Map<String, Object> pF = new HashMap<>();
                pF.put("reference", FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                FirebaseFirestore.getInstance().collection("users").document(to_idUser).collection("pendingFriends").add(pF);
    }

}
