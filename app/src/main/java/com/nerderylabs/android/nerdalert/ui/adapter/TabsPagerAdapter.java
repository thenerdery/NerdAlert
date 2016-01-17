/*
 * Copyright (C) 2016 The Nerdery, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nerderylabs.android.nerdalert.ui.adapter;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.ui.fragment.BeaconFragment;
import com.nerderylabs.android.nerdalert.ui.fragment.NerdFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    final static private String TAG = TabsPagerAdapter.class.getSimpleName();

    private enum Tabs {
        NERDS(R.string.title_nerds),
        BEACONS(R.string.title_beacons);

        private final int resourceId;

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
        switch (position) {
            case 0:
                return context.getString(Tabs.NERDS.resourceId);
            case 1:
                return context.getString(Tabs.BEACONS.resourceId);
        }
        return null;
    }

}
