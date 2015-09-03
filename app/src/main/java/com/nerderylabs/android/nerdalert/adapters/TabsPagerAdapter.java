package com.nerderylabs.android.nerdalert.adapters;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.fragments.BeaconFragment;
import com.nerderylabs.android.nerdalert.fragments.NerdFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    final static private String TAG = TabsPagerAdapter.class.getSimpleName();

    private enum Tabs {
        NERDS(R.string.title_nerds),
        BEACONS(R.string.title_beacons);

        private int resourceId;

        Tabs(int resourceId) {
            this.resourceId = resourceId;
        }
    }

    final private Context context;

    public TabsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return NerdFragment.newInstance(NerdFragment.class);
            case 1:
                return BeaconFragment.newInstance(BeaconFragment.class);
        }

        return null;
    }

    @Override
    public int getCount() {
        return Tabs.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return context.getString(Tabs.NERDS.resourceId);
            case 1:
                return context.getString(Tabs.BEACONS.resourceId);
        }
        return null;
    }

}
