package com.petworld_madebysocialworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Adaptador de routes
 */
public class RoutesAdapter extends ArrayAdapter<Route> {
    public RoutesAdapter(Context context, List<Route> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.list_item_route,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView image = (ImageView) convertView.findViewById(R.id.route_image);
        TextView name = (TextView) convertView.findViewById(R.id.route_name);
        TextView place= (TextView) convertView.findViewById(R.id.route_place);
        TextView description = (TextView) convertView.findViewById(R.id.route_description);
        TextView id = (TextView) convertView.findViewById(R.id.route_id);

        // Route actual.
        Route route = getItem(position);

        // Setup.
        Glide.with(getContext()).load(route.getImage()).into(image);
        name.setText(route.getName());
        place.setText(route.getPlace());
        description.setText(route.getDescription());
        id.setText(route.getId());

        return convertView;
    }
}
