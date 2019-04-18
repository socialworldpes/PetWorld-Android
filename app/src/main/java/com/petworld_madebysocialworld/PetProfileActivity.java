package com.petworld_madebysocialworld;

import Models.User;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;


public class PetProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView name;
    private TextView gender;
    private TextView race;
    private TextView specie;
    private TextView comment;
    private String petPath;
    private Button btnEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);
        initFireBase();
        initTextView();
        initIntent();
        initButtons();
        if (mAuth.getCurrentUser() != null)
            initLayout();
        initNavigationDrawer();
    }

    private void initButtons() {
        btnEditar  = findViewById(R.id.buttonEdit);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivity();
            }
        });
    }

    private void editActivity() {
        Intent intent = new Intent(getApplicationContext(), PetUpdateActivity.class);
        intent.putExtra("docPetRef", petPath);
        startActivityForResult(intent, 0);
    }


    private void initIntent() {
        petPath = getIntent().getStringExtra("docPetRef");
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
        Log.d("petProfilePetRef", "" + petPath);
        DocumentReference docRef = db.document(petPath);
        Log.d("userID", userID);
        Log.d("petRefIntent", petPath);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("task size: ", "" + task.getResult());
                    DocumentSnapshot result = task.getResult();

                    name.setText("" + task.getResult().get("name"));
                    gender.setText("" + task.getResult().get("gender"));
                    specie.setText("" + task.getResult().get("specie"));
                    race.setText("" + task.getResult().get("race"));
                    comment.setText("" + task.getResult().get("comment"));
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
