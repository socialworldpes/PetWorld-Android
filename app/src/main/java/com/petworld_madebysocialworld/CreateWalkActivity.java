package com.petworld_madebysocialworld;

import Models.User;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class CreateWalkActivity extends AppCompatActivity {

    TextView date;
    private FirebaseFirestore db;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_walk);

        initNavigationDrawer();
        db = FirebaseFirestore.getInstance();
        userID = User.getInstance().getAccount().getId();

        Button but = findViewById(R.id.elegirRuta);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateWalkActivity.this, LeadsActivity.class));
            }
        });

        Button selectDate = findViewById(R.id.elegirHora);
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

        Button createPaseo = findViewById(R.id.createWalk);
        createPaseo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", "onClick: In");
                TextView dateToFB = findViewById(R.id.textDate);
                Map<String, Object> docData = new HashMap<>();
                docData.put("date",dateToFB.getText().toString());

                Log.d("Debug", "onClick: docData");
                DocumentReference res = db.collection("walks").add(docData).getResult();
                Log.d("Debug", "onClick: Result");
                String path = res.getPath();
                Log.d("Debug", "onClick: Path");
                db.collection("users/"+userID+"/routes").add(path);
                Log.d("Debug", "onClick: Done");

                startActivity(new Intent(CreateWalkActivity.this, MapActivity.class));
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
