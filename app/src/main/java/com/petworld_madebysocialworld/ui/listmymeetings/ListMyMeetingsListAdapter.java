package com.petworld_madebysocialworld.ui.listmymeetings;

import Models.Meeting;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.Timestamp;
import com.petworld_madebysocialworld.R;
import com.petworld_madebysocialworld.ViewMeetingActivity;
import com.petworld_madebysocialworld.listMyWalksActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.*;

public class ListMyMeetingsListAdapter extends ArrayAdapter<Meeting> implements ListAdapter {

    private Context context;
    private ArrayList<Meeting> meetingListInfo;
    final private static String TAG = "ListMyWalksListAdapter";

    public ListMyMeetingsListAdapter(Context context, int textViewResourceid, ArrayList<Meeting> meetingsInfo) {
        super(context, textViewResourceid, meetingsInfo);
        this.meetingListInfo = meetingsInfo;
        Log.d(TAG, String.valueOf(meetingListInfo.size()));
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
        Log.d(TAG, String.valueOf(meetingListInfo.size())); return meetingListInfo.size();
    }

    @Override
    public Meeting getItem(int position) {
        return meetingListInfo.get(position);
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
        Log.d(TAG, "He entrado en getView");
        final Meeting meetingData = meetingListInfo.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.meetings_list_row, null);
            final View auxView = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(auxView.getContext(), ViewMeetingActivity.class);
                    intent.putExtra("id", meetingData.getId());
                    auxView.getContext().startActivity(intent);
                }
            });
            final TextView name = convertView.findViewById(R.id.name);
            ImageView image = convertView.findViewById(R.id.imageView);
            final TextView description = convertView.findViewById(R.id.description);
            final TextView start = convertView.findViewById(R.id.start);
            name.setText(meetingData.getName());
            description.setText(meetingData.getDescription());
            if (meetingData.getStart().getSeconds() < Timestamp.now().getSeconds())
                convertView.setBackgroundResource(R.color.grey);
            start.setText(getDateIntoString(meetingData.getStart()));
            Picasso.get().load(meetingData.getImageURL()).into(image);
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
        return meetingListInfo.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
