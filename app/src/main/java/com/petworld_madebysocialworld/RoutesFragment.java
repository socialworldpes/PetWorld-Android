package com.petworld_madebysocialworld;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


/**
 * Vista para los routes del CRM
 */
public class RoutesFragment extends Fragment {

    ListView mRoutesList;
    ArrayAdapter<Route> mRoutesAdapter;

    public RoutesFragment() {
        // Required empty public constructor
    }

    public static RoutesFragment newInstance(/*parámetros*/) {
        RoutesFragment fragment = new RoutesFragment();
        // Setup parámetros
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Gets parámetros
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_routes, container, false);


        // Inicializar el adaptador con la fuente de datos.
        List<Route> repo = RoutesRepository.getInstance().getRoutes();
        //es buit
        mRoutesAdapter = new RoutesAdapter(getActivity(), repo);

        // Instancia del ListView.
        mRoutesList = (ListView) root.findViewById(R.id.routes_list);

        //Relacionando la lista con el adaptador
        mRoutesList.setAdapter(mRoutesAdapter);

        mRoutesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Route selItem = (Route) parent.getAdapter().getItem(position);
                String passId = selItem.getId();
                Intent i = new Intent(RoutesFragment.this.getActivity(), CreateWalkActivity.class);
                i.putExtra("itemId",passId);
                startActivity(i);
            }
        });
        return root;
    }
}
