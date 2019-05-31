package com.petworld_madebysocialworld;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter;
import com.sangcomz.fishbun.define.Define;

import java.util.*;

public class EditWalkActivity extends AppCompatActivity {


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

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;

    // Date
    private Calendar c = Calendar.getInstance();
    private int pickedMonth  = c.get(Calendar.MONTH);
    private int pickedDay    = c.get(Calendar.DAY_OF_MONTH);
    private int pickedYear   = c.get(Calendar.YEAR);
    private int pickedHour   = c.get(Calendar.HOUR_OF_DAY);
    private int pickedMinute = c.get(Calendar.MINUTE);
    private Date pickedDate  = new GregorianCalendar(pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute).getTime();
    // Date Formatter & Hour Formatter
    private java.text.DateFormat df;
    private java.text.DateFormat hf;

    //layout
    EditText nameWalkEditText;
    EditText descriptionWalkEditText;
    EditText nameRouteEditText;

    //buttons
    Button editButton;
    private Button btnUploadImage;
    private Button dateInput;
    private Button hourInput;

    // Pickers
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_walk);
        initFireBase();
        initVariables();
        initLayout();
        initEvents();
        initPickers();
        setupToolbar();
        readWalkInfo();
    }

    private void initPickers() {
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                pickedYear = year; pickedMonth = month; pickedDay = dayOfMonth;
                pickedDate = new GregorianCalendar(pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute).getTime();
                String formattedDate = df.format(pickedDate);
                dateInput.setText(formattedDate);
            }
        }, pickedYear, pickedMonth, pickedDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                pickedHour = hourOfDay; pickedMinute = minute;
                pickedDate = new GregorianCalendar(pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute).getTime();
                String formattedTime = hf.format(pickedDate);
                hourInput.setText(formattedTime);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, pickedHour, pickedMinute, false);
    }

    private void initVariables() {
        idWalk =  getIntent().getExtras().getString("id");
        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();

        // init formatter
        df = new android.text.format.DateFormat().getMediumDateFormat(getApplicationContext());
        hf = new android.text.format.DateFormat().getTimeFormat(getApplicationContext());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Paseo");
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
                    urlImages = (ArrayList<String>)result.get("images");
                    routeDocumentReference = (DocumentReference) result.get("route");
                    com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) result.get("start");
                    pickedDate = time.toDate();


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
    }

    private void setLayoutText() {
        nameWalkEditText.setText(name);
        descriptionWalkEditText.setText(description);
        dateInput.setText(df.format(pickedDate));
        hourInput.setText(hf.format(pickedDate));
    }

    private void setImages() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), urlImages);
        viewPager.setAdapter(adapter);
    }

    private void initEvents() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWalk();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });

        //data
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();

            }
        });
        hourInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();

            }
        });
    }

    private void pickTime() {
        timePickerDialog.updateTime(pickedHour, pickedMinute);
        timePickerDialog.show();
    }

    private void pickDate() {
        datePickerDialog.updateDate(pickedYear, pickedMonth, pickedDay);
        datePickerDialog.show();
    }

    private void loadImage() {
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }


    private void initLayout() {
        nameWalkEditText = findViewById(R.id.nameInput);
        descriptionWalkEditText = findViewById(R.id.descriptionInput);
        editButton = findViewById(R.id.editButton);
        btnUploadImage = findViewById(R.id.uploadImagesButton);

        //data
        dateInput = findViewById(R.id.dateInput);
        hourInput = findViewById(R.id.hourInput);
    }

    private void initFireBase() {
        db = FirebaseFirestore.getInstance();
    }

    private void editWalk() {

        //get info
        HashMap<String, Object> walk = new HashMap<String, Object>();
        walk.put("description", descriptionWalkEditText.getText().toString());
        walk.put("name", nameWalkEditText.getText().toString());
        walk.put("start",pickedDate);


        final DocumentReference walkRef = db.collection("walks").document(idWalk);
        walkRef.update(walk);

        //update image


        final DocumentReference docRAux = walkRef;
        // do something with result.
        for (int i = 0; i < uriImages.size(); i++) {
            final int j = i;
            final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("walks/" + walkRef.getId() + "_" + i);
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
                        docRAux.update("images", urlImages);
                    } else {
                        // Handle failures
                        // ...
                    }
                }

            });
        }

        Toast.makeText(getApplicationContext(), "Paseo Editado",
                Toast.LENGTH_LONG).show();
        startMap();

    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Define.ALBUM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                uriImages = data.getParcelableArrayListExtra(Define.INTENT_PATH);
                if (uriImages.size() > 0){
                    refreshImageView();
                }
            }
        }


}

    private void refreshImageView() {
        for (Uri uri: uriImages)
            urlImages.add(uri.toString());

        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), urlImages);
        viewPager.setAdapter(adapter);
    }
    }
