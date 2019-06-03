package com.petworld_madebysocialworld.ui.Participants;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import Models.Friend;
import com.petworld_madebysocialworld.FriendsSingleton;
import com.petworld_madebysocialworld.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ParticipantsListAdapter extends ArrayAdapter<Friend> implements ListAdapter {
    private ArrayList<Friend> participantsListInfo;
    private Context context;

    public ParticipantsListAdapter(Context context, int textViewResourceid, ArrayList<Friend> participantsListInfo) {
        super(context, textViewResourceid);
        this.context = context;
        this.participantsListInfo = participantsListInfo;
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
        return participantsListInfo.size();
    }

    @Override
    public Friend getItem(int position) {
        return participantsListInfo.get(position);
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
        final Friend friendData = participantsListInfo.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.participants_list_row, null);
            final TextView name = convertView.findViewById(R.id.name2);
            ImageView image = convertView.findViewById(R.id.imageView3);
            //Button button = convertView.findViewById(R.id.button);
            name.setText(friendData.getName());
            Picasso.get().load(friendData.getImageURL()).into(image);
            /*button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (removeFriend(friendData.getId())) notifyDataSetChanged();
                    Snackbar.make(v, friendData.getName() + " " + position, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            if (friendData.getId().equals("NoFriends")) button.setVisibility(View.INVISIBLE);*/
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return participantsListInfo.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
