package com.example.petworld_madebysocialworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    public void goToLogIn (View view){
        Intent nextActivity = new Intent(this, MainActivity.class);
        startActivity(nextActivity);
    }

    public void goToUserProfile (View view){
        Intent nextActivity = new Intent(this, UserActivity.class);
        startActivity(nextActivity);
    }


}
