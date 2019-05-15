package com.petworld_madebysocialworld;

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
import java.util.ArrayList;
import java.util.Map;

public class SearchFriendsActivity extends AppCompatActivity {

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
            int size = textTmp.length();
            char c = textTmp.charAt(size - 1);//returns h
            String next = String.valueOf((char) (c + 1));
            String newName;
            newName = textTmp.substring(0, size - 1) + next;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference meetingsRef = db.collection("users");
            linearLayoutSheet.removeAllViews();
            Query meetingLocations = meetingsRef.whereGreaterThanOrEqualTo("name", textTmp).whereLessThanOrEqualTo("name", newName);
            meetingLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            String name = (String) document.get("name");
                            LinearLayout linearLayoutList = new LinearLayout(context);
                            TextView textViewDescreList = new TextView(context);
                            textViewDescreList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            textViewDescreList.setText(name);
                            textViewDescreList.setPadding(40, 20, 40, 20);

                            linearLayoutList.addView(textViewDescreList);

                            final Button friendButton = new Button(context);
                            friendButton.setText("Add Friend");
                            friendButton.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String id = (String) document.getId();
                                    friendButton.setVisibility(0);
                                    //Toast.makeText(context, "Id: " + id, Toast.LENGTH_SHORT).show();
                                    //DocumentReference DR_aux = document.getReference();
                                    sendNotificationToUser (id);
                                }
                            });

                            linearLayoutList.addView(friendButton);

                            linearLayoutSheet.addView(linearLayoutList);
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

        FirebaseFirestore.getInstance().collection("users").document(to_idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<DocumentReference> pendingFriendsList = (ArrayList<DocumentReference>)documentSnapshot.get("pendingFriends");
                if (pendingFriendsList == null)
                    pendingFriendsList = new ArrayList<>();
                pendingFriendsList.add(FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                FirebaseFirestore.getInstance().collection("users").document(to_idUser).update("pendingFriends", pendingFriendsList);
            }
        });

        /*final String titleAux = title;
        final String textAux = text;
        FirebaseFirestore.getInstance().collection("users").document(to_idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               String token = (String)documentSnapshot.get("token");
               RemoteMessage.Builder messageBuilder = new RemoteMessage.Builder(token + "@gcm.googleapis.com");
               messageBuilder.setMessageId("1")
                       .addData("Title", titleAux)
                       .addData("text", textAux)
                       .setTtl(600);

               RemoteMessage message = messageBuilder.build();
               Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show();
                // Send a message to the device corresponding to the provided
                // registration token.
               FirebaseMessaging.getInstance().send(message);*/
         /*  }
       });*/

    }
}
