package com.petworld_madebysocialworld.ui.Walks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.petworld_madebysocialworld.*;
import com.petworld_madebysocialworld.R;
import com.petworld_madebysocialworld.ui.main.SectionsPagerAdapter;
import android.support.v7.widget.Toolbar;


import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressLint("ValidFragment")
public class WalksFragment extends Fragment {


    //fireStore
    private FirebaseFirestore db;

    //id's
    String idWalk;
    String idRoute;
    DocumentReference routeDocumentReference;

    //walk info
    String name;
    String description;
    GregorianCalendar date;
    private ArrayList<String> imageUrls;
    private Date pickedDate;



    //layout
    EditText nameWalkEditText;
    EditText descriptionWalkEditText;
    EditText nameRouteEditText;

    // Date Formatter & Hour Formatter
    private java.text.DateFormat df;
    private java.text.DateFormat hf;



    //buttons
    Button editButton;
    Button deleteButton;
    private Button dateInput;
    private Button hourInput;

    private View view;
    private FragmentActivity myContext;

    public WalksFragment (Context context, String collection, String id){
        this.idWalk = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_view_walk_fragment_info, container, false);


        create();

        return view;
    }

    private void create() {

                initVariables();
                initLayout();
                initButtons();
                initListeners();
                initFireBase();

                readWalkInfo();

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    private void initListeners() {
        nameRouteEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRoute();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWalk();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWalk();
            }
        });
    }

    private void deleteWalk() {
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private void deleteWalkFireBase() {
        final String id = idWalk;

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        ArrayList<DocumentReference> alWalksRef = (ArrayList<DocumentReference>) document.get("walks");
                        for (DocumentReference dr : alWalksRef) {

                            if (dr.getPath().equals("walks/" + id)) {

                                //borra en routes/
                                dr.delete();
                                //borra referencia en users/routes
                                document.getReference().update("walks", FieldValue.arrayRemove(dr));

                            }
                        }
                    }
                }
                Toast.makeText(view.getContext(), "Paseo Borrado",
                        Toast.LENGTH_LONG).show();
                startMap();

            }

        });
    }

    private void editWalk() {
        Intent intent = new Intent (view.getContext(), EditWalkActivity.class);
        intent.putExtra("idWalk", idWalk);
        startActivityForResult(intent, 0);

    }

    private void initButtons() {
        editButton = view.findViewById(R.id.editButton);
        deleteButton = view.findViewById(R.id.deleteButton);
    }

    private void initLayout() {
        nameWalkEditText = view.findViewById(R.id.nameWalkEditText);
        descriptionWalkEditText = view.findViewById(R.id.descrptionWalkEditText);
        nameRouteEditText = view.findViewById(R.id.nameRoutekEditText);
        //data
        dateInput = view.findViewById(R.id.dateInput);
        hourInput = view.findViewById(R.id.hourInput);
    }

    private void initFireBase() {
        db = FirebaseFirestore.getInstance();

    }

    private void setLayoutText() {
        nameWalkEditText.setText(name);
        descriptionWalkEditText.setText(description);
        dateInput.setText(df.format(pickedDate));
        hourInput.setText(hf.format(pickedDate));
    }


    private void readWalkInfo() {
        DocumentReference docRef = db.collection("walks").document(idWalk);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    name = (String) result.get("name");
                    description = (String) result.get("description");
                    imageUrls = (ArrayList<String>)result.get("images");
                    routeDocumentReference = (DocumentReference) result.get("route");
                    com.google.firebase.Timestamp time = (com.google.firebase.Timestamp) result.get("start");
                    pickedDate = time.toDate();


                    //images
                    setImages();
                    setLayoutText();
                    readRouteInfo();
                }
            }
        });
    }

    private void readRouteInfo() {


        routeDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    nameRouteEditText.setText((String) result.get("name"));
                    idRoute = result.getId();


                }
            }
        });

    }

    private void goRoute() {
        Intent nextActivity = new Intent(view.getContext(), ViewRouteActivity.class);
        nextActivity.putExtra("id", idRoute);
        startActivity(nextActivity);
    }

    private void setImages() {
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(view.getContext(), imageUrls);
        viewPager.setAdapter(adapter);
    }

    private void initVariables() {
        idWalk =  getActivity().getIntent().getExtras().getString("idWalk");
        // init formatter
        df = new android.text.format.DateFormat().getMediumDateFormat(view.getContext());
        hf = new android.text.format.DateFormat().getTimeFormat(view.getContext());
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(view.getContext())
                //set message, title, and icon
                .setTitle("Borrar")
                .setMessage("¿Borrar Paseo?")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code

                        deleteWalkFireBase();
                        startMap();
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void startMap() {
        Intent intent = new Intent (view.getContext(), MapActivity.class);
        startActivityForResult(intent, 0);
    }
}