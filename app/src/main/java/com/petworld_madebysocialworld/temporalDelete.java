package com.petworld_madebysocialworld;

import Models.User;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class temporalDelete {
    String userID;
    FirebaseFirestore db;

    public temporalDelete() {
        userID = User.getInstance().getAccount().getId();
        db = FirebaseFirestore.getInstance();
    }

    public void borrarMeeting(final String meetingPath) {

        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<DocumentReference> alMeetingRef = (ArrayList<DocumentReference>) document.get("meetings");
                        for (DocumentReference dr : alMeetingRef) {
                            if (dr.getPath().equals(meetingPath)) {
                                dr.delete(); //borra en meetings/
                                document.getReference().update("pets", FieldValue.arrayRemove(dr)); //borra en users/meetings
                                break;
                            }
                        }
                    }
                }

            }

        });
    }
}
