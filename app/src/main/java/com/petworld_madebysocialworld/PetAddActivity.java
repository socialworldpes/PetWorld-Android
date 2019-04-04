package com.petworld_madebysocialworld;

import Models.User;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PetAddActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText name;
    private EditText gender;
    private EditText specie;
    private EditText race;
    private EditText comment;
    private Button btnAddPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_add);
        initNavigationDrawer();
        initFireBase();
        initLayout();
        initListeners();

    }

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initListeners() {
        btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPet();
            }
        });
    }

    private void initLayout() {
        name = findViewById(R.id.editTextName);
        gender = findViewById(R.id.editTextGender);
        specie = findViewById(R.id.editTextSpecie);
        race = findViewById(R.id.editTextRace);
        comment = findViewById(R.id.editTextComment);
        btnAddPet = findViewById(R.id.buttonAddPet);
    }


    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Añadir Mascota");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }

    private void addPet() {

        String userID = User.getInstance().getAccount().getId();
        Log.d("userID", userID);
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("name", name.getText().toString());
        mascota.put("gender", gender.getText().toString());
        mascota.put("specie", specie.getText().toString());
        mascota.put("race", race.getText().toString());
        mascota.put("comment",comment.getText().toString());
        mascota.put("photo", "no foto");
        mascota.put("owner", userID);

        db.collection("users").document(userID)
                .update("pets", mascota)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("mascota", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("mascota", "Error writing document", e);
                    }
                });
    }
}
