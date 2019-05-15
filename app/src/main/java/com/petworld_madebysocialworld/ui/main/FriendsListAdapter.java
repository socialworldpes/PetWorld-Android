package com.petworld_madebysocialworld.ui.main;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.petworld_madebysocialworld.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class FriendsListAdapter extends ArrayAdapter<Map<String, String>> implements ListAdapter {
    ArrayList<Map<String, String>> friendsListInfo;
    Context context;

    public FriendsListAdapter(Context context, int textViewResourceid, ArrayList<Map<String, String>> arrayList) {
        super(context, textViewResourceid, arrayList);
        friendsListInfo = arrayList;
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
        return friendsListInfo.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return friendsListInfo.get(position);
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
        final Map<String, String> friendData = friendsListInfo.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.friends_list_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            final TextView name = convertView.findViewById(R.id.name);
            ImageView image = convertView.findViewById(R.id.imageView);
            Button button = convertView.findViewById(R.id.button);
            name.setText(friendData.get("name"));
            Picasso.get().load(friendData.get("imageURL")).into(image);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (removeFriend(friendData.get("id"))) notifyDataSetChanged();
                    Snackbar.make(v, friendData.get("name") + " " + position, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        return convertView;
    }

    public boolean addFriend(Map<String, String> friend) {
        // ADD EN FIREBASE
        // ADD AND REFRESH VIEW
        return friendsListInfo.add(friend);
    }

    public boolean removeFriend(String newfriendId) {
        for (int i = 0; i < friendsListInfo.size(); ++i) {
            // TEST - NO SE SI friendsListInfo.remove(i) != null se cumple
            if (friendsListInfo.get(i).get("id").equals(newfriendId) && friendsListInfo.remove(i) != null) {
                // DELETE FROM FIREBASE
                // REFRESH VIEW
                return true;
            }
        }

        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return friendsListInfo.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
