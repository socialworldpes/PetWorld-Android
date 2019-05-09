package com.petworld_madebysocialworld;

import Models.User;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
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

    public static final int PICK_IMAGE = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText name;
    private EditText gender;
    private EditText specie;
    private EditText race;
    private EditText comment;
    private Button btnAddPet;
    private Button btnUploadImage;
    private Bitmap imagePerfil;

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;

    //booleans
    private boolean imagesCanContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_add_2);
        initNavigationDrawer();
        initFireBase();
        initLayout();
        initVariables();
        initListeners();

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
        name = findViewById(R.id.editTextName);
        gender = findViewById(R.id.editTextGender);
        specie = findViewById(R.id.editTextSpecie);
        race = findViewById(R.id.editTextRace);
        comment = findViewById(R.id.editTextComment);
        btnAddPet = findViewById(R.id.buttonAddPet);
        btnUploadImage = findViewById(R.id.buttonLoadImage);
    }


    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Añadir Mascota");
        setSupportActionBar(toolBar);
       // DrawerUtil.getDrawer(this,toolBar);
    }


    private void loadImage(){
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }
    private void addPet() {
        Log.d("PRUEBAImagesSize", "Images size: " + uriImages.size());
        String userID = User.getInstance().getAccount().getId();
        Log.d("userID", userID);
        HashMap<String, Object> mascota =  new HashMap<String, Object>();
        if (checkNulls()) {
            mascota.put("name", name.getText().toString());
            mascota.put("gender", gender.getText().toString());
            mascota.put("specie", specie.getText().toString());
            mascota.put("race", race.getText().toString());
            mascota.put("comment", comment.getText().toString());
            mascota.put("photo", Arrays.asList());
            mascota.put("owner", userID);


            db.collection("pets").add(mascota).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(final DocumentReference documentReference) {

                    //ojo, ahora hay que guardar las fotos en su sitio y ponerlas en firebase RECOGER LINK y añadir a lugar correspondiente
                    final DocumentReference docRAux = documentReference;
                    // do something with result.
                    Log.d("PRUEBA004", "Antes de entrar en el for");
                    for (int i = 0; i < uriImages.size(); i++) {
                        Log.d("PRUEBA005", "Después de entrar en el for");
                        final int j = i;
                        final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("pets/" + documentReference.getId() + "_" + i);
                        Uri file = uriImages.get(i);
                        Log.d("PRUEBA006", "Cojo la urii");

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
                                    Log.d("PRUEBA002", "He entrado");
                                    Log.d("PRUEBA007", "pets/" + documentReference.getId() + "_" + j);
                                    urlImages.add(task.getResult().toString());
                                    Log.d("Tamaño url", String.valueOf(urlImages.size()));
                                    docRAux.update("photo", urlImages);
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }

                        });
                    }

                    Log.d("mascotaRefenrece:", documentReference.getId());

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
                                if (arrayReference == null) arrayReference = new ArrayList<>();
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
                            Toast.makeText(getApplicationContext(), "Mascota Añadida",
                                    Toast.LENGTH_LONG).show();
                            startMap();
                        }
                    });


                }

            });
        }



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
                    }
                    break;
                }
        }
    }

    private boolean checkNulls() {
        String check;
        boolean result = true;
        check = name.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingName));
            result = false;
        }
        else resetHeading(R.id.headingName);
        check = gender.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingGender));
            result = false;
        }
        else resetHeading(R.id.headingGender);
        check = specie.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingSpecie));
            result = false;
        }
        else resetHeading(R.id.headingSpecie);
        check = race.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingRace));
            result = false;
        }
        else resetHeading(R.id.headingRace);
        check = comment.getText().toString();
        if (check == null || check.equals("")) {
            printErrorNull(findViewById(R.id.headingComment));
            result = false;
        }
        else resetHeading(R.id.headingComment);
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

    private void startMap() {
       Intent intent = new Intent (getApplicationContext(), MapActivity.class);
       startActivityForResult(intent, 0);
    }
}
