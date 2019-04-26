package com.petworld_madebysocialworld;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewMeetingActivity extends AppCompatActivity implements OnMapReadyCallback {

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
        mapFragment.getMapAsync(this);

        //OJO, hay que pasar la id del meeting
        id = getIntent().getParcelableExtra("id");

        FirebaseFirestore.getInstance().collection("meetings").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                imageUrls = (ArrayList<String>)documentSnapshot.get("images");
                creator = (String)documentSnapshot.get("creator");
                description = (String)documentSnapshot.get("description");
                name = (String)documentSnapshot.get("name");
                location = (LatLng)documentSnapshot.get("location");
                placeName = (String)documentSnapshot.get("placeName");
                start = (String)documentSnapshot.get("start");
                visibility = (String)documentSnapshot.get("visibility");

                ViewPager viewPager = findViewById(R.id.viewPager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(context, imageUrls);
                viewPager.setAdapter(adapter);

                ((TextView)findViewById(R.id.Titulo)).setText(name);
                ((TextView)findViewById(R.id.Descripcion)).setText(description);
                ((TextView)findViewById(R.id.Lugar)).setText(placeName);
                ((TextView)findViewById(R.id.Fecha)).setText(start);

                //mapa


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        while (location == null);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
