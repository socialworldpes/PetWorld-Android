package com.petworld_madebysocialworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


import android.widget.Toast;

import java.io.FileNotFoundException;

public class CreateMeetingActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    boolean isCreating = true, toLocationPicker = false;
    //LatLng  location;
 //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

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
                //ImageView imgView = (ImageView)findViewById(R.id.imageView);
                //imgView.setImageDrawable(bitmapDrawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
