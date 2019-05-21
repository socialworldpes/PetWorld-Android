package com.petworld_madebysocialworld.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.petworld_madebysocialworld.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MeetingsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.InfoMeetings, R.string.ParticipantsMeetings};
    private final Context mContext;
    private Fragment infoMeetingFragment;
    private Fragment participantsFragment;

    public MeetingsPagerAdapter(Context context, FragmentManager fm, String id) {
        super(fm);
        mContext = context;

        infoMeetingFragment = new infoMeetingFragment(mContext, "meetings", id);
        participantsFragment = new ParticipantsFragment(mContext, "meetings", id);
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