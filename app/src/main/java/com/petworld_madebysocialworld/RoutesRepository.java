package com.petworld_madebysocialworld;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio ficticio de routes
 */
public class RoutesRepository {
    private static volatile RoutesRepository repository = new RoutesRepository();
    private HashMap<String, Route> routes = new HashMap<>();
    private FirebaseFirestore db;
    String userID;

    public synchronized static RoutesRepository getInstance() {
        if (repository == null){ //if there is no instance available... create new one
            repository = new RoutesRepository();
        }
        return repository;
    }

    public static void resetRepository() {
        repository = null;
    }

    private RoutesRepository() {
        LeerRutasUsuario();
    }

    private void saveRoute(Route route) {
        routes.put(route.getId(), route);
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes.values());
    }

    public Route getRoute(String id) { return routes.get(id); }

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
                                            String id = (String) document.getId();
                                            String place = (String) data.get("placeName");
                                            GeoPoint placeLocation = (GeoPoint) data.get("placeLocation");
                                            saveRoute(new Route(name, place, description, R.drawable.mapmarker, placeLocation, id));
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