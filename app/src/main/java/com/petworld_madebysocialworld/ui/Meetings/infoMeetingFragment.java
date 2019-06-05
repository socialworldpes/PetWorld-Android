package com.petworld_madebysocialworld.ui.Meetings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.petworld_madebysocialworld.R;
import com.petworld_madebysocialworld.ViewPagerAdapter;
import com.petworld_madebysocialworld.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Date;

@SuppressLint("ValidFragment")
public class infoMeetingFragment extends Fragment {

    private String creator;
    private ArrayList<String> imageUrls;
    private Context context;
    private String collection;
    private String id;
    private LatLng location;
    private String description;
    private String name;
    private String placeName;
    private String visibility;
    private GoogleMap mMap;
    private ArrayList<DocumentReference> participants;
    private View view;
    private FragmentActivity myContext;
    private Timestamp time;
    private String start;

    // Date Formatter & Hour Formatter
    private java.text.DateFormat df;
    private java.text.DateFormat hf;

    public infoMeetingFragment (Context context, String collection, String id){
        this.context = context;
        this.collection = collection;
        this.id = id;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_view_meeting, container, false);

        df = new android.text.format.DateFormat().getMediumDateFormat(view.getContext());
        hf = new android.text.format.DateFormat().getTimeFormat(view.getContext());

        create();

        return view;
    }

    private void create() {
        //va a petar
        //info: https://stackoverflow.com/questions/20237531/how-can-i-access-getsupportfragmentmanager-in-a-fragment

        //OJO, hay que pasar la id del meeting

        FirebaseFirestore.getInstance().collection(collection).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                imageUrls = (ArrayList<String>)documentSnapshot.get("images");
                creator = (String)documentSnapshot.get("creator");
                description = (String)documentSnapshot.get("description");
                name = (String)documentSnapshot.get("name");
                GeoPoint aux = ((GeoPoint)documentSnapshot.get("placeLocation"));
                location = new LatLng(aux.getLatitude(), aux.getLongitude());
                placeName = (String)documentSnapshot.get("placeName");
                com.google.firebase.Timestamp time = (com.google.firebase.Timestamp)documentSnapshot.get("start");
                visibility = (String)documentSnapshot.get("visibility");
                participants = (ArrayList<DocumentReference>) documentSnapshot.get("participants");

                Log.d("creator", creator);

                //mapa
                LatLng aux2 = new LatLng(aux.getLatitude(), aux.getLongitude());
                setUpMap(aux2);

                ViewPager viewPager = view.findViewById(R.id.viewPager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(context, imageUrls);
                viewPager.setAdapter(adapter);

                ((TextView)view.findViewById(R.id.Titulo)).setText(name);
                ((TextView)view.findViewById(R.id.Descripcion)).setText(description);
                ((TextView)view.findViewById(R.id.Lugar)).setText(placeName);
                Date date = time.toDate();

                start = df.format(date) + " " + hf.format(date);

                ((TextView)view.findViewById(R.id.Fecha)).setText(start);

            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }


    private void setUpMap(LatLng location) {
        final LatLng auxLocation = location;
        Log.d("MAPAAA", "BIEN!!");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapViewMeeting);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setAllGesturesEnabled(false);
                CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(auxLocation, (float) 15);
                mMap.moveCamera(cameraupdate);
                mMap.addMarker(new MarkerOptions()
                        .position(auxLocation)
                );
            }
        });
        //info: https://stackoverflow.com/questions/20237531/how-can-i-access-getsupportfragmentmanager-in-a-fragment

    }
}