package com.petworld_madebysocialworld;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {

    private Context context;
    private String id, name, email;
    private Integer frindsSize, petsSize, meetingSize, routesSize, walksSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initNavigationDrawer();
        context = this;
        id = getIntent().getStringExtra("id");
        frindsSize =  petsSize = meetingSize = routesSize = walksSize = 0;

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

                name = (String)documentSnapshot.get("name");
                nameTv.setText(name);

                email = (String)documentSnapshot.get("email");
                emailTv.setText(email);

                ArrayList petsQ = (ArrayList)documentSnapshot.get("pets");
                if (petsQ != null){
                    petsSize = petsQ.size();
                }
                petsTv.setText("Mascotas: " + petsSize);

                ArrayList frindsQ = (ArrayList)documentSnapshot.get("frinds");
                if (frindsQ != null){
                    frindsSize = frindsQ.size();
                }
                friendsTv.setText("Amigos: " + frindsSize);

                ArrayList meetingsQ = (ArrayList)documentSnapshot.get("meetings");
                if (meetingsQ != null){
                    meetingSize = meetingsQ.size();
                }
                meetingsTv.setText("Eventos: " + meetingSize);

                ArrayList routesQ = (ArrayList)documentSnapshot.get("routes");
                if (routesQ != null){
                    routesSize = routesQ.size();
                }
                routesTv.setText("Rutas: " + routesSize);

                ArrayList walksQ = (ArrayList)documentSnapshot.get("walks");
                if (walksQ != null){
                    walksSize = walksQ.size();
                }
                walksTv.setText("Paseos: " + walksSize);
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
