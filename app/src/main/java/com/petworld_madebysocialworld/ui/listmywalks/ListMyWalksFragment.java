package com.petworld_madebysocialworld.ui.listmywalks;

import Models.Walk;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.petworld_madebysocialworld.R;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ListMyWalksFragment extends Fragment {

    private Context context;
    private View view;
    private ArrayList<Walk> listWalks;
    private static String TAG = "ListMyWalksFragment";

    public ListMyWalksFragment(Context context, ArrayList<Walk> listWalksFromActivity) {
        Log.d(TAG, "contructoraFragmentMyWalks");
        this.context = context;
        listWalks = listWalksFromActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "he entrado al oncreate");
        view = inflater.inflate(R.layout.list_my_walks_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        ListView walkList = (ListView) view.findViewById(R.id.listMyWalks);
        ListMyWalksListAdapter customAdapter = new ListMyWalksListAdapter(context, R.layout.list_my_walks_fragment, listWalks);
        walkList.setAdapter(customAdapter);
    }

}