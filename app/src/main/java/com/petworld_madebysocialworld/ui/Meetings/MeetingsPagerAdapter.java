package com.petworld_madebysocialworld.ui.Meetings;

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
public class MeetingsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.InfoMeetings, R.string.ParticipantsMeetings};
    private final Context mContext;
    private Fragment infoMeetingFragment;
    private Fragment participantsFragment;
    private Activity actAux;

    public MeetingsPagerAdapter(Context context, FragmentManager fm, String id, Activity act, boolean visibilityFabButton1, boolean visibilityFabButton2) {
        super(fm);
        mContext = context;
        actAux = act;
        infoMeetingFragment = new infoMeetingFragment(mContext, "meetings", id, visibilityFabButton1);
        participantsFragment = new ParticipantsFragment(mContext, "meetings", id, visibilityFabButton2);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            actAux.findViewById(R.id.inviteParticipantsMeeting).setVisibility(View.GONE);
            actAux.findViewById(R.id.JoinMeeting).setVisibility(View.VISIBLE);
            //Toast.makeText(actAux, "EYYYYY", Toast.LENGTH_LONG).show();
            return infoMeetingFragment;
        } else if (position == 1) {
            //Toast.makeText(actAux, "OHHHH", Toast.LENGTH_LONG).show();

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