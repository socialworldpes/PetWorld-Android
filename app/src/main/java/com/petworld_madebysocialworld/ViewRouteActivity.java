package com.petworld_madebysocialworld;

import Models.User;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRouteActivity extends AppCompatActivity {

    // route info
    String id;
    String name, userID, creator;
    String description;
    String placeName;

    //info view
    EditText nameInput;
    EditText descriptionInput;
    EditText locationNameInput;
    RatingBar ratingBar;
    private int numVotes;
    HashMap<String, Long> puntuation;
    private int puntationFinal;
    private ArrayList<String> imageUrls;
    Button deleteButton;
    Button editButton;
    Button valorarButton;

    //map
    private GoogleMap map;
    private List<GeoPoint> path;
    private GeoPoint placeLocation;
    Polyline pathPolyline;

    //boolean
    private boolean valorar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_route);
        initLayout();
        initEvents();
        readRouteInfo();
        setupToolbar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        // So that the view is refreshed when editting the route!
        readRouteInfo();
    }

    private void initEvents() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRoute();
            }
        });
        valorarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valorar = true;
                editRoute();
            }
        });
    }

    private void editRoute() {
        Intent intent = new Intent (getApplicationContext(), EditRouteActivity.class);
        intent.putExtra("id", id);
        if (valorar) intent.putExtra("valorar",  true);
        else intent.putExtra("valorar",  false);
        startActivityForResult(intent, 0);
    }

    public void deleteRoute() {
        id = getIntent().getStringExtra("id");

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        ArrayList<DocumentReference> alRoutesRef = (ArrayList<DocumentReference>) document.get("routes");
                        for (DocumentReference dr : alRoutesRef) {

                            if (dr.getPath().equals("routes/" + id)) {

                                //borra en routes/
                                dr.delete();
                                //borra referencia en users/routes
                                document.getReference().update("routes", FieldValue.arrayRemove(dr));

                            }
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), "Ruta Borrada",
                        Toast.LENGTH_LONG).show();
                startMap();

            }

        });
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("View Ruta");
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
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);;
        editButton = findViewById(R.id.editButton);
        editButton.setVisibility(View.INVISIBLE);;
        valorarButton = findViewById(R.id.valorarButton);
        ratingBar = findViewById(R.id.ratingBar);
    }




    private void readRouteInfo() {
        id = getIntent().getStringExtra("id");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("routes").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();

                    creator = "" + task.getResult().get("creator");
                    if (!creator.equals(userID)){
                        deleteButton.setVisibility(View.GONE);
                        editButton.setVisibility(View.GONE);
                        valorarButton.setVisibility(View.VISIBLE);
                    } else {
                        deleteButton.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                        valorarButton.setVisibility(View.GONE);
                    }
                    name = "" + task.getResult().get("name");
                    description = "" + task.getResult().get("description");
                    placeName = "" + task.getResult().get("placeName");
                    name = "" + task.getResult().get("name");
                    path = (List<GeoPoint>) task.getResult().get("path");
                    placeLocation = path.get(0);
                    imageUrls = (ArrayList<String>)task.getResult().get("images");

                    //check para las versiones de rutas antiguas
                    if (task.getResult().get("puntuation") == null) ratingBar.setRating(0);
                    else {
                        puntuation = (HashMap<String, Long>) task.getResult().get("puntuation");

                        //calculate puntuacion
                        puntationFinal = calculatePoints();

                        //set puntuacion
                        ratingBar.setRating(puntationFinal);
                    }



                    nameInput.setText(name);
                    descriptionInput.setText(description);
                    locationNameInput.setText(placeName);

                    //images
                    ViewPager viewPager = findViewById(R.id.viewPager);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), imageUrls);
                    viewPager.setAdapter(adapter);

                    //route on map
                    setUpMap();
                }
            }
        });
    }

    private int calculatePoints() {
        int resultado = 0;


        for(Map.Entry<String, Long> entry : puntuation.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();

            resultado += value;
        }

        return resultado/puntuation.size();
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

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Borrar")
                .setMessage("¿Borrar ruta?")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code

                        deleteRoute();
                        startMap();
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
