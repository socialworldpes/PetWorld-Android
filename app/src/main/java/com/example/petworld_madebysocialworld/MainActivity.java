package com.example.petworld_madebysocialworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigationDrawer();
        //FirebaseApp f = FirebaseApp.initializeApp(this);
        //Object mAuth = FirebaseAuth.getInstance();
        //Log.d("LLEGA", "onCreate se ejecuta");
        //initButtons();
        //initListeners();
        //initializeFireBase();
        //loginFireBase();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //mapa mauri
        /* val mapFragment = supportFragmentManager
                 .findFragmentById(R.id.map) as SupportMapFragment
         mapFragment.getMapAsync(this)*/
    }

    /*
    private void initListeners() {
    }

    private void initButtons() {
    }
    */

    //Function Button Map
    public void goToMap (View view){
        Intent nextActivity = new Intent(this, MapActivity.class);
        startActivity(nextActivity);
    }
    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);

        toolBar.setTitle("Titulo navigaitonbar");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }

}
