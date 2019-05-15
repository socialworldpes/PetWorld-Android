package com.petworld_madebysocialworld.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.petworld_madebysocialworld.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.Friends, R.string.Requests};
    private final Context mContext;
    private Fragment friendsFragment, requestsFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        iniFriendsFragment();
        iniRequestsFragment();
    }

    private void iniFriendsFragment() {
        // OBTENGO ARRAY ArrayList<Map<String, String>> friendsListInfo
        friendsFragment = new FriendsFragment(mContext);
    }

    private void iniRequestsFragment() {
        // OBTENGO ARRAY ArrayList<Map<String, String>> friendsListInfo
        requestsFragment = new RequestsFragment(mContext);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            return friendsFragment;
        } else if (position == 1) {
            return requestsFragment;
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