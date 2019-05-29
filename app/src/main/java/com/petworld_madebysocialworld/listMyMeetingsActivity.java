package com.petworld_madebysocialworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.petworld_madebysocialworld.ui.listmymeetings.ListMyMeetingsFragment;

public class listMyMeetingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_my_meetings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ListMyMeetingsFragment.newInstance())
                    .commitNow();
        }
    }
}
