package com.example.petworld_madebysocialworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public void goToLogIn (View view){
        Intent nextActivity = new Intent(this, MapActivity.class);
        startActivity(nextActivity);
    }
}
