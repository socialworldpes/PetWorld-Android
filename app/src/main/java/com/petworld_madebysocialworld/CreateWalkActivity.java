package com.petworld_madebysocialworld;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.*;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class CreateWalkActivity extends AppCompatActivity {
    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;

    // Layout
    private EditText descriptionInput;
    private EditText nameInput;
    private Button dateInput;
    private Button hourInput;
    private Button btnAddWalk;
    private Button btnUploadImage;
    private Button routeInput;

    private String userID;
    private Route routeToShow;

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

    // picked Route data
    private String pickedRouteId;
    private String pickedRouteName;
    private String pickedRouteDescription;
    private String pickedRouteImageURL;
    private String pickedRouteLocationName;
    private GeoPoint pickedRouteLocationPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_walk);

        setupToolbar();
        initVariables();
        initLayout();
        initListeners();
        initPickers();
        codeThatWasInsideOnCreate();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Crear Paseo");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }

    private void initVariables() {
        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();
        RoutesRepository.resetRepository();
        List<Route> repo = RoutesRepository.getInstance().getRoutes();

        // init formatter
        df = new android.text.format.DateFormat().getMediumDateFormat(getApplicationContext());
        hf = new android.text.format.DateFormat().getTimeFormat(getApplicationContext());
    }

    private void initLayout() {
        descriptionInput = findViewById(R.id.descriptionInput);
        nameInput = findViewById(R.id.nameInput);
        dateInput = findViewById(R.id.dateInput);
        dateInput.setText(df.format(pickedDate));
        hourInput = findViewById(R.id.hourInput);
        hourInput.setText(hf.format(pickedDate));
        routeInput = findViewById(R.id.routeInput);

        btnAddWalk = findViewById(R.id.createButton);
        btnUploadImage = findViewById(R.id.uploadImagesButton);
    }

    private void initListeners() {
        btnAddWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { createWalk(); }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();

            }
        });
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
        routeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickRoute();

            }
        });
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

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void createWalk() {
        HashMap<String, Object> walk =  new HashMap<String, Object>();
        walk.put("creator",     mAuth.getCurrentUser().getUid());
        walk.put("route",       db.collection("routes").document(pickedRouteId));
        walk.put("description", descriptionInput.getText().toString());
        walk.put("name",        nameInput.getText().toString());
        walk.put("images",      Arrays.asList());
        walk.put("start",       pickedDate);
        walk.put("placeLocation", pickedRouteLocationPlace);

        addWalkToFireBase(walk);
    }

    private void addWalkToFireBase(HashMap<String, Object> walk) {
        db.collection("walks").add(walk).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {

                //ojo, ahora hay que guardar las fotos en su sitio y ponerlas en firebase RECOGER LINK y añadir a lugar correspondiente
                final DocumentReference docRAux = documentReference;
                for (int i = 0; i < uriImages.size(); i++) {
                    final StorageReference imagesRef = storage.getReference().child("walks/" + documentReference.getId() + "_" + i);
                    Uri file = uriImages.get(i);

                    UploadTask uploadTask = imagesRef.putFile(file);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) { throw task.getException(); }
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

                String userID = mAuth.getCurrentUser().getUid();
                addWalkRefToUser(documentReference, userID);
            }

        });
    }

    private void addWalkRefToUser(final DocumentReference documentReference, String userID) {
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userID = mAuth.getCurrentUser().getUid();
                    DocumentSnapshot result = task.getResult();
                    ArrayList<DocumentReference> arrayReference = (ArrayList<DocumentReference>) result.get("walks");
                    if (arrayReference == null) arrayReference = new ArrayList<>();
                    arrayReference.add(documentReference);

                    //add walk to users(userID)
                    db.collection("users").document(userID)
                            .update("walks", arrayReference)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) { Log.d("walk", "DocumentSnapshot successfully written!"); }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) { Log.w("walk", "Error writing document", e); }
                            });
                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
                //Toast.makeText(getApplicationContext(), "Paseo creada", Toast.LENGTH_LONG).show();
                startMap();
            }
        });
    }


    private void refreshImageView() {
        for (Uri uri: uriImages)
            urlImages.add(uri.toString());

        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), urlImages);
        viewPager.setAdapter(adapter);
    }

    private void loadImage(){
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }

    public void pickDate() {
        datePickerDialog.updateDate(pickedYear, pickedMonth, pickedDay);
        datePickerDialog.show();
    }

    public void pickTime() {
        timePickerDialog.updateTime(pickedHour, pickedMinute);
        timePickerDialog.show();
    }

    private void pickRoute() {
        startActivityForResult(new Intent(CreateWalkActivity.this, RoutesActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                pickedRouteId           = data.getStringExtra("routeId");
                pickedRouteName         = data.getStringExtra("routeName");
                pickedRouteDescription  = data.getStringExtra("routeDescription");
                pickedRouteImageURL     = data.getStringExtra("routeImageURL");
                pickedRouteLocationName = data.getStringExtra("routeLocationName");
                Double lat = data.getDoubleExtra("routeLocationPlaceLat", 41.389);
                Double lng = data.getDoubleExtra("routeLocationPlaceLng", 2.088);
                Log.d("RouteLocationPlace", "lat: "+ lat + "     lng: " + lng);
                pickedRouteLocationPlace = new GeoPoint(lat, lng);

                routeInput.setText(pickedRouteName);
            }
        }
        if (requestCode == Define.ALBUM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                uriImages = data.getParcelableArrayListExtra(Define.INTENT_PATH);
                if (uriImages.size() > 0){
                    refreshImageView();
                }
            }
        }
    }





    protected void codeThatWasInsideOnCreate() {
/*
        //initNavigationDrawer();
        userID = mAuth.getCurrentUser().getUid();

        Button but = findViewById(R.id.elegirRuta);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateWalkActivity.this, RoutesActivity.class));
            }
        });

        Button selectDate = findViewById(R.id.elegirFecha);
        dateInput = findViewById(R.id.textDate);

        selectDate.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateWalkActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        dateInput.setText(day + "/" + (month+1) + "/" + year);
                                    }
                                }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        Button selectHour = findViewById(R.id.elegirHora);
        hourInput = findViewById(R.id.textHour);

        selectHour.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int min = calendar.get(Calendar.MINUTE);

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateWalkActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                hourInput.setText(hour + ":" + min);
                            }
                        }, hour, min, true);

                //timePickerDialog.getTimePicker().setMinDate(System.currentTimeMillis());
                timePickerDialog.show();
            }
        });


        Button createPaseo = findViewById(R.id.createWalk);
        createPaseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", "onClick: In");
                TextView dateToFB = findViewById(R.id.textDate);
                Map<String, Object> docData = new HashMap<>();
                docData.put("dateInput",dateToFB.getText().toString());
                docData.put("name",routeToShow.getName());
                docData.put("description",routeToShow.getDescription());
                docData.put("place",routeToShow.getPlace());


                Log.d("Debug", "onClick: docData");
                Task resTask = db.collection("walks").add(docData);
                Log.d("Debug", "onClick: docData");
                resTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("Debug", "onClick: If");
                            final DocumentReference res = task.getResult();
                            Log.d("Debug", "onClick: Result");
                            //path = res.getPath();
                            Log.d("Debug", "onClick: Path");
                            db.collection("users").document(userID).get().addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                          @Override
                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                              if (task.isSuccessful()) {
                                                  DocumentSnapshot toRead = task.getResult();
                                                  Map<String,Object> data= Objects.requireNonNull(toRead).getData();
                                                  ArrayList walks = (ArrayList) Objects.requireNonNull(data).get("walks");
                                                  walks.add(res);
                                                  Map<String, Object> pathMap = new HashMap<>();
                                                  pathMap.put("walks", walks);
                                                  db.collection("users").document(userID).update(pathMap);
                                                  Log.d("Debug", "onClick: " +data.toString());
                                              }
                                          }
                                    });
                        }
                    }
                });
                startActivity(new Intent(CreateWalkActivity.this, MapActivity.class));
            }
        });


        View routeSelected = findViewById(R.id.routeSelected);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("itemId");
            routeToShow = RoutesRepository.getInstance().getRoute(id);

            ImageView image = (ImageView) routeSelected.findViewById(R.id.route_image);
            TextView name = (TextView) routeSelected.findViewById(R.id.route_name);
            TextView place = (TextView) routeSelected.findViewById(R.id.route_place);
            TextView description = (TextView) routeSelected.findViewById(R.id.route_description);
            TextView idTextView = (TextView) routeSelected.findViewById(R.id.route_id);

            Glide.with(this).load(routeToShow.getImage()).into(image);
            name.setText(routeToShow.getName());
            place.setText(routeToShow.getPlace());
            description.setText(routeToShow.getDescription());
            idTextView.setText(routeToShow.getId());
        }
*/
    }
}
