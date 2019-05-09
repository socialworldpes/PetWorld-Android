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
import android.widget.Toast;
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.LatLng;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter;
import com.sangcomz.fishbun.define.Define;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateRouteActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;

    //booleans
    private boolean imagesCanContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);
        initFireBase();
        initLayout();
        initVariables();
        initListeners();
    }



    private void initLayout() {
    }

    private void initVariables() {
        imagesCanContinue = false;
        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();
    }

    private void initListeners() {
    }

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void loadImage(){;
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
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

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void createRoute() {
        String userID = User.getInstance().getAccount().getId();
        HashMap<String, Object> route =  new HashMap<String, Object>();
        List<LatLng> path =  new ArrayList<LatLng>();
        route.put("creator", userID);
        route.put("description", descriptionInput.getText().toString());
        route.put("name", namaeInput.getText().toString());
        route.put("placeName", placeNameInput.getText().toString());
        route.put("images", path);
        // TODO: fill array
        route.put("path", path);
        route.put("placeLocation", path.get(0));

        //add route to fireBase
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
