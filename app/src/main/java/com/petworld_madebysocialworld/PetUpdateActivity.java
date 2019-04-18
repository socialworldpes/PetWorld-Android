package com.petworld_madebysocialworld;

import Models.User;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private Button btnUpdatePet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_update);
        initFireBase();
        initItems();
        initListeners();
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
        btnUpdatePet = findViewById(R.id.buttonUpdatePet);
    }

    private void initLayout() {
        String userID = User.getInstance().getAccount().getId();
        DocumentReference docRef = db.collection("users").document(userID);
        Log.d("userID", userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("task size: ", "" + task.getResult());
                    DocumentSnapshot result = task.getResult();
                    ArrayList<DocumentReference> arrayReference =  (ArrayList<DocumentReference>) result.get("pets");
                    if (arrayReference == null) arrayReference =  new ArrayList<>();
                    DocumentReference petRef = arrayReference.get(0);
                    petRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            name.setText("" + task.getResult().get("name"));
                            gender.setText("" + task.getResult().get("gender"));
                            specie.setText("" + task.getResult().get("specie"));
                            race.setText("" + task.getResult().get("race"));
                            comment.setText("" + task.getResult().get("comment"));
                        }
                    });

                    // Log.d("mascot a size: ", "" + mascota.size());
                    // for (String s: mascota.keySet()) Log.d("map", s);
                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });

    }


    private void updatePet() {

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

        DocumentReference petRef = db.collection("pets").document("ywVGU7eUrF4rCGEylBmh");
        petRef.update(mascota);
    }

    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Actualizar Mascota");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }
}
