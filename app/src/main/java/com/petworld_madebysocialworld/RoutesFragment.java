package com.petworld_madebysocialworld;

import Models.Route;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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

        Log.d("repoRoutes: ", "size: " + repo.size());
        //es buit
        mRoutesAdapter = new RoutesAdapter(getActivity(), repo);

        // Instancia del ListView.
        mRoutesList = (ListView) root.findViewById(R.id.routes_list);

        //Relacionando la lista con el adaptador
        mRoutesList.setAdapter(mRoutesAdapter);

        mRoutesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Route route = (Route) parent.getAdapter().getItem(position);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("routeId",           route.getId());
                returnIntent.putExtra("routeName",         route.getName());
                returnIntent.putExtra("routeDescription",  route.getDescription());
                returnIntent.putExtra("routeLocationName", route.getPlace());
                returnIntent.putExtra("routeImageURL",     route.getImage());
                returnIntent.putExtra("routeLocationPlaceLat",route.getPlaceLocation().getLatitude());
                returnIntent.putExtra("routeLocationPlaceLng",route.getPlaceLocation().getLongitude());

                Activity activity = getActivity();
                activity.setResult(Activity.RESULT_OK,returnIntent);
                activity.finish();
            }
        });
        return root;
    }
}
