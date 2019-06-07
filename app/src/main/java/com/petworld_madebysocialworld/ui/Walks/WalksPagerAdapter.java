package com.petworld_madebysocialworld.ui.Walks;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.Toast;
import com.petworld_madebysocialworld.R;
import com.petworld_madebysocialworld.ui.Participants.ParticipantsFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class WalksPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.InfoMeetings, R.string.ParticipantsMeetings};
    private final Context mContext;
    private Fragment infoMeetingFragment;
    private Fragment participantsFragment;
    private Activity actAux;

    public WalksPagerAdapter(Context context, FragmentManager fm, String id, Activity act) {
        super(fm);
        mContext = context;
        actAux = act;
        infoMeetingFragment = new WalksFragment(mContext, "walks", id);
        participantsFragment = new ParticipantsFragment(mContext, "walks", id);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            return infoMeetingFragment;
        } else if (position == 1) {
            return participantsFragment;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}