package com.petworld_madebysocialworld;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import android.widget.Toast;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CreateEventActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    boolean isCreating = true, toLocationPicker = false;
    //LatLng  location;
 //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        /*
        location = (LatLng) getIntent().getParcelableExtra("location");
        TextView tvLat = (TextView)findViewById(R.id.coordLat);
        tvLat.setText("Lat: " + location.latitude);
        TextView tvLong = (TextView)findViewById(R.id.coordLong);
        tvLong.setText("Long: " + location.longitude);
        */
    }

    public void loadImage(View view)
    {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        startActivityForResult(getIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            Uri selectedImage = data.getData();
            Toast.makeText(this, "Imatge Pillada + URI: " + selectedImage, Toast.LENGTH_SHORT).show();
            try {
                Toast.makeText(this, "Dins Try", Toast.LENGTH_SHORT).show();
                Bitmap  bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
                ImageView imgView = (ImageView)findViewById(R.id.imageView);
                imgView.setImageDrawable(bitmapDrawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
