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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;


public class ViewPetActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView name;
    private TextView gender;
    private TextView race;
    private TextView specie;
    private TextView comment;
    private String petPath;
    private Button btnEditar;
    private Button btnBorrar;
    private String userID;
    private String owner;
    private ArrayList<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pet);

        setupToolbar();
        initFireBase();
        initTextView();
        initIntent();
        initButtons();
        identification();
        if (mAuth.getCurrentUser() != null)
            initLayout();

    }




    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("View Mascota");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }

    private void identification() {
        btnEditar  = findViewById(R.id.editButton);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivity();
            }
        });
        btnBorrar = findViewById(R.id.deleteButton);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarMascota();
            }
        });
    }

    private void borrarMascota() {
        AlertDialog diaBox = AskOption();
        diaBox.show();

    }

    private void initButtons() {
        btnEditar  = findViewById(R.id.editButton);
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivity();
            }
        });
        btnEditar.setVisibility(View.INVISIBLE);;
        btnBorrar = findViewById(R.id.deleteButton);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarMascota();
            }
        });
        btnBorrar.setVisibility(View.INVISIBLE);
    }


    private void borrarReferenciaUsuario() {
        Log.d("alPetRef: ", "in");
        Log.d("alPetRef", "" + userID);
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("alPetRef: ", "task is successful");
                    DocumentSnapshot document = task.getResult();
                    Log.d("alPetRef: ", "" + document.toString());
                    if (document.exists()) {
                        Log.d("alPetRef: ", "document exists");
                        ArrayList<DocumentReference> alPetRef = (ArrayList<DocumentReference>) document.get("pets");
                        for (DocumentReference dr: alPetRef) {
                            Log.d("alPetRef: ", petPath + " ---- " + dr.getPath());
                            if (dr.getPath().equals(petPath)) {
                                Log.d("alPetRef:","sn iguales dentro IF");
                                dr.delete(); //borra en pets/
                                document.getReference().update("pets", FieldValue.arrayRemove(dr)); //borra en users/pets
                                break;
                            }
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), "Mascota Borrada",
                        Toast.LENGTH_LONG).show();
                startMap();
            }

        });

        Log.d("alPetRef: ", "out");

    }

    private void editActivity() {
        Intent intent = new Intent(getApplicationContext(), EditPetActivity.class);
        intent.putExtra("docPetRef", petPath);
        startActivityForResult(intent, 0);
    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
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
        name = findViewById(R.id.namePetInput);
        gender = findViewById(R.id.genderPetInput);
        race = findViewById(R.id.speciePetInput);
        specie = findViewById(R.id.racePetInput);
        comment = findViewById(R.id.commentPetInput);
    }

    private void initLayout() {
        userID = mAuth.getCurrentUser().getUid();

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
                    imageUrls = (ArrayList<String>)result.get("photo");

                    name.setText("" + task.getResult().get("name"));
                    gender.setText("" + task.getResult().get("gender"));
                    specie.setText("" + task.getResult().get("specie"));
                    race.setText("" + task.getResult().get("race"));
                    comment.setText("" + task.getResult().get("comment"));
                    owner =(String) task.getResult().get("owner");

                    Log.d("tacobell", "Owner: " + owner + " UserID: " + userID);
                    if (!owner.equals(userID)){
                        btnEditar.setVisibility(View.GONE);;
                        btnBorrar.setVisibility(View.GONE);;
                    } else {
                        btnEditar.setVisibility(View.VISIBLE);;
                        btnBorrar.setVisibility(View.VISIBLE);;
                    }

                    //images
                    ViewPager viewPager = findViewById(R.id.viewPager);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), imageUrls);
                    viewPager.setAdapter(adapter);
                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });

    }


    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Perfil Mascota");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }
    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Borrar")
                .setMessage("¿Borrar mascota?")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code

                        borrarReferenciaUsuario();
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
}
