package com.petworld_madebysocialworld;

import Models.User;
import android.content.Intent;
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


import java.util.ArrayList;
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
        HashMap<String, Object> mascota =  new HashMap<String, Object>();
        mascota.put("name", name.getText().toString());
        mascota.put("gender", gender.getText().toString());
        mascota.put("specie", specie.getText().toString());
        mascota.put("race", race.getText().toString());
        mascota.put("comment",comment.getText().toString());
        mascota.put("photo", "no foto");
        mascota.put("owner", userID);


         db.collection("pets").add(mascota).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
             @Override
             public void onSuccess(final DocumentReference documentReference) {
                 Log.d("mascotaRefenrece:" , documentReference.getId());

                 String userID = User.getInstance().getAccount().getId();

                 Log.d("userID", userID);
                 DocumentReference docRef = db.collection("users").document(userID);



                 docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if (task.isSuccessful()) {
                             String userID = User.getInstance().getAccount().getId();
                             DocumentSnapshot result = task.getResult();
                             ArrayList<DocumentReference> arrayReference = (ArrayList<DocumentReference>) result.get("pets");
                             if (arrayReference == null) arrayReference =  new ArrayList<>();
                             arrayReference.add(documentReference);

                             //añadir pet a users(userID)
                             db.collection("users").document(userID)
                                     .update("pets", arrayReference)
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


                         } else {
                             Log.w("task ko", "Error getting documents.", task.getException());
                         }
                         Intent intent = new Intent (getApplicationContext(), MapActivity.class);
                         startActivityForResult(intent, 0);
                     }
                 });


             }

        });



    }
}
