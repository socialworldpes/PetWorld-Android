package com.petworld_madebysocialworld.ui.listmymeetings;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.petworld_madebysocialworld.R;

public class ListMyMeetingsFragment extends Fragment {

    private ListMyMeetingsViewModel mViewModel;

    public static ListMyMeetingsFragment newInstance() {
        return new ListMyMeetingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_my_meetings_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListMyMeetingsViewModel.class);
        // TODO: Use the ViewModel
    }

}
