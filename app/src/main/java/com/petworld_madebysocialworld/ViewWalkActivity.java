package com.petworld_madebysocialworld;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private Date pickedDate;



    //layout
    EditText nameWalkEditText;
    EditText descriptionWalkEditText;
    EditText nameRouteEditText;

    // Date Formatter & Hour Formatter
    private java.text.DateFormat df;
    private java.text.DateFormat hf;



    //buttons
    Button editButton;
    Button deleteButton;
    private Button dateInput;
    private Button hourInput;

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
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private void deleteWalkFireBase() {
         final String id = idWalk;

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        ArrayList<DocumentReference> alWalksRef = (ArrayList<DocumentReference>) document.get("walks");
                        for (DocumentReference dr : alWalksRef) {

                            if (dr.getPath().equals("walks/" + id)) {

                                //borra en routes/
                                dr.delete();
                                //borra referencia en users/routes
                                document.getReference().update("walks", FieldValue.arrayRemove(dr));

                            }
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), "Paseo Borrado",
                        Toast.LENGTH_LONG).show();
                startMap();

            }

        });
    }

    private void editWalk() {
        Intent intent = new Intent (getApplicationContext(), EditWalkActivity.class);
        intent.putExtra("id", idWalk);
        startActivityForResult(intent, 0);

    }

    private void initButtons() {
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void initLayout() {
        nameWalkEditText = findViewById(R.id.nameWalkEditText);
        descriptionWalkEditText = findViewById(R.id.descrptionWalkEditText);
        nameRouteEditText = findViewById(R.id.nameRoutekEditText);
        //data
        dateInput = findViewById(R.id.dateInput);
        hourInput = findViewById(R.id.hourInput);
    }

    private void initFireBase() {
        db = FirebaseFirestore.getInstance();

    }

    private void setLayoutText() {
        nameWalkEditText.setText(name);
        descriptionWalkEditText.setText(description);
        dateInput.setText(df.format(pickedDate));
        hourInput.setText(hf.format(pickedDate));
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
                    com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) result.get("start");
                    pickedDate = time.toDate();


                    //images
                    setImages();
                    setLayoutText();
                    readRouteInfo();
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
        // init formatter
        df = new android.text.format.DateFormat().getMediumDateFormat(getApplicationContext());
        hf = new android.text.format.DateFormat().getTimeFormat(getApplicationContext());
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Borrar")
                .setMessage("¿Borrar Paseo?")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code

                        deleteWalkFireBase();
                        startMap();
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }
}
