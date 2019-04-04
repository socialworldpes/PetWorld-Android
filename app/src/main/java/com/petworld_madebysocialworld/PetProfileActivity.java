package com.petworld_madebysocialworld;

import Models.User;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class PetProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView name;
    private TextView gender;
    private TextView race;
    private TextView specie;
    private TextView comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);
        initFireBase();
        initTextView();
        if (mAuth.getCurrentUser() != null)
            initLayout();
        initNavigationDrawer();
    }

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initTextView() {
        name = findViewById(R.id.textViewName);
        gender = findViewById(R.id.textViewGender);
        race = findViewById(R.id.textViewRace);
        specie = findViewById(R.id.textViewSpecie);
        comment = findViewById(R.id.textViewComment);
    }

    private void initLayout() {
        String userID = User.getInstance().getAccount().getId();
        DocumentReference docRef = db.collection("users").document(userID);
        Log.d("test", docRef.toString());

        db.collection("pets").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("task size: ", "" + task.getResult().size());
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("task ok", document.getId() + " => " + document.getData());

                    }
                    Map<String, Object> mascota = task.getResult().getDocuments().get(0).getData();
                    name.setText("" + mascota.get("name"));
                    gender.setText("" + mascota.get("gender"));
                    race.setText("" + mascota.get("race"));
                    specie.setText("" + mascota.get("specie"));
                    comment.setText("" + mascota.get("comment"));
                    // Log.d("mascot a size: ", "" + mascota.size());
                    // for (String s: mascota.keySet()) Log.d("map", s);
                    Log.d("mascota name: ", "" + mascota.get("name"));
                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });

    }


    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("PerfilMascota");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }
}
