package com.petworld_madebysocialworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserActivity extends AppCompatActivity {

    private Context context;
    private String id, name, email;
    private Integer frindsSize, petsSize, meetingSize, routesSize, walksSize;
    private FirebaseFirestore db;
    private boolean isFriend;
    private FriendsSingleton friendsSingleton;
    private LinearLayout LayoutFriends, LayoutPets, LayoutMeetings, LayoutRoutes, LayoutWalks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initNavigationDrawer();
        context = this;
        id = getIntent().getStringExtra("id");
        frindsSize =  petsSize = meetingSize = routesSize = walksSize = 0;
        friendsSingleton = FriendsSingleton.getInstance();
        db = FirebaseFirestore.getInstance();
        LayoutFriends = (LinearLayout) findViewById(R.id.layoutFriends);
        LayoutPets = (LinearLayout) findViewById(R.id.layoutPets);
        LayoutMeetings = (LinearLayout) findViewById(R.id.layoutMeetings);
        LayoutRoutes = (LinearLayout) findViewById(R.id.layoutsRoutes);
        LayoutWalks  = (LinearLayout) findViewById(R.id.layoutWalks);

        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            isFriend = true;
        else {
            isFriend = friendsSingleton.isFriend(id);
        }

        FirebaseFirestore.getInstance().collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                TextView nameTv = findViewById(R.id.tvName);
                TextView emailTv = findViewById(R.id.tvEmail);
                TextView petsTv = findViewById(R.id.petsTv);
                TextView friendsTv = findViewById(R.id.friendsTv);
                TextView meetingsTv = findViewById(R.id.meetingsTv);
                TextView routesTv = findViewById(R.id.routesTv);
                TextView walksTv = findViewById(R.id.walksTv);
                ImageView image = findViewById(R.id.photoPerfil);

                if ((String)documentSnapshot.get("imageURL") != null) {
                    Picasso.get().load((String) documentSnapshot.get("imageURL")).into(image);
                } else {
                    Toast.makeText(context, "No foto", Toast.LENGTH_SHORT).show();
                }

                name = (String)documentSnapshot.get("name");
                nameTv.setText(name);

                email = (String)documentSnapshot.get("email");
                emailTv.setText(email);

                ArrayList<DocumentReference> petsQ = (ArrayList<DocumentReference>)documentSnapshot.get("pets");
                if (petsQ != null){
                    petsSize = petsQ.size();
                    if (petsSize > 0 && isFriend) {
                        for (final DocumentReference dc : petsQ) {
                            db.document(dc.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //Toast.makeText(context, "Dins Loop", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            String name = (String) document.get("name");
                                            TextView textViewNameList = new TextView(context);
                                            textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewNameList.setText(name);
                                            textViewNameList.setTextColor(Color.BLACK);
                                            textViewNameList.setTextSize(1, 12);
                                            textViewNameList.setPadding(40, 20, 40, 5);


                                            textViewNameList.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(UserActivity.this, ViewPetActivity.class);
                                                    intent.putExtra("docPetRef", dc.getPath());
                                                    startActivity(intent);
                                                }

                                            });


                                            LayoutPets.addView(textViewNameList);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                petsTv.setText("" + petsSize);

                ArrayList<DocumentReference> frindsQ = (ArrayList<DocumentReference>)documentSnapshot.get("friends");
                if (frindsQ != null){
                    frindsSize = frindsQ.size();
                    if (frindsSize > 0  && isFriend) {
                        for (final DocumentReference dc : frindsQ) {
                            db.document(dc.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //Toast.makeText(context, "Dins Loop", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            String name = (String) document.get("name");
                                            TextView textViewNameList = new TextView(context);
                                            textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewNameList.setText(name);
                                            textViewNameList.setTextColor(Color.BLACK);
                                            textViewNameList.setTextSize(1, 12);
                                            textViewNameList.setPadding(40, 20, 40, 5);

                                            textViewNameList.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String id = document.getId();
                                                    Intent intent = new Intent(UserActivity.this, UserActivity.class);
                                                    intent.putExtra("id", id);
                                                    startActivity(intent);
                                                }
                                            });
                                            LayoutFriends.addView(textViewNameList);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                friendsTv.setText("" + frindsSize);

                ArrayList <DocumentReference> meetingsQ = (ArrayList<DocumentReference>)documentSnapshot.get("meetings");
                if (meetingsQ != null){
                    meetingSize = meetingsQ.size();
                    if (meetingSize > 0  && isFriend) {
                        for (final DocumentReference dc : meetingsQ) {
                            db.document(dc.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //Toast.makeText(context, "Dins Loop", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            String name = (String) document.get("name");
                                            TextView textViewNameList = new TextView(context);
                                            textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewNameList.setText(name);
                                            textViewNameList.setTextColor(Color.BLACK);
                                            textViewNameList.setTextSize(1, 12);
                                            textViewNameList.setPadding(40, 20, 40, 5);

                                            textViewNameList.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    String id = document.getId();
                                                    Intent intent = new Intent(UserActivity.this, ViewMeetingActivity.class);
                                                    intent.putExtra("id", id);
                                                    startActivity(intent);
                                                }
                                            });
                                            LayoutMeetings.addView(textViewNameList);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                meetingsTv.setText("" + meetingSize);

                ArrayList <DocumentReference> routesQ = (ArrayList <DocumentReference>)documentSnapshot.get("routes");
                if (routesQ != null){
                    routesSize = routesQ.size();
                    if (routesSize > 0  && isFriend) {
                        for (final DocumentReference dc : routesQ) {
                            db.document(dc.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //Toast.makeText(context, "Dins Loop", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            String name = (String) document.get("name");
                                            TextView textViewNameList = new TextView(context);
                                            textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewNameList.setText(name);
                                            textViewNameList.setTextColor(Color.BLACK);
                                            textViewNameList.setTextSize(1, 12);
                                            textViewNameList.setPadding(40, 20, 40, 5);

                                            textViewNameList.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String id = document.getId();
                                                    Intent intent = new Intent(UserActivity.this, ViewRouteActivity.class);
                                                    intent.putExtra("id", id);
                                                    startActivity(intent);
                                                }

                                            });
                                            LayoutRoutes.addView(textViewNameList);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                routesTv.setText("" + routesSize);

                ArrayList <DocumentReference> walksQ = (ArrayList <DocumentReference>)documentSnapshot.get("walks");
                if (walksQ != null){
                    walksSize = walksQ.size();
                    if (walksSize > 0  && isFriend) {
                        for (final DocumentReference dc : walksQ) {
                            db.document(dc.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //Toast.makeText(context, "Dins Loop", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            String name = (String) document.get("name");
                                            TextView textViewNameList = new TextView(context);
                                            textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewNameList.setText(name);
                                            textViewNameList.setTextColor(Color.BLACK);
                                            textViewNameList.setTextSize(1, 12);
                                            textViewNameList.setPadding(40, 20, 40, 5);

                                            /*
                                            textViewNameList.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    String id = document.getId();
                                                    Intent intent = new Intent(MapActivity.this, ViewMeetingActivity.class);
                                                    intent.putExtra("id", id);
                                                    startActivity(intent);
                                                }

                                            });
                                            */
                                            LayoutWalks.addView(textViewNameList);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                walksTv.setText("" + walksSize);
            }
        });


    }

    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);

        toolBar.setTitle("Usuario");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }
}
