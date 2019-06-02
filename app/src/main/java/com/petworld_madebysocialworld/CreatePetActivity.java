package com.petworld_madebysocialworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.*;
import com.google.firebase.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter;
import com.sangcomz.fishbun.define.Define;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CreatePetActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Layout
    private EditText name;
    private EditText gender;
    private Spinner specie;
    private EditText race;
    private EditText comment;
    private Button btnAddPet;
    private Button btnUploadImage;

    //images
    private Bitmap imagePerfil;
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;
    public static final int PICK_IMAGE = 1;

    //booleans
    private boolean imagesCanContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pet);

        setupToolbar();
        initFireBase();
        initLayout();
        initVariables();
        initListeners();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Añadir Mascota");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }

    private void initVariables() {
        imagesCanContinue = false;
        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();

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
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
    }

    private void initLayout() {
        name = findViewById(R.id.namePetInput);
        gender = findViewById(R.id.genderPetInput);
        race = findViewById(R.id.racePetInput);
        comment = findViewById(R.id.commentPetInput);
        btnAddPet = findViewById(R.id.buttonAddPet);
        btnUploadImage = findViewById(R.id.buttonLoadImage);

        //init specie dropdown
        String[] arraySpecie = new String[] {
                "Perro", "Gato", "Hamster", "Conejo", "Ave", "Pez", "Reptil", "Invertebrado", "Otros"
        };
        specie = findViewById(R.id.selectSpecie);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpecie);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specie.setAdapter(adapter);
    }



    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Añadir Mascota");
        setSupportActionBar(toolBar);
    }

    private void refreshImageView() {
        for (Uri uri: uriImages)
            urlImages.add(uri.toString());

        ViewPager viewPager= findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), urlImages);
        viewPager.setAdapter(adapter);
    }

    private void loadImage(){
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }
    private void addPet() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> mascota =  new HashMap<String, Object>();


        mascota.put("name", name.getText().toString());
        mascota.put("gender", gender.getText().toString());
        mascota.put("specie", specie.getSelectedItem().toString());
        mascota.put("race", race.getText().toString());
        mascota.put("comment", comment.getText().toString());
        mascota.put("photo", Arrays.asList());
        mascota.put("owner", userID);

        db.collection("pets").add(mascota).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {

                final DocumentReference docRAux = documentReference;
                for (int i = 0; i < uriImages.size(); i++) {
                    final int j = i;
                    final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("pets/" + documentReference.getId() + "_" + i);
                    Uri file = uriImages.get(i);

                    UploadTask uploadTask = imagesRef.putFile(file);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return imagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                urlImages.add(task.getResult().toString());
                                docRAux.update("photo", urlImages);
                            } else {
                                // Handle failures
                                // ...
                            }
                        }

                    });
                }


                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference docRef = db.collection("users").document(userID);


                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DocumentSnapshot result = task.getResult();
                            ArrayList<DocumentReference> arrayReference = (ArrayList<DocumentReference>) result.get("pets");
                            if (arrayReference == null) arrayReference = new ArrayList<>();
                            arrayReference.add(documentReference);

                            //añadir pet a users(userID)
                            db.collection("users").document(userID)
                                    .update("pets", arrayReference)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });


                        } else {
                        }
                        Toast.makeText(getApplicationContext(), "Mascota Añadida",
                                Toast.LENGTH_LONG).show();
                        startMap();
                    }
                });


            }

        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    uriImages = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    if (uriImages.size() > 0){
                        imagesCanContinue = true;
                        refreshImageView();
                    }
                    break;
                }
        }
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

    private void startMap() {
       Intent intent = new Intent (getApplicationContext(), MapActivity.class);
       startActivityForResult(intent, 0);
    }
}
