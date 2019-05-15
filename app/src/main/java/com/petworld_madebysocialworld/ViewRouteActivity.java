package com.petworld_madebysocialworld;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class ViewRouteActivity extends AppCompatActivity {

    // route info
    //TODO: use a model
    String id;
    String name;
    String description;
    String placeName;
    //Object placeLocation;
    //Object path;

    //info view
    EditText nameInput;
    EditText descriptionInput;
    EditText locationNameInput;
    private ArrayList<String> imageUrls;

    //map
    private GoogleMap map;
    private List<GeoPoint> path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_route);
        initLayout();

        readRouteInfo();
        setupToolbar();


    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        // TODO: Use route name
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
                    //placeLocation = task.getResult().get("placeLocation");
                    //path = task.getResult().get("path");
                    imageUrls = (ArrayList<String>)result.get("images");

                    nameInput.setText(name);
                    descriptionInput.setText(description);
                    locationNameInput.setText(placeName);

                    //images
                    ViewPager viewPager = findViewById(R.id.viewPager);
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), imageUrls);
                    viewPager.setAdapter(adapter);
                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void setUpMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCreateRoute)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                map = googleMap;
                LatLng startPoint = new LatLng(path.get(0).getLatitude(), path.get(0).getLongitude());
                CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(startPoint, (float) 16);
                map.moveCamera(cameraupdate);

                for (GeoPoint point : path) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(point.getLatitude(), point.getLongitude()))
                            //.draggable(true)
                            .anchor((float) 0.5, (float) 0.5)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue))
                    );
                }


            }
        });
    }
}
