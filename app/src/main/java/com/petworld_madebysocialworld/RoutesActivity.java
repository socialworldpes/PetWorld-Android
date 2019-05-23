package com.petworld_madebysocialworld;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RoutesActivity extends AppCompatActivity {
    RoutesFragment routesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        routesFragment = (RoutesFragment) getSupportFragmentManager().findFragmentById(R.id.routes_container2);

        if (routesFragment == null) {
            routesFragment = RoutesFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.routes_container2, routesFragment)
                    .commit();
        }
    }

}