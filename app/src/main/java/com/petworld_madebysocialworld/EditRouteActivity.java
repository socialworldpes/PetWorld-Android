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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditRouteActivity extends AppCompatActivity {

    //firestore
    private FirebaseFirestore db;

    // route info
    String id;
    String name;
    String description;
    String placeName;
    HashMap<String, Object> route =  new HashMap<String, Object>();


    //info view
    EditText nameInput;
    EditText descriptionInput;
    EditText locationNameInput;
    Button saveButton;
    RatingBar ratingBar;
    HashMap<String, Long> puntuation;
    private Long puntationUser;
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
    private List<LatLng> pathLl = new ArrayList<LatLng>();
    private List<GeoPoint> path;
    private GeoPoint placeLocation;
    Polyline pathPolyline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_route);

        initFireBase();
        initLayout();
        initEvents();
        setupToolbar();
        readRouteInfo();
    }

    private void initFireBase() {
        db = FirebaseFirestore.getInstance();
    }

    private void initEvents() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoute();
            }
        });
        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
                refreshImageView();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                puntuation.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), (long) rating);
            }
        });
    }

    private void loadImage(){;
        FishBun.with(this).setImageAdapter(new PicassoAdapter()).setMaxCount(3).startAlbum();
    }

    private void refreshImageView() {

        imageUrls =  new ArrayList<>();
        for (Uri uri: uriImages)
            imageUrls.add(uri.toString());

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
        // TODO: Use route name
        toolbar.setTitle("Edit Ruta");
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
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void readRouteInfo() {
        id = getIntent().getStringExtra("id");

        FirebaseFirestore.getInstance().collection("routes").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();

                    name = "" + task.getResult().get("name");
                    description = "" + task.getResult().get("description");
                    placeName = "" + task.getResult().get("placeName");
                    name = "" + task.getResult().get("name");
                    path = (List<GeoPoint>) task.getResult().get("path");
                    placeLocation = path.get(0);
                    imageUrls = (ArrayList<String>)result.get("images");
                    //fill uri images
                    loadUriImages();

                    if (task.getResult().get("puntuation") == null) {
                        HashMap<String, Long> puntuationAux =  new HashMap<>();
                        puntuationAux.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), (long) 0);
                        puntuation = puntuationAux;
                    }
                    else {
                        puntuation = (HashMap<String, Long>) task.getResult().get("puntuation");
                    }

                    //calculate puntuacion
                    puntationUser = calculatePointsUser();

                    //set puntuacion
                    ratingBar.setRating(puntationUser);

                    nameInput.setText(name);
                    descriptionInput.setText(description);
                    locationNameInput.setText(placeName);

                    //images
                    ViewPager viewPager = findViewById(R.id.viewPager);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), imageUrls);
                    viewPager.setAdapter(adapter);

                    //route on map
                    setUpMap();

                    //save info route
                    saveInfoRoute();

                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private Long calculatePointsUser() {
        Long resultado =  new Long(0);

        for(Map.Entry<String, Long> entry : puntuation.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();

            if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                resultado = value;

        }

        return resultado;
    }

    private void loadUriImages() {
        uriImages =  new ArrayList<>();
        for (String s: imageUrls)
            uriImages.add(Uri.parse(s));
    }

    private void saveInfoRoute() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Read fields
        route.put("creator", userID);
        route.put("description", descriptionInput.getText().toString());
        route.put("name", nameInput.getText().toString());
        route.put("placeName", locationNameInput.getText().toString());
        route.put("images", imageUrls);
        route.put("path", path);
        route.put("placeLocation", path.get(0));
        route.put("puntuation", puntuation);

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
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewRoute)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                map = googleMap;
                LatLng startPoint = new LatLng(path.get(0).getLatitude(), path.get(0).getLongitude());
                CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(startPoint, (float) 16);
                map.moveCamera(cameraupdate);
                //put points on map
                boolean firstPoint = true;
                for (GeoPoint point : path) {
                    int resource;
                    if (firstPoint) {
                        resource = R.drawable.marker_blue;
                        firstPoint = false;
                    }
                    else resource = R.drawable.marker_green;
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(point.getLatitude(), point.getLongitude()))
                            //.draggable(true)
                            .anchor((float) 0.5, (float) 0.5)
                            .icon(BitmapDescriptorFactory.fromResource(resource))
                    );
                }
                // map line
                List<LatLng> pathLl = toLatLng(path);
                pathPolyline = map.addPolyline(new PolylineOptions()
                        .add(pathLl.get(0))
                        .width(20)
                        .color(Color.parseColor("#2D9CDB")));
                refreshPolyLine(pathLl);


            }
        });
    }
    private List<LatLng> toLatLng(List<GeoPoint> path) {
        List<LatLng> result =  new ArrayList<>();
        for (GeoPoint point: path)
            result.add(new LatLng(point.getLatitude(), point.getLongitude()));
        return result;
    }
    private void refreshPolyLine(List<LatLng> path) {
        pathPolyline.setPoints(path);
    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void saveRoute() {
        saveInfoRoute();
        updateRoute(route);
        startMap();
    }

    private void updateRoute(HashMap<String, Object> route) {


        final DocumentReference routeRef = db.collection("routes").document(id);
        routeRef.update(route);

        //update image

        //ojo, ahora hay que guardar las fotos en su sitio y ponerlas en firebase RECOGER LINK y añadir a lugar correspondiente
        final DocumentReference docRAux = routeRef;
        // do something with result.
        Log.d("PRUEBA004", "Antes de entrar en el for");
        for (int i = 0; i < uriImages.size(); i++) {
            Log.d("PRUEBA005", "Después de entrar en el for");
            final int j = i;
            final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("routes/" + routeRef.getId() + "_" + i);
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
                        Log.d("PRUEBA007", "routes/" + routeRef.getId() + "_" + j);
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

        Toast.makeText(getApplicationContext(), "Route Editada",
                Toast.LENGTH_LONG).show();
        startMap();
    }



}
