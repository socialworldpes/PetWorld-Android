package com.petworld_madebysocialworld;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

import java.util.Map;

public class SearchFriendsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutSheet;
    private String textTmp;
    private EditText text;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        linearLayoutSheet = (LinearLayout) findViewById(R.id.LayoutMeetings);
        text = (EditText)findViewById(R.id.Search);
        context = this;
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchFriends();
                    return true;
                }
                return false;
            }
        });
    }

    public void searchFriends() {

        textTmp = text.getText().toString();
        if (textTmp != null) {
            int size = textTmp.length();
            char c = textTmp.charAt(size - 1);//returns h
            String next = String.valueOf((char) (c + 1));
            String newName;
            newName = textTmp.substring(0, size - 1) + next;

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference meetingsRef = db.collection("meetings");
            linearLayoutSheet.removeAllViews();
            Query meetingLocations = meetingsRef.whereGreaterThanOrEqualTo("name", textTmp).whereLessThanOrEqualTo("name", newName);
            //Query meetingLocations = meetingsRef.whereLessThanOrEqualTo("name", textTmp + "ZZZZZZZZZZZZZZZZZZZZZZZZ");
            meetingLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> map;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = (String) document.get("name");
                            TextView textViewDescreList = new TextView(context);
                            textViewDescreList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            textViewDescreList.setText("Text: " + name);
                            textViewDescreList.setPadding(40, 20, 40, 20);

                            linearLayoutSheet.addView(textViewDescreList);
                        }
                    }
                }
            });
        }
    }
}
