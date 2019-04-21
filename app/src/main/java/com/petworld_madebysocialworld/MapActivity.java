package com.petworld_madebysocialworld;

import Models.User;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Map;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener {

    private static final String TAG = MapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition; //prova

    private int times = 0;
    private ArrayList<Map<String, Object>> meetings = new ArrayList<Map<String, Object>>();

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    //user
    private User u;

    // RecyclerView for meetings and walks
    private RecyclerView recyclerView;
    private RecyclerView.Adapter meetingsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionsMenu fam;

    // Data beeing used
    Query locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        u = User.getInstance();
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        initNavigationDrawer();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        getLocationPermission();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // RecyclerView for meetings and walks
        layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        // TODO: we need the variable meetings to contain the meetings displayed in the map
        meetingsAdapter = new MeetingSmallAdapter(this, meetings);
        //recyclerView.setAdapter(meetingsAdapter);

        fam = (FloatingActionsMenu) findViewById(R.id.menu_fab);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mMap = googleMap;

        updateLocationUI();
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                fam.collapse();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng point) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
                vibe.vibrate(30);

                //newMeeting(point);
                fam.expand();
            }
        });

        mMap.setOnCameraMoveStartedListener(this);

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {

            if (mLocationPermissionGranted){
                //Toast.makeText(this, "Dins mLocation", Toast.LENGTH_SHORT).show();
                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                DEFAULT_ZOOM));
                            } else {
                                Toast.makeText(MapActivity.this, "NULL", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e){

        }
    }

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }

    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void newMeeting(View view){
        newMeeting();
    }

    public void newWalk(View view){
        newWalk();
    }

    public void newRoute(View view){
        newRoute();
    }

    public void newMeeting(){
        Intent intent = new Intent(MapActivity.this, CreateMeetingActivity.class);
        startActivity(intent);
    }

    public void newWalk(){
        Intent intent = new Intent(MapActivity.this, CreateWalkActivity.class);
        startActivity(intent);
    }

    public void newRoute(){
        Intent intent = new Intent(MapActivity.this, CreateRouteActivity.class);
        startActivity(intent);
    }
    public void newMeeting(LatLng location){
        Intent intent = new Intent(MapActivity.this, CreateMeetingActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }

    public void newWalk(LatLng location){
        Intent intent = new Intent(MapActivity.this, CreateWalkActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }

    public void newRoute(LatLng location){
        Intent intent = new Intent(MapActivity.this, CreateRouteActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }

    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }



        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            Log.i(TAG, "The user did not grant location permission.");
            getLocationPermission();
        }

    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
              //  .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {

            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void goToLogIn (View view){
       // Intent nextActivity = new Intent(this, MainActivity.class);
       // startActivity(nextActivity);
    }

    public void goToUserProfile (View view){
      //  Intent nextActivity = new Intent(this, UserActivity.class);
       // startActivity(nextActivity);
    }

    private void initNavigationDrawer() {
        //TODO: improve
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }

    public void searchNearPlaces(View view) {
        view.setVisibility(View.INVISIBLE);
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference meetingsRef = db.collection("meetings");
        Query locations = meetingsRef.whereLessThanOrEqualTo("placeLocation", new GeoPoint(bounds.northeast.latitude, bounds.northeast.longitude)).whereGreaterThanOrEqualTo("placeLocation", new GeoPoint(bounds.southwest.latitude, bounds.southwest.longitude));

        locations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ++times;
                    mMap.clear();
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        meetings.add(document.getData());
                        GeoPoint point = (GeoPoint) document.get("placeLocation");
                        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())));
                        Log.d("Event", times + " - " + point.getLatitude() + " " + point.getLongitude());
                    }

                    if (task.getResult().isEmpty()) Log.d("Event", times + " - NO hay quedadas cerca");
                    /*
                    Nuse pq la primera vez los carga 2 veces... Y es muy raro porque times = 1 en ambos!!
                    Carga un poco más de lo que hay en la pantalla (nuse pq, imagino que los bounds te da algo más grande)
                    */
                }
            }
        });
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        Log.d("HOLA", "HOLA");
        View b = findViewById(R.id.nearPlaces);
        b.setVisibility(View.VISIBLE);
    }

}
