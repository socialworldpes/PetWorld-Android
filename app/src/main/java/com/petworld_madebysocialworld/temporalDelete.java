package com.petworld_madebysocialworld;

import Models.User;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class temporalDelete {
    private String userID;
    private FirebaseFirestore db;

    public temporalDelete() {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
    }

    public void deleteMeeting(final String meetingPath) {

        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<DocumentReference> alMeetingRef = (ArrayList<DocumentReference>) document.get("meetings");
                        for (DocumentReference dr : alMeetingRef) {
                            if (dr.getPath().equals(meetingPath)) {
                                //borra en meetings/
                                dr.delete();
                                //borra referencia en users/meetings
                                document.getReference().update("meetings", FieldValue.arrayRemove(dr));
                                break;
                            }
                        }
                    }
                }

            }

        });
    }

    public void deleteRoutes(final String routePath) {

        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<DocumentReference> alMeetingRef = (ArrayList<DocumentReference>) document.get("routes");
                        for (DocumentReference dr : alMeetingRef) {
                            if (dr.getPath().equals(routePath)) {
                                //borra en routes/
                                dr.delete();
                                //borra referencia en users/routes
                                document.getReference().update("routes", FieldValue.arrayRemove(dr));
                                break;
                            }
                        }
                    }
                }

            }

        });
    }

    public void deleteWalks(final String walkPath) {

        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<DocumentReference> alWalkRef = (ArrayList<DocumentReference>) document.get("walks");
                        for (DocumentReference dr : alWalkRef) {
                            if (dr.getPath().equals(walkPath)) {
                                //borra en walks/
                                dr.delete();
                                //borra referencia en users/walks
                                document.getReference().update("walks", FieldValue.arrayRemove(dr));
                                break;
                            }
                        }
                    }
                }

            }

        });
    }
}
