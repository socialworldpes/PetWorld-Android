package com.petworld_madebysocialworld.ui.listmymeetings;

import Models.Meeting;
import Models.Walk;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.petworld_madebysocialworld.R;
import com.petworld_madebysocialworld.ui.listmymeetings.ListMyMeetingsListAdapter;
import com.petworld_madebysocialworld.ui.listmywalks.ListMyWalksListAdapter;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ListMyMeetingsFragment extends Fragment {

    private Context context;
    private View view;
    private ArrayList<Meeting> listMeetings;
    private static String TAG = "ListMyMeetingsFragment";

    public ListMyMeetingsFragment(Context context, ArrayList<Meeting> listMeetingsFromActivity) {
        this.context = context;
        listMeetings = listMeetingsFromActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_my_meetings_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView walkList = (ListView) view.findViewById(R.id.listMyMeetings);
        ListMyMeetingsListAdapter customAdapter = new ListMyMeetingsListAdapter(context, R.layout.list_my_meetings_fragment, listMeetings);
        walkList.setAdapter(customAdapter);
    }

}