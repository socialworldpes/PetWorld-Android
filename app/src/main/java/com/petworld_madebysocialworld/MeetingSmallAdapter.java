package com.petworld_madebysocialworld;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MeetingSmallAdapter extends RecyclerView.Adapter<MeetingSmallAdapter.MeetingSmallViewHolder> {

    protected LayoutInflater inflater;
    protected Context context;
    private List<HashMap<String,Object>> meetings;

    // ignore
    public MeetingSmallAdapter(Context context, List<HashMap<String,Object>> meetings) {
        this.context = context;
        this.meetings = meetings;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class MeetingSmallViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date, place, description;
        // public ImageView photo;
        // public RatingBar rating;

        public MeetingSmallViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            place = (TextView) itemView.findViewById(R.id.place);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }

    // ignore
    @Override
    public MeetingSmallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.meeting_small, null);
        return new MeetingSmallViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MeetingSmallViewHolder holder, int position) {
        HashMap<String,Object> m = meetings.get(position);

        holder.name.setText((String) m.get("name"));
        holder.date.setText((String) m.get("start"));
        holder.place.setText((String) m.get("placeName"));
        holder.description.setText((String) m.get("description"));
    }

    // ignore
    @Override
    public int getItemCount() {
        return meetings.size();
    }
}