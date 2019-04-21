package com.petworld_madebysocialworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

import static java.security.AccessController.getContext;

public class CreateWalkActivity extends AppCompatActivity {

    LeadsAdapter mLeadsAdapter;
    List mLeadsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_walk);

        initNavigationDrawer();

        Button but = findViewById(R.id.elegirRuta);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateWalkActivity.this, LeadsActivity.class));
            }
        });

        View leadSelected = findViewById(R.id.leadSelected);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("itemId");
            Lead leadToShow = LeadsRepository.getInstance().getLead(id);

            ImageView image = (ImageView) leadSelected.findViewById(R.id.route_image);
            TextView name = (TextView) leadSelected.findViewById(R.id.route_name);
            TextView place= (TextView) leadSelected.findViewById(R.id.route_place);
            TextView description = (TextView) leadSelected.findViewById(R.id.route_description);
            TextView idTextView = (TextView) leadSelected.findViewById(R.id.route_id);

            Glide.with(this).load(leadToShow.getImage()).into(image);
            name.setText(leadToShow.getName());
            place.setText(leadToShow.getPlace());
            description.setText(leadToShow.getDescription());
            idTextView.setText(leadToShow.getId());
        }
    }

    private void initNavigationDrawer() {
        //TODO: improve
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        DrawerUtil.getDrawer(this,toolBar);
    }
}
