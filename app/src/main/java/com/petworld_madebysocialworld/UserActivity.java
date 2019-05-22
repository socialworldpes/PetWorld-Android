package com.petworld_madebysocialworld;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
        isFriend = friendsSingleton.isFriend(id);
        if (isFriend){
            Toast.makeText(context, "Es amic", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "No es amic", Toast.LENGTH_SHORT).show();
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
                    if (petsSize > 0) {
                        LinearLayout linearLayoutTmp = (LinearLayout) findViewById(R.id.LayoutMeetings);
                        for (final DocumentReference dc : petsQ) {
                            db.document(dc.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //Toast.makeText(context, "Dins Loop", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            String name = (String) document.get("name");
                                            String comment = (String) document.get("comment");
                                            String gender = (String) document.get("gender");
                                            Toast.makeText(context, "Name: " + name + " Comment: " + comment + " gender: " + gender, Toast.LENGTH_SHORT).show();

                                            TextView textViewNameList = new TextView(context);
                                            textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewNameList.setText(name);
                                            textViewNameList.setTextColor(Color.BLACK);
                                            textViewNameList.setTextSize(1, 20);
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

                                            linearLayoutTmp.addView(textViewNameList);

                                            Timestamp timeList = (Timestamp) mapTmp.get("start");
                                            Date timeDateList = timeList.toDate();
                                            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            String timeStringList = formatter.format(timeDateList);

                                            TextView textViewTime = new TextView(context);
                                            textViewTime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewTime.setText(timeStringList);
                                            textViewTime.setPadding(40, 5, 40, 20);

                                            linearLayoutTmp.addView(textViewTime);


                                            String descriptionList = (String) mapTmp.get("description");

                                            TextView textViewDescreList = new TextView(context);
                                            textViewDescreList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                                            textViewDescreList.setText(descriptionList);
                                            textViewDescreList.setPadding(40, 20, 40, 20);

                                            linearLayoutSheet.addView(textViewDescreList);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                petsTv.setText("" + petsSize);

                ArrayList<String> frindsQ = (ArrayList<String>)documentSnapshot.get("frinds");
                if (frindsQ != null){
                    frindsSize = frindsQ.size();
                }
                friendsTv.setText("" + frindsSize);

                ArrayList meetingsQ = (ArrayList)documentSnapshot.get("meetings");
                if (meetingsQ != null){
                    meetingSize = meetingsQ.size();
                }
                meetingsTv.setText("" + meetingSize);

                ArrayList routesQ = (ArrayList)documentSnapshot.get("routes");
                if (routesQ != null){
                    routesSize = routesQ.size();
                }
                routesTv.setText("" + routesSize);

                ArrayList walksQ = (ArrayList)documentSnapshot.get("walks");
                if (walksQ != null){
                    walksSize = walksQ.size();
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
