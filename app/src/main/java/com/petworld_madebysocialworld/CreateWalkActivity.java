package com.petworld_madebysocialworld;

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
import com.google.android.gms.tasks.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter;

import java.util.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CreateWalkActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;

    //booleans
    private boolean imagesCanContinue;

    // layout
    private EditText descriptionInput;
    private EditText nameInput;
    private Button dateInput;
    private Button hourInput;
    private Button btnAddWalk;
    private Button btnUploadImage;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_walk);

        setupToolbar();
        initFireBase();
        initLayout();
        initVariables();
        initListeners();
        initPickers();
        codeThatWasInsideOnCreate();
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

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                pickedHour = hourOfDay; pickedMinute = minute;
                pickedDate = new GregorianCalendar(pickedYear, pickedMonth, pickedDay, pickedHour, pickedMinute).getTime();
                String formattedDate = hf.format(pickedDate);
                hourInput.setText(formattedDate);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, pickedHour, pickedMinute, false);
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

    private void initLayout() {
        descriptionInput = findViewById(R.id.descriptionInput);
        nameInput = findViewById(R.id.nameInput);
        dateInput = findViewById(R.id.dateInput);
        hourInput = findViewById(R.id.hourInput);

        btnAddWalk = findViewById(R.id.createButton);
        btnUploadImage = findViewById(R.id.uploadImagesButton);
    }

    private void initVariables() {
        imagesCanContinue = false;
        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();

        // init formatter
        df = new android.text.format.DateFormat().getMediumDateFormat(getApplicationContext());
        hf = new android.text.format.DateFormat().getTimeFormat(getApplicationContext());
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
    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void createWalk() {
        HashMap<String, Object> walk =  new HashMap<String, Object>();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Read fields
        walk.put("creator", userID);
        walk.put("description", descriptionInput.getText().toString());
        walk.put("name", nameInput.getText().toString());
        walk.put("images", Arrays.asList());

        addWalkToFireBase(walk);
    }

    private void addWalkToFireBase(HashMap<String, Object> walk) {
        db.collection("walks").add(walk).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {

                //ojo, ahora hay que guardar las fotos en su sitio y ponerlas en firebase RECOGER LINK y añadir a lugar correspondiente
                final DocumentReference docRAux = documentReference;
                for (int i = 0; i < uriImages.size(); i++) {
                    final int j = i;
                    final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("walks/" + documentReference.getId() + "_" + i);
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
                                Log.d("PRUEBA007", "walks/" + documentReference.getId() + "_" + j);
                                urlImages.add(task.getResult().toString());
                                docRAux.update("images", urlImages);
                            } else {
                                // Handle failures
                                // ...
                            }
                        }

                    });
                }

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                addWalkRefToUser(documentReference, userID);
            }

        });
    }

    private void addWalkRefToUser(final DocumentReference documentReference, String userID) {
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void loadImage(){;
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }

    public void pickDate(View view) {
        datePickerDialog.updateDate(pickedYear, pickedMonth, pickedDay);
        datePickerDialog.show();
    }

    public void pickTime(View view) {
        timePickerDialog.updateTime(pickedHour, pickedMinute);
        timePickerDialog.show();
    }







    protected void codeThatWasInsideOnCreate() {
        /*
        //initNavigationDrawer();
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
