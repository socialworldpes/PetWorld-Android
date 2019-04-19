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
 * Adaptador de leads
 */
public class LeadsAdapter extends ArrayAdapter<Lead> {
    public LeadsAdapter(Context context, List<Lead> objects) {
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
                    R.layout.list_item_lead,
                    parent,
                    false);
        }

        // Referencias UI.
        ImageView image = (ImageView) convertView.findViewById(R.id.route_image);
        TextView name = (TextView) convertView.findViewById(R.id.route_name);
        TextView place= (TextView) convertView.findViewById(R.id.route_place);
        TextView description = (TextView) convertView.findViewById(R.id.route_description);

        // Lead actual.
        Lead lead = getItem(position);

        // Setup.
        Glide.with(getContext()).load(lead.getImage()).into(image);
        name.setText(lead.getName());
        place.setText(lead.getPlace());
        description.setText(lead.getDescription());

        return convertView;
    }
}
