package com.petworld_madebysocialworld;

import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class PetProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    //private DatabaseReference mDatabase;
    //private FirebaseDatabase fdb;
    private TextView nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);
        initNavigationDrawer();
        // [START initialize_auth]
        // Initialize Firebase Auth
        initFireBase();
        initLayout();
        if (mAuth.getCurrentUser() != null)
            initTextView();
    }

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
       // mDatabase = FirebaseDatabase.getInstance().getReference("pets");
        db = FirebaseFirestore.getInstance();
    }

    private void initTextView() {


        /* METODO LEER ROBERTO */
        String userID =  mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(userID);
        Log.d("test", docRef.toString());




        /*  */
       /* FRACASO TOTAL
       String userID =  mAuth.getCurrentUser().getUid();

       mDatabase.addValueEventListener(new ValueEventListener() {

           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Mascota mascota = dataSnapshot.getValue(Mascota.class);
               if (mascota != null) Log.d("nombre mascota", mascota.getName());
               else Log.d("nombre mascota", "mascota null");
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Log.e("ERROR FIREBASE", databaseError.getMessage());
           }
       });


       */



    }

    private void initLayout() {
       // nombre = findViewById(R.id.textViewNombre);
    }


    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);

        toolBar.setTitle("PerfilMascota");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }
}
