package com.petworld_madebysocialworld.ui.main;

import Models.Friend;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.petworld_madebysocialworld.FriendsSingleton;
import com.petworld_madebysocialworld.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestsListAdapter extends ArrayAdapter<Friend> implements ListAdapter {
    private FriendsSingleton friendsSingleton;
    private ArrayList<Friend> requestsListInfo;
    private Context context;

    public RequestsListAdapter(Context context, int textViewResourceid, ArrayList<Friend> requestsList) {
        super(context, textViewResourceid, requestsList);
        friendsSingleton = FriendsSingleton.getInstance();
        requestsListInfo = friendsSingleton.getRequestsListInfo();
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
    public Friend getItem(int position) {
        return requestsListInfo.get(position);
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
        final Friend friendData = requestsListInfo.get(position);
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
            name.setText(friendData.getName());
            Picasso.get().load(friendData.getImageURL()).into(image);
            acceptBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Se ha añadido " + friendData.getName() + " a tus amigos", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    friendsSingleton.acceptRequest(friendData);
                }
            });
            refuseBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Has rechazado a " + friendData.getName() + " como amigo", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    friendsSingleton.refuseRequest(friendData);
                }
            });
            if (friendData.getId().equals("NoPendingRequests")) {
                acceptBttn.setVisibility(View.INVISIBLE);
                refuseBttn.setVisibility(View.INVISIBLE);
            }
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
