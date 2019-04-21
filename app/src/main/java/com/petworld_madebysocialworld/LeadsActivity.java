package com.petworld_madebysocialworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LeadsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LeadsFragment leadsFragment = (LeadsFragment)
                getSupportFragmentManager().findFragmentById(R.id.leads_container2);

        if (leadsFragment == null) {
            leadsFragment = LeadsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.leads_container2, leadsFragment)
                    .commit();
        }
    }


    private void initNavigationDrawer() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        toolBar.setTitle("PerfilMascota");
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }
}
