package com.petworld_madebysocialworld;

import Models.User;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class CreateWalkActivity extends AppCompatActivity {

    TextView date;
    TextView hourmin;
    private FirebaseFirestore db;
    private String userID;
    private String path;
    DocumentReference res;
    Lead leadToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_walk);

        //initNavigationDrawer();
        db = FirebaseFirestore.getInstance();
        userID = User.getInstance().getAccount().getId();

        Button but = findViewById(R.id.elegirRuta);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateWalkActivity.this, LeadsActivity.class));
            }
        });

        Button selectDate = findViewById(R.id.elegirFecha);
        date = findViewById(R.id.textDate);

        selectDate.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateWalkActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        date.setText(day + "/" + (month+1) + "/" + year);
                                    }
                                }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        Button selectHour = findViewById(R.id.elegirHora);
        hourmin = findViewById(R.id.textHour);

        selectHour.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int min = calendar.get(Calendar.MINUTE);

            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateWalkActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                hourmin.setText(hour + ":" + min);
                            }
                        }, hour, min, true);

                //timePickerDialog.getTimePicker().setMinDate(System.currentTimeMillis());
                timePickerDialog.show();
            }
        });


        Button createPaseo = findViewById(R.id.createWalk);
        createPaseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", "onClick: In");
                TextView dateToFB = findViewById(R.id.textDate);
                Map<String, Object> docData = new HashMap<>();
                docData.put("date",dateToFB.getText().toString());
                docData.put("name",leadToShow.getName());
                docData.put("description",leadToShow.getDescription());
                docData.put("place",leadToShow.getPlace());


                Log.d("Debug", "onClick: docData");
                Task resTask = db.collection("walks").add(docData);
                Log.d("Debug", "onClick: docData");
                resTask.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("Debug", "onClick: If");
                            res = task.getResult();
                            Log.d("Debug", "onClick: Result");
                            //path = res.getPath();
                            Log.d("Debug", "onClick: Path");
                            db.collection("users").document(userID).get().addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                          @Override
                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                              if (task.isSuccessful()) {
                                                  DocumentSnapshot toRead = task.getResult();
                                                  Map<String,Object> data= Objects.requireNonNull(toRead).getData();
                                                  ArrayList walks = (ArrayList) Objects.requireNonNull(data).get("walks");
                                                  walks.add(res);
                                                  Map<String, Object> pathMap = new HashMap<>();
                                                  pathMap.put("walks", walks);
                                                  db.collection("users").document(userID).update(pathMap);
                                                  Log.d("Debug", "onClick: " +data.toString());
                                              }
                                          }
                                    });
                        }
                    }
                });
                startActivity(new Intent(CreateWalkActivity.this, MapActivity.class));
            }
        });


        View leadSelected = findViewById(R.id.leadSelected);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("itemId");
            leadToShow = LeadsRepository.getInstance().getLead(id);

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
