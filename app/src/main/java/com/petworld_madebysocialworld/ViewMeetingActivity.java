package com.petworld_madebysocialworld;

import Models.User;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class ViewMeetingActivity extends AppCompatActivity {

    private String id;
    private ArrayList<String> imageUrls;
    private String creator;
    private String description;
    private String name;
    private LatLng location;
    private String placeName;
    private String start;
    private String visibility;
    private Context context;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        location = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meeting);
        context = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //OJO, hay que pasar la id del meeting
        id = getIntent().getStringExtra("id");

        FirebaseFirestore.getInstance().collection("meetings").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                imageUrls = (ArrayList<String>)documentSnapshot.get("images");
                creator = (String)documentSnapshot.get("creator");
                description = (String)documentSnapshot.get("description");
                name = (String)documentSnapshot.get("name");
                GeoPoint aux = ((GeoPoint)documentSnapshot.get("placeLocation"));
                location = new LatLng(aux.getLatitude(), aux.getLongitude());
                placeName = (String)documentSnapshot.get("placeName");
                start = ((Timestamp)documentSnapshot.get("start")).toString();
                visibility = (String)documentSnapshot.get("visibility");

                //mapa
                setUpMap();

                ViewPager viewPager = findViewById(R.id.viewPager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(context, imageUrls);
                viewPager.setAdapter(adapter);

                ((TextView)findViewById(R.id.Titulo)).setText(name);
                ((TextView)findViewById(R.id.Descripcion)).setText(description);
                ((TextView)findViewById(R.id.Lugar)).setText(placeName);
                ((TextView)findViewById(R.id.Fecha)).setText(start);

                //TODO GET NAME from user
                /*FirebaseFirestore.getInstance().collection("users").document(creator).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        //((TextView)findViewById(R.id.Fecha)).setText(documentSnapshot.get("name"));
                    }
                }*/

            }
        });

    }

    private void setUpMap() {
        Log.d("MAPAAA", "BIEN!!");
        while (location == null);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewMeeting)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setAllGesturesEnabled(false);
                CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(location, (float) 18);
                mMap.moveCamera(cameraupdate);
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                );
            }
        });
    }

    private void inviteToMeeting () {
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //documentSnapshot.get("frieds")
            }
        });
    }
}
