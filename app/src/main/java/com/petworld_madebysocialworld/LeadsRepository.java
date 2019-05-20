package com.petworld_madebysocialworld;

import Models.User;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

/**
 * Repositorio ficticio de leads
 */
public class LeadsRepository {
    private static volatile LeadsRepository repository = new LeadsRepository();
    private HashMap<String, Lead> leads = new HashMap<>();
    private FirebaseFirestore db;
    String userID;

    public synchronized static LeadsRepository getInstance() {
        if (repository == null){ //if there is no instance available... create new one
            repository = new LeadsRepository();
        }
        return repository;
    }

    private LeadsRepository() {
        LeerRutasUsuario();
    }

    private void saveLead(Lead lead) {
        leads.put(lead.getId(), lead);
    }

    public List<Lead> getLeads() {
        return new ArrayList<>(leads.values());
    }

    public Lead getLead(String id) { return leads.get(id); }

    private void LeerRutasUsuario() {
        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        db.collection("users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<DocumentReference> alRouteRef = (ArrayList<DocumentReference>) document.get("routes");
                        for (DocumentReference dr: alRouteRef) {
                            db.document(dr.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> data = document.getData();
                                            String description = (String) data.get("description");
                                            String name = (String) data.get("name");
                                            String place = data.get("placeLocation").toString();
                                            saveLead(new Lead(name, place, description, R.drawable.anabohueles));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }

        });
    }
}