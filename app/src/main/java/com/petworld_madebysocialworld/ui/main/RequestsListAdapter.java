package com.petworld_madebysocialworld.ui.main;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.petworld_madebysocialworld.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

class RequestsListAdapter implements ListAdapter {
    ArrayList<Map<String, String>> requestsListInfo;
    Context context;

    public RequestsListAdapter(Context context, ArrayList<Map<String, String>> arrayList) {
        requestsListInfo = arrayList;
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
        return requestsListInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
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
        final Map<String, String> friendData = requestsListInfo.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.requests_list_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            final TextView name = convertView.findViewById(R.id.name);
            ImageView image = convertView.findViewById(R.id.imageView);
            Button acceptBttn = convertView.findViewById(R.id.acceptBttn);
            Button refuseBttn = convertView.findViewById(R.id.refuseBttn);
            name.setText(friendData.get("name"));
            Picasso.get().load(friendData.get("imageURL")).into(image);
            acceptBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Accept: " + friendData.get("name") + " " + position, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            refuseBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Refuse: " + friendData.get("name") + " " + position, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return requestsListInfo.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}