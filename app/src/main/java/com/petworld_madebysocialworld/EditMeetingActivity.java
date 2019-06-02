package com.petworld_madebysocialworld;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class EditMeetingActivity extends AppCompatActivity {

    //firestore
    private FirebaseFirestore db;

    // meeting info
    String id;
    String name;
    String description;
    String placeName;
    HashMap<String, Object> meeting =  new HashMap<String, Object>();


    //info view
    EditText nameInput;
    EditText descriptionInput;
    EditText locationNameInput;
    Button saveButton;
    Button loadImageButton;

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> imageUrls;


    //booleans
    private boolean imagesCanContinue;
    private boolean imagesUpdated = false;

    //map
    private GoogleMap map;
    private GeoPoint placeLocation;

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

    // Pickers
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private Button dateInput;
    private Button hourInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meeting);

        initFireBase();
        initVariables();
        initLayout();
        initEvents();
        initPickers();
        setupToolbar();
        readMeetingInfo();
    }

    private void initVariables() {
        // init formatter
        df = new android.text.format.DateFormat().getMediumDateFormat(getApplicationContext());
        hf = new android.text.format.DateFormat().getTimeFormat(getApplicationContext());
    }

    private void initFireBase() {
        db = FirebaseFirestore.getInstance();
    }

    private void initEvents() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeeting();
            }
        });
        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
                refreshImageView();
            }
        });

        //date
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { pickDate(); }
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

    private void loadImage(){;
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }

    private void refreshImageView() {
        imageUrls =  new ArrayList<>();
        for (Uri uri: uriImages) imageUrls.add(uri.toString());

        ViewPager viewPager= findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), imageUrls);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        Log.d("onActivityresult", "in ");
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    uriImages = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    if (uriImages.size() > 0){
                        imagesCanContinue = true;
                        Log.d("onActivityresult", "uri size: " + uriImages.size());
                        refreshImageView();

                    }
                    break;
                }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // TODO: Use meeting name
        toolbar.setTitle("Edit Meeting");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }
    private void initLayout() {
        descriptionInput = findViewById(R.id.descriptionInput);
        nameInput = findViewById(R.id.nameInput);
        locationNameInput = findViewById(R.id.locationNameInput);
        saveButton = findViewById(R.id.saveButton);
        loadImageButton = findViewById(R.id.uploadImagesButton);
        imagesCanContinue = false;

        //date
        dateInput = findViewById(R.id.dateInput);
        hourInput = findViewById(R.id.hourInput);
    }

    private void readMeetingInfo() {
        id = getIntent().getStringExtra("id");

        FirebaseFirestore.getInstance().collection("meetings").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();

                    name = "" + task.getResult().get("name");
                    description = "" + task.getResult().get("description");
                    placeName = "" + task.getResult().get("placeName");
                    name = "" + task.getResult().get("name");
                    placeLocation = (GeoPoint) task.getResult().get("placeLocation");
                    com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) task.getResult().get("start");
                    pickedDate = time.toDate();

                    imageUrls = (ArrayList<String>)result.get("images");
                    //fill uri images
                    loadUriImages();

                    nameInput.setText(name);
                    descriptionInput.setText(description);
                    locationNameInput.setText(placeName);
                    dateInput.setText(df.format(pickedDate));
                    hourInput.setText(hf.format(pickedDate));

                    //images
                    ViewPager viewPager = findViewById(R.id.viewPager);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), imageUrls);
                    viewPager.setAdapter(adapter);

                    //meeting on map
                    setUpMap();

                    //save info meeting
                    saveInfoMeeting();

                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void loadUriImages() {
        uriImages =  new ArrayList<>();
        for (String s: imageUrls)
            uriImages.add(Uri.parse(s));
    }

    private void saveInfoMeeting() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Read fields
        meeting.put("creator", userID);
        meeting.put("description", descriptionInput.getText().toString());
        meeting.put("name", nameInput.getText().toString());
        meeting.put("placeName", locationNameInput.getText().toString());
        meeting.put("images", imageUrls);
        meeting.put("placeLocation", placeLocation);
        meeting.put("start",pickedDate);

    }

    //parse the LatLng to GeoPoint of the List
    private List<GeoPoint> parsePath(List<LatLng> latLngList) {
        List<GeoPoint> geoPointList = new ArrayList<GeoPoint>();

        for (LatLng ll: latLngList) {
            geoPointList.add(new GeoPoint(ll.latitude, ll.longitude));
        }

        return geoPointList;
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewMeeting)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                map = googleMap;
                LatLng startPoint = new LatLng(placeLocation.getLatitude(), placeLocation.getLongitude());
                CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(startPoint, (float) 16);
                map.moveCamera(cameraupdate);
                //put point on map
                map.addMarker(new MarkerOptions()
                        .position(startPoint)
                        //.draggable(true)
                        .anchor((float) 0.5, (float) 0.5)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue))
                );
            }
        });
    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void saveMeeting() {
        saveInfoMeeting();
        updateMeeting(meeting);
        startMap();
    }

    private void updateMeeting(HashMap<String, Object> meeting) {


        final DocumentReference meetingRef = db.collection("meetings").document(id);
        meetingRef.update(meeting);

        //update image

        //ojo, ahora hay que guardar las fotos en su sitio y ponerlas en firebase RECOGER LINK y añadir a lugar correspondiente
        final DocumentReference docRAux = meetingRef;
        // do something with result.
        Log.d("PRUEBA004", "Antes de entrar en el for");
        for (int i = 0; i < uriImages.size(); i++) {
            Log.d("PRUEBA005", "Después de entrar en el for");
            final int j = i;
            final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("meetings/" + meetingRef.getId() + "_" + i);
            Uri file = uriImages.get(i);
            Log.d("PRUEBA006", "Cojo la urii: " + file.toString());

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
                        Log.d("PRUEBA007", "meetings/" + meetingRef.getId() + "_" + j);
                        imageUrls.add(task.getResult().toString());
                        Log.d("Tamaño url", String.valueOf(imageUrls.size()));
                        docRAux.update("images", imageUrls);
                    } else {
                        // Handle failures
                        // ...
                    }
                }

            });
        }

        Toast.makeText(getApplicationContext(), "Meeting Editado", Toast.LENGTH_LONG).show();
        startMap();
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

}
