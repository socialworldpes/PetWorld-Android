package com.petworld_madebysocialworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.text.Html;
import android.widget.TextView;
import com.google.android.gms.maps.model.*;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.*;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateRouteActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //map
    private GoogleMap map;

    //images
    ArrayList<Bitmap> images;
    ArrayList<Uri> uriImages;
    ArrayList<String> urlImages;

    //booleans
    private boolean imagesCanContinue;

    // line of points
    private List<LatLng> path = new ArrayList<LatLng>();
    Polyline pathPolyline;

    // layout
    private EditText descriptionInput;
    private EditText nameInput;
    private EditText locationNameInput;
    private Button btnAddRoute;
    private Button btnUploadImage;

    //market googeMaps
    private List<Marker> myMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        initFireBase();
        initLayout();
        initVariables();
        initListeners();
        setUpMap();
    }


    private void initLayout() {
        descriptionInput = findViewById(R.id.descriptionInput);
        nameInput = findViewById(R.id.nameInput);
        locationNameInput = findViewById(R.id.locationNameInput);

        btnAddRoute = findViewById(R.id.createButton);
        btnUploadImage = findViewById(R.id.uploadImagesButton);
    }

    private void initVariables() {
        imagesCanContinue = false;
        images = new ArrayList<>();
        uriImages = new ArrayList<>();
        urlImages = new ArrayList<>();
        myMarkers =  new ArrayList<>();

        // path variable
        LatLng location = getIntent().getParcelableExtra("location");
        if (location == null) location =  new LatLng(41.3818, 2.1685);
        path.add(location);
    }

    private void initListeners() {
        btnAddRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoute();
            }
        });
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();

            }
        });
    }

    private void refreshImageView() {

        for (Uri uri: uriImages)
            urlImages.add(uri.toString());

        ViewPager viewPager= findViewById(R.id.viewPager);
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
                        refreshImageView();
                    }
                    break;
                }
        }
    }

    private void startMap() {
        Intent intent = new Intent (getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }

    private void createRoute() {
        HashMap<String, Object> route =  new HashMap<String, Object>();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Read fields
        route.put("creator", userID);
        route.put("description", descriptionInput.getText().toString());
        route.put("name", nameInput.getText().toString());
        route.put("placeName", locationNameInput.getText().toString());
        route.put("images", Arrays.asList());

        List<GeoPoint> geoPointList = parsePath(path);
        route.put("path", geoPointList);
        route.put("placeLocation", geoPointList.get(0));

        addRouteToFireBase(route);
    }

    //parse the LatLng to GeoPoint of the List
    private List<GeoPoint> parsePath(List<LatLng> latLngList) {
        List<GeoPoint> geoPointList = new ArrayList<GeoPoint>();

        for (LatLng ll: latLngList) {
            geoPointList.add(new GeoPoint(ll.latitude, ll.longitude));
        }

        return geoPointList;
    }

    private void addRouteToFireBase(HashMap<String, Object> route) {
        db.collection("routes").add(route).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {

                //ojo, ahora hay que guardar las fotos en su sitio y ponerlas en firebase RECOGER LINK y añadir a lugar correspondiente
                final DocumentReference docRAux = documentReference;
                // do something with result.
                Log.d("PRUEBA004", "Antes de entrar en el for");
                for (int i = 0; i < uriImages.size(); i++) {
                    Log.d("PRUEBA005", "Después de entrar en el for");
                    final int j = i;
                    final StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("routes/" + documentReference.getId() + "_" + i);
                    Uri file = uriImages.get(i);
                    Log.d("PRUEBA006", "Cojo la urii");

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
                                Log.d("PRUEBA007", "routes/" + documentReference.getId() + "_" + j);
                                urlImages.add(task.getResult().toString());
                                Log.d("Tamaño url", String.valueOf(urlImages.size()));
                                docRAux.update("images", urlImages);
                            } else {
                                // Handle failures
                                // ...
                            }
                        }

                    });
                }

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                addRouteRefToUser(documentReference, userID);
            }

        });
    }

    private void addRouteRefToUser(final DocumentReference documentReference, String userID) {
        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentSnapshot result = task.getResult();
                    ArrayList<DocumentReference> arrayReference = (ArrayList<DocumentReference>) result.get("routes");
                    if (arrayReference == null) arrayReference = new ArrayList<>();
                    arrayReference.add(documentReference);

                    //añadir ruta a users(userID)
                    db.collection("users").document(userID)
                            .update("routes", arrayReference)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) { Log.d("route", "DocumentSnapshot successfully written!"); }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) { Log.w("route", "Error writing document", e); }
                            });
                } else {
                    Log.w("task ko", "Error getting documents.", task.getException());
                }
                //Toast.makeText(getApplicationContext(), "Ruta creada", Toast.LENGTH_LONG).show();
                startMap();
            }
        });
    }






    // -------------- MAP FUNCTIONS ------------------- //

    private void setUpMap() {

        //Toast.makeText(this, "Mapa listo", Toast.LENGTH_SHORT).show();

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapCreateRoute)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                map = googleMap;
                CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(path.get(0), (float) 16);
                map.moveCamera(cameraupdate);
                map.addMarker(new MarkerOptions()
                        .position(path.get(0))
                        //.draggable(true)
                        .anchor((float) 0.5, (float) 0.5)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue))
                );

                //TODO: improve custom layout
                //set adapter for custom window info
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        View v = getLayoutInflater().inflate(R.layout.marker_info, null);

                        TextView title = (TextView) v.findViewById(R.id.title);
                        TextView info= (TextView) v.findViewById(R.id.info);

                        title.setText("Borrar Punto");
                        info.setText(Html.fromHtml("<font color='red' size = '6'>"+ "X"+"</font>"));

                        return v;
                    }
                });
                //delete point when click windows info
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        for (Marker myMarker: myMarkers) {
                            if(marker.equals(myMarker)) {
                                //remove mark
                                myMarkers.remove(marker);
                                myMarker.remove();
                                //remove point
                                int i = findPointIndex(marker.getPosition());
                                path.remove(i);
                                refreshPolyLine();
                                break;
                            }
                        }
                    }
                });

                //show info when click mark
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        for(Marker myMarker: myMarkers)
                            if (marker.equals(myMarker)) {
                                myMarker.showInfoWindow();
                                return true;
                            }
                        return false;
                    }
                });

                // map line
                pathPolyline = map.addPolyline(new PolylineOptions()
                        .add(path.get(0))
                        .width(20)
                        .color(Color.parseColor("#2D9CDB")));

                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng newPoint) {
                        LatLng nearestPoint = findNearestPoint(newPoint);

                        if(areSamePoint(nearestPoint, newPoint)){
                            mapRemovePoint(nearestPoint);
                        }else{
                            mapAppendPoint(newPoint);
                            addMark(newPoint, googleMap);
                        }
                    }
                });

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng newPoint) {
                        LatLng nearestPoint = findNearestPoint(newPoint);

                        if(areSamePoint(nearestPoint, newPoint)){
                            mapRemovePoint(nearestPoint);
                        }else{
                            mapAppendPoint(newPoint);
                            addMark(newPoint, googleMap);
                        }
                    }
                });
            }
        });
    }

    private int findPointIndex(LatLng position) {
        int i = -1;
        for (LatLng point: path) {
            i++;
            if (point.equals(position)) break;
        }

        return i;
    }

    private void addMark(LatLng newPoint, GoogleMap googleMap) {
        Marker mark = googleMap.addMarker(new MarkerOptions()
                    .position(newPoint)
                    .title("Borrar Punto")
                    .snippet("X")
                    //.draggable(true)
                    .anchor((float) 0.5, (float) 0.5)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green))
        );
        myMarkers.add(mark);
    }

    private boolean areSamePoint(LatLng p1, LatLng p2){
        if (p1 == null || p2 == null) return false;
        // TODO: instead of 100 use a dp distance depending on the zoom level
        return false;
        //return dist(p1, p2) <= 100;
    }

    private LatLng findNearestPoint( LatLng p){

        float[] distance =  new float[1];
        float[] distanceNearestPoint =  new float[1];

        LatLng nearestPoint = null;

        for (LatLng point : path) {
            if (nearestPoint != null) {
                Location.distanceBetween(p.latitude, p.longitude, nearestPoint.latitude, nearestPoint.longitude, distanceNearestPoint);
                Location.distanceBetween(p.latitude, p.longitude, point.latitude, point.longitude, distance);

                if (distanceNearestPoint[0] > distance[0] ) nearestPoint = point;
            }
            else nearestPoint = point;
        }


        if (path.isEmpty()) return null;
        // TODO: calculate the closest point
        return nearestPoint;
    }

    private boolean mapRemovePoint(LatLng p){
        return path.remove(p);
    }

    private void mapAppendPoint(LatLng newPoint){
        path.add(newPoint);
        //map.clear();

        pathPolyline.setPoints(path);
    }

    private void refreshPolyLine() {
        pathPolyline.setPoints(path);
    }
}
