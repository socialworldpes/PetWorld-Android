package com.petworld_madebysocialworld;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.petworld_madebysocialworld.ui.main.MeetingsPagerAdapter;

public class ViewMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meting);
        MeetingsPagerAdapter meetingsPagerAdapter = new MeetingsPagerAdapter(this, getSupportFragmentManager(), getIntent().getStringExtra("id"));
        ViewPager viewPager = findViewById(R.id.view_pager);
        boolean b = meetingsPagerAdapter == null;
        Log.d("ViewMeetingActivity", "Es nulo?  " + b);
        viewPager.setAdapter(meetingsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}