package com.petworld_madebysocialworld;

import Models.User;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener {

    private static final String TAG = MapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition; //prova

    private ArrayList<Map<String, Object>> meetings = new ArrayList<Map<String, Object>>();
    private ArrayList<Map<String, Object>> routes = new ArrayList<Map<String, Object>>();
    private ArrayList<Map<String, Object>> walks = new ArrayList<Map<String, Object>>();

    private String[] dayOfWeek = new String[] {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

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
    private LatLng selectedLocation = null;
    private TabLayout tabLayout;
    private Context context;
    private Integer position;
    private LinearLayout linearLayoutSheet;


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
        fam.setOnFloatingActionsMenuUpdateListener( new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {

            }

            @Override
            public void onMenuCollapsed() {
                selectedLocation = null;
                //TODO: remove point location
            }
        } );

        tabLayout = (TabLayout) findViewById(R.id.selectTab);
        context = this;
        position = 0;
        linearLayoutSheet = (LinearLayout) findViewById(R.id.LayoutMeetings);
        loadListLayout(position);
        listenerList();
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

                selectedLocation = point;
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
        if(selectedLocation == null) newMeeting();
        else newMeeting(selectedLocation);
    }

    public void newRoute(View view){
        if(selectedLocation == null) newRoute();
        else newRoute(selectedLocation);
    }

    public void newWalk(View view){
        if(selectedLocation == null) newWalk();
        else newWalk(selectedLocation);
    }

    public void newMeeting(){
        Intent intent = new Intent(MapActivity.this, CreateMeetingActivity.class);
        startActivity(intent);
    }

    public void newWalk(){
        Intent intent = new Intent(MapActivity.this, LeadsActivity.class);
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
        final LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Meetings
        final CollectionReference meetingsRef = db.collection("meetings");
        Query meetingLocations = meetingsRef.whereLessThanOrEqualTo("placeLocation", new GeoPoint(bounds.northeast.latitude, bounds.northeast.longitude)).whereGreaterThanOrEqualTo("placeLocation", new GeoPoint(bounds.southwest.latitude, bounds.southwest.longitude));

        // Query Walks
        final CollectionReference walksRef = db.collection("walks");
        final Query walkLocations = walksRef.whereLessThanOrEqualTo("placeLocation", new GeoPoint(bounds.northeast.latitude, bounds.northeast.longitude)).whereGreaterThanOrEqualTo("placeLocation", new GeoPoint(bounds.southwest.latitude, bounds.southwest.longitude));

        // Query Routes
        final CollectionReference routesRef = db.collection("routes");
        final Query routeLocations = routesRef.whereLessThanOrEqualTo("placeLocation", new GeoPoint(bounds.northeast.latitude, bounds.northeast.longitude)).whereGreaterThanOrEqualTo("placeLocation", new GeoPoint(bounds.southwest.latitude, bounds.southwest.longitude));

        mMap.clear();

        meetingLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    meetings.clear();
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        GeoPoint point = (GeoPoint) document.get("placeLocation");
                        String name = (String) document.get("name");
                        Timestamp date = (Timestamp) document.get("start");
                        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toDate().toInstant(), ZoneId.systemDefault());

                        if (checkConditions(point, bounds, dateTime) > 0) {

                        meetings.add(document.getData());
                        meetingAndWalkMarker(point, dateTime, "Meeting");
                        mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).title(name)).showInfoWindow();
                        Log.d("Meeting", "Lat: " + point.getLatitude() + " Long:" + point.getLongitude());
                        }

                    }

                    if (task.getResult().isEmpty()) Log.d("Meeting", "NO hay quedadas cerca");
                }

                // Its important for Walks to be queried first!
                walkLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            walks.clear();
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                GeoPoint point = (GeoPoint) document.get("placeLocation");
                                Timestamp date = (Timestamp) document.get("start");
                                LocalDateTime dateTime = LocalDateTime.ofInstant(date.toDate().toInstant(), ZoneId.systemDefault());

                                if (checkConditions(point, bounds, dateTime) > 0) {
                                walks.add(document.getData());
                                meetingAndWalkMarker(point, dateTime, "Walk");
                                Log.d("Walk", "Lat: " + point.getLatitude() + " Long:" + point.getLongitude());
                                }

                            }

                            if (task.getResult().isEmpty()) Log.d("Walk", "NO hay paseos cerca");
                        }

                        routeLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    routes.clear();
                                    for (QueryDocumentSnapshot document: task.getResult()) {
                                        GeoPoint point = (GeoPoint) document.get("placeLocation");

                                        if (checkConditions(point, bounds, null) > 0 && hasWalk(document.getId()) < 0) {
                                        routes.add(document.getData());
                                        routeMarker(point);
                                        Log.d("Route", "Lat: " + point.getLatitude() + " Long:" + point.getLongitude());
                                        }

                                    }

                                    if (task.getResult().isEmpty()) Log.d("Route", "NO hay rutas cerca");
                                }
                            }
                        });
                    }
                });
            }
        });

        loadListLayout(position);

    }

    public int checkConditions(GeoPoint point, LatLngBounds bounds, LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        // Only shows the points of the following 7 days
        LocalDateTime weekFromToday = now.plusWeeks(1);

        if (point.getLongitude() <= bounds.northeast.longitude &&
                point.getLongitude() >= bounds.southwest.longitude &&
                (dateTime == null || (now.compareTo(dateTime) <= 0 && dateTime.compareTo(weekFromToday) <= 0))) return 1;
        return 0;
    }

    public int hasWalk(String routeId) {
        for (Map<String, Object> walk : walks) {
            if (walk.get("walkForRoute").toString().equals(routeId)) return 1;
        }
        return -1;
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        Log.d("HOLA", "HOLA");
        View b = findViewById(R.id.nearPlaces);
        b.setVisibility(View.VISIBLE);
    }

    public void addMarker(GeoPoint point, Bitmap bmp) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(point.getLatitude(), point.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                // Specifies the anchor to be at a particular point in the marker image.
                .anchor(0.5f, 0.5f));
    }

    public static boolean isToday(LocalDate date) {
        if (date.isEqual(LocalDate.now())) return true;
        return false;
    }

    public void meetingAndWalkMarker(GeoPoint point, LocalDateTime date, String type) {
        LinearLayout linearLayout = (LinearLayout) this.getLayoutInflater().inflate(type.equals("Meeting") ? R.layout.meeting_marker : R.layout.walk_marker, null, false);

        TextView layoutDate = linearLayout.findViewById(R.id.date);

        String day = isToday(date.toLocalDate()) ? "Hoy" : dayOfWeek[date.getDayOfWeek().getValue() - 1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        layoutDate.setText(day + " " + date.format(formatter));

        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());;

        linearLayout.setDrawingCacheEnabled(true);
        linearLayout.buildDrawingCache();
        Bitmap bmp = linearLayout.getDrawingCache();

        addMarker(point, bmp);
    }

    public void routeMarker(GeoPoint point) {
        LinearLayout linearLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.route_marker, null, false);

        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());;

        linearLayout.setDrawingCacheEnabled(true);
        linearLayout.buildDrawingCache();
        Bitmap bmp = linearLayout.getDrawingCache();

        addMarker(point, bmp);
    }

    private void listenerList() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                //Toast.makeText(MapActivity.this, "Position es " + position, Toast.LENGTH_SHORT).show();
                loadListLayout(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadListLayout(Integer position) {
        linearLayoutSheet.removeAllViews();
        if (position == 0) {
            if (meetings.size() != 0){

                for(Map<String, Object> mapTmp : meetings) {

                    LinearLayout linearLayoutList = new LinearLayout(context);

                    String nameList = (String) mapTmp.get("name");

                    TextView textViewNameList = new TextView(context);
                    textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textViewNameList.setText(nameList);
                    textViewNameList.setTextColor(Color.BLACK);
                    textViewNameList.setTextSize(1, 20);
                    textViewNameList.setPadding(40, 20, 40, 5);

                    linearLayoutList.addView(textViewNameList);

                    Timestamp timeList = (Timestamp) mapTmp.get("start");
                    Date timeDateList = timeList.toDate();
                    Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timeStringList = formatter.format(timeDateList);

                    TextView textViewTime = new TextView(context);
                    textViewTime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textViewTime.setText(timeStringList);
                    textViewTime.setPadding(40, 5, 40, 20);

                    linearLayoutList.addView(textViewTime);

                    linearLayoutSheet.addView(linearLayoutList);

                    String descriptionList = (String) mapTmp.get("description");

                    TextView textViewDescreList = new TextView(context);
                    textViewDescreList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textViewDescreList.setText(descriptionList);
                    textViewDescreList.setPadding(40, 20, 40, 20);

                    linearLayoutSheet.addView(textViewDescreList);

                }
            } else {
                TextView textViewAvis = new TextView(context);
                textViewAvis.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                textViewAvis.setText("No hay quedadas disponibles");
                textViewAvis.setPadding(40, 40, 40, 20);
                linearLayoutSheet.addView(textViewAvis);

                TextView textViewSolucio = new TextView(context);
                textViewSolucio.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                textViewSolucio.setText("Actualiza el mapa");
                textViewSolucio.setTextColor(Color.BLACK);
                textViewSolucio.setTextSize(1, 18);
                textViewSolucio.setPadding(40, 20, 40, 40);
                linearLayoutSheet.addView(textViewSolucio);
            }
        } else if (position == 1) {
            if (routes.size() != 0) {

                for(Map<String, Object> mapTmp : routes) {

                    LinearLayout linearLayoutList = new LinearLayout(context);
                    String nameList = (String) mapTmp.get("name");
                    String descriptionList = (String) mapTmp.get("description");

                    TextView textViewNameList = new TextView(context);
                    textViewNameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textViewNameList.setText(nameList);
                    textViewNameList.setTextColor(Color.BLACK);
                    textViewNameList.setTextSize(1, 20);
                    textViewNameList.setPadding(40, 20, 40, 5);

                    linearLayoutList.addView(textViewNameList);


                    linearLayoutSheet.addView(linearLayoutList);

                    //Obtenir Descripcio
                    TextView textViewDescriList = new TextView(context);
                    textViewDescriList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    textViewDescriList.setText(descriptionList);
                    textViewDescriList.setPadding(40, 20, 40, 20);

                    linearLayoutSheet.addView(textViewDescriList);
                }
            } else {
                TextView textViewAvis = new TextView(context);
                textViewAvis.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                textViewAvis.setText("No hay rutas disponibles");
                textViewAvis.setPadding(40, 40, 40, 20);
                linearLayoutSheet.addView(textViewAvis);

                TextView textViewSolucio = new TextView(context);
                textViewSolucio.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                textViewSolucio.setText("Actualiza el mapa");
                textViewSolucio.setTextColor(Color.BLACK);
                textViewSolucio.setTextSize(1, 18);
                textViewSolucio.setPadding(40, 20, 40, 40);
                linearLayoutSheet.addView(textViewSolucio);
            }
        } else {
            Toast.makeText(this, "Error al LoadList", Toast.LENGTH_SHORT).show();
        }
    }


}

