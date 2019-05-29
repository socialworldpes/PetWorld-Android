package com.petworld_madebysocialworld.ui.listmywalks;

import Models.Friend;
import Models.Walk;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.Timestamp;
import com.petworld_madebysocialworld.FriendsSingleton;
import com.petworld_madebysocialworld.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.*;

public class ListMyWalksListAdapter implements ListAdapter {
    private Context context;
    private ArrayList<Walk> walkListInfo;
    public ListMyWalksListAdapter(Context context, int textViewResourceid, ArrayList<Walk> walksInfo) {
        walkListInfo = walksInfo;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return walkListInfo.size();
    }

    @Override
    public Walk getItem(int position) {
        return walkListInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //TODO VISTA DE LA LISTA
        final Walk walkData = walkListInfo.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.walks_list_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            final TextView name = convertView.findViewById(R.id.name);
            ImageView image = convertView.findViewById(R.id.imageView);
            final TextView description = convertView.findViewById(R.id.description);
            final TextView start = convertView.findViewById(R.id.start);
            name.setText(walkData.getName());
            description.setText(walkData.getDescription());
            if (walkData.getStart().getSeconds() < Timestamp.now().getSeconds())
                convertView.setBackgroundResource(R.color.grey);
            start.setText(getDateIntoString(walkData.getStart()));
            Picasso.get().load(walkData.getImageURL()).into(image);
        }
        return convertView;
    }

    private String getDateIntoString(Timestamp start) {
        Date date = new Date(start.getSeconds()*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE HH:mm:ss, dd MM yyyy "); // the format of your date
        //TODO HORARIO VERANO O INVIERNO?
        return sdf.format(date);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return walkListInfo.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
