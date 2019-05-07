package com.petworld_madebysocialworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ViewWalkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_walk);

        String exemple = getIntent().getExtras().getString("id");
        //if (exemple == null) Toast.makeText(this, "Error, null", Toast.LENGTH_SHORT).show();
        //else Toast.makeText(this, "El intent es " + exemple, Toast.LENGTH_SHORT).show();
        TextView tv = findViewById(R.id.intentView);
        tv.setText("La id es: " + exemple);

        /*
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
            } else {
                newString= extras.getString("STRING_I_NEED");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }*/

    }
}
