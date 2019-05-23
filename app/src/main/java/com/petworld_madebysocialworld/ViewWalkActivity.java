package com.petworld_madebysocialworld;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ViewWalkActivity extends AppCompatActivity {

    //fireStore
    private FirebaseFirestore db;

    //id's
    String idWalk;
    String idRoute;

    //walk info
    String name;
    String description;
    GregorianCalendar date;
    private ArrayList<String> imageUrls;

    //layout
    EditText nameWalkEditText;
    EditText descriptionWalkEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_walk);
        initVariables();
        initLayout();
        setupToolbar();
        initFireBase();

        readWalkInfo();

    }

    private void initLayout() {
        nameWalkEditText = findViewById(R.id.nameWalkEditText);
        descriptionWalkEditText =findViewById(R.id.descrptionWalkEditText);
    }

    private void initFireBase() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("walks").document(idWalk);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    name = (String) result.get("name");
                    description = (String) result.get("description");

                    setLayoutText();
                }
                else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void setLayoutText() {
        nameWalkEditText.setText(name);
        descriptionWalkEditText.setText(description);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("View Walk");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }
    private void readWalkInfo() {

    }

    private void initVariables() {
        idWalk =  getIntent().getExtras().getString("idWalk");
    }
}
