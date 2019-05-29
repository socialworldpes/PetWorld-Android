package com.petworld_madebysocialworld.ui.listmywalks;

import Models.Walk;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.petworld_madebysocialworld.FriendsSingleton;
import com.petworld_madebysocialworld.R;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ListMyWalksFragment extends Fragment {

    private Context context;
    private View view;
    private ArrayList<Walk> listWalks;

    public ListMyWalksFragment(Context context, ArrayList<Walk> listWalksFromActivity) {
        this.context = context;
        listWalks = listWalksFromActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_my_walks_fragment, container, false);
        return inflater.inflate(R.layout.list_my_walks_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView walkList = (ListView) view.findViewById(R.id.list);
        ListMyWalksListAdapter customAdapter = new ListMyWalksListAdapter(context, R.layout.list_my_walks_fragment, listWalks);
        walkList.setAdapter(customAdapter);
    }

}
