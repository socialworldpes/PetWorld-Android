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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter;
import com.sangcomz.fishbun.define.Define;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EditPetActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //Layout
    private EditText name;
    private Spinner gender;
    private Spinner specie;
    private EditText race;
    private EditText comment;
    private TextView nameUpdate;
    private TextView genderUpdate;
    private TextView raceUpdate;
    private TextView specieUpdate;
    private TextView commentUpdate;
    private Button btnUploadImage;
    private String petPath;
    private Button btnUpdatePet;

    //images
    public static final int PICK_IMAGE = 1;
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;

    //booleans
    private boolean imagesCanContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        setupToolbar();
        initFireBase();
        initItems();
        initListeners();
        initIntent();
        initVariables();
        initLayout();
    }

    private void initVariables() {
        imagesCanContinue = false;
        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();
    }

    private void initListeners() {
        btnUpdatePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePet();
            }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
    }

    private void loadImage() {
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }


    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initItems() {
        name = findViewById(R.id.namePetInput);
        race = findViewById(R.id.racePetInput);
        comment = findViewById(R.id.commentPetInput);
        btnUploadImage = findViewById(R.id.buttonLoadImage);
        btnUpdatePet = findViewById(R.id.editButton);

        //init specie dropdown
        String[] arraySpecie = new String[] {
                "Perro", "Gato", "Hamster", "Conejo", "Ave", "Pez", "Reptil", "Invertebrado", "Otros"
        };
        specie = findViewById(R.id.selectSpecie);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpecie);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specie.setAdapter(adapter);

        //init gender dropdown
        String[] arrayGender =  new String[] {
                "Macho", "Hembra"
        };
        gender = findViewById(R.id.selectGender);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayGender);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Mascota");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }


    private void initLayout() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference docRef = db.document(petPath);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    name.setText("" + task.getResult().get("name"));
                    gender.setSelection(((ArrayAdapter)gender.getAdapter()).getPosition("" + task.getResult().get("gender")));
                    specie.setSelection(((ArrayAdapter)specie.getAdapter()).getPosition("" + task.getResult().get("specie")));
                    race.setText("" + task.getResult().get("race"));
                    comment.setText("" + task.getResult().get("comment"));
                    urlImages = (ArrayList<String>) task.getResult().get("photo");
                    refreshImageView();
                    uriImages = uriParse(urlImages);



                }
            }
        });

    }

    private ArrayList<Uri> uriParse(ArrayList<String> urlImages) {

        ArrayList<Uri> result =  new ArrayList<>();
        for (String s: urlImages)
            result.add(Uri.parse(s));
        return result;
    }

    private void initIntent() {
        petPath = getIntent().getStringExtra("docPetRef");
    }
    private void updatePet() {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, Object> mascota =  new HashMap<String, Object>();


        //fill pet info
        mascota.put("name", name.getText().toString());
        mascota.put("specie", specie.getSelectedItem().toString());
        mascota.put("gender", gender.getSelectedItem().toString());
        mascota.put("race", race.getText().toString());
        mascota.put("comment",comment.getText().toString());
        mascota.put("photo", Arrays.asList());
        mascota.put("owner", userID);

        final DocumentReference petRef = db.document(petPath);
        final DocumentReference docRAux = petRef;
        // do something with result.
        for (int i = 0; i < uriImages.size(); i++) {
            final int j = i;
            final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("pets/" + petRef.getId() + "_" + i);
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
        petRef.update(mascota);
        Toast.makeText(getApplicationContext(), "Mascota Editada",
                Toast.LENGTH_LONG).show();
        startMap();

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
    private void refreshImageView() {

        for (Uri uri: uriImages)
            urlImages.add(uri.toString());

        ViewPager viewPager= findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), urlImages);
        viewPager.setAdapter(adapter);


    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("Editar Mascota");
        setSupportActionBar(toolBar);
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
