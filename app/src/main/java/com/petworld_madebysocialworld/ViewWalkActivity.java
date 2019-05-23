package com.petworld_madebysocialworld;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ViewWalkActivity extends AppCompatActivity {

    //fireStore
    private FirebaseFirestore db;

    //id's
    String idWalk;
    String idRoute;
    DocumentReference routeDocumentReference;

    //walk info
    String name;
    String description;
    GregorianCalendar date;
    private ArrayList<String> imageUrls;

    //layout
    EditText nameWalkEditText;
    EditText descriptionWalkEditText;
    EditText nameRouteEditText;

    //buttons
    Button editButton;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_walk);
        initVariables();
        initLayout();
        initButtons();
        initListeners();
        setupToolbar();
        initFireBase();

        readWalkInfo();

    }

    private void initListeners() {
        nameRouteEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRoute();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWalk();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWalk();
            }
        });
    }

    private void deleteWalk() {
    }

    private void editWalk() {
    }

    private void initButtons() {
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void initLayout() {
        nameWalkEditText = findViewById(R.id.nameWalkEditText);
        descriptionWalkEditText = findViewById(R.id.descrptionWalkEditText);
        nameRouteEditText = findViewById(R.id.nameRoutekEditText);
    }

    private void initFireBase() {
        db = FirebaseFirestore.getInstance();

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
        DocumentReference docRef = db.collection("walks").document(idWalk);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    name = (String) result.get("name");
                    description = (String) result.get("description");
                    imageUrls = (ArrayList<String>)result.get("images");
                    routeDocumentReference = (DocumentReference) result.get("route");


                    //images
                    setImages();
                    setLayoutText();
                    readRouteInfo();
                }
                else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void readRouteInfo() {


        routeDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    nameRouteEditText.setText((String) result.get("name"));
                    idRoute = result.getId();


                }
                else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });

    }

    private void goRoute() {
        Log.d("goRoute : " ,  "in ");
        Intent nextActivity = new Intent(this, ViewRouteActivity.class);
        nextActivity.putExtra("id", idRoute);
        startActivity(nextActivity);
    }

    private void setImages() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), imageUrls);
        viewPager.setAdapter(adapter);
    }

    private void initVariables() {
        idWalk =  getIntent().getExtras().getString("idWalk");
    }
}
