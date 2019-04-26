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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PetUpdateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText name;
    private EditText gender;
    private EditText race;
    private EditText specie;
    private EditText comment;
    private TextView nameUpdate;
    private TextView genderUpdate;
    private TextView raceUpdate;
    private TextView specieUpdate;
    private TextView commentUpdate;
    private String petPath;
    private Button btnUpdatePet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_update_2);
        initFireBase();
        initItems();
        initListeners();
        initIntent();
        if (mAuth.getCurrentUser() != null)
            initLayout();
        initNavigationDrawer();
    }

    private void initListeners() {
        btnUpdatePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePet();
            }
        });
    }


    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initItems() {
        name = findViewById(R.id.editTextName);
        gender = findViewById(R.id.editTextGender);
        race = findViewById(R.id.editTextRace);
        specie = findViewById(R.id.editTextSpecie);
        comment = findViewById(R.id.editTextComment);
        nameUpdate = findViewById(R.id.headingNameUpdate);
        genderUpdate = findViewById(R.id.headingGenderUpdate);
        raceUpdate = findViewById(R.id.headingRaceUpdate);
        specieUpdate = findViewById(R.id.headingSpecieUpdate);
        commentUpdate = findViewById(R.id.headingCommentUpdate);
        btnUpdatePet = findViewById(R.id.buttonUpdatePet);
    }

    private void initLayout() {
        String userID = User.getInstance().getAccount().getId();
        DocumentReference docRef = db.document(petPath);
        Log.d("userID", userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    name.setText("" + task.getResult().get("name"));
                    gender.setText("" + task.getResult().get("gender"));
                    specie.setText("" + task.getResult().get("specie"));
                    race.setText("" + task.getResult().get("race"));
                    comment.setText("" + task.getResult().get("comment"));



                    // Log.d("mascot a size: ", "" + mascota.size());
                    // for (String s: mascota.keySet()) Log.d("map", s);
                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });

    }

    private void initIntent() {
        petPath = getIntent().getStringExtra("docPetRef");
    }
    private void updatePet() {

        String userID = User.getInstance().getAccount().getId();
        Log.d("userID", userID);
        HashMap<String, Object> mascota =  new HashMap<String, Object>();
        if (checkNulls()) {


            mascota.put("name", name.getText().toString());
            mascota.put("gender", gender.getText().toString());
            mascota.put("specie", specie.getText().toString());
            mascota.put("race", race.getText().toString());
            mascota.put("comment",comment.getText().toString());
            mascota.put("photo", "no foto");
            mascota.put("owner", userID);

            DocumentReference petRef = db.document(petPath);
            petRef.update(mascota);
            Toast.makeText(getApplicationContext(), "Mascota Editada",
                    Toast.LENGTH_LONG).show();
            startMap();
        }
    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Editar Mascota");
        setSupportActionBar(toolBar);
        //DrawerUtil.getDrawer(this,toolBar);
    }
    private boolean checkNulls() {
        String check;
        boolean result = true;
        check = name.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingNameUpdate));
            result = false;
        }
        else resetHeading(R.id.headingNameUpdate);
        check = gender.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingGenderUpdate));
            result = false;
        }
        else resetHeading(R.id.headingGenderUpdate);
        check = specie.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingSpecieUpdate));
            result = false;
        }
        else resetHeading(R.id.headingSpecieUpdate);
        check = race.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingRaceUpdate));
            result = false;
        }
        else resetHeading(R.id.headingRaceUpdate);
        check = comment.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingCommentUpdate));
            result = false;
        }
        else resetHeading(R.id.headingCommentUpdate);
        return result;
    }

    private void resetHeading(int headingName) {
        TextView textView = (TextView)findViewById(headingName);
        textView.setText(textView.getText().toString().substring(0));
    }

    private void printErrorNull(View viewById) {
        TextView textView = (TextView) viewById;
        String oldText = textView.getText().toString();
        if (oldText.length() < 10) textView.setText(oldText + "         Error: "+ oldText.substring(0) +" no puede ser nulo");
    }
}
