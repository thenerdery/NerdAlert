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

import com.nerderylabs.android.nerdalert.model.Tabs;
import com.nerderylabs.android.nerdalert.ui.fragment.BaseFragment;
import com.nerderylabs.android.nerdalert.ui.fragment.TabFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    final static private String TAG = TabsPagerAdapter.class.getSimpleName();

    final private Context context;

    public TabsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment tabFragment = BaseFragment.newInstance(TabFragment.class);
        Tabs tab = getTabForIndex(position);
        if (tab != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(TabFragment.TAB_EXTRA, tab);
            tabFragment.setArguments(bundle);
        }
        return tabFragment;
    }

    @Override
    public int getCount() {
        return Tabs.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Tabs tab = getTabForIndex(position);
        if (tab != null) {
            int resourceId = tab.getTitleStringId();
            return context.getString(resourceId);
        }
        return "";
    }

    private Tabs getTabForIndex(int index) {
        for (Tabs tab : Tabs.values()) {
            if (tab.getTabIndex() == index) {
                return tab;
            }
        }
        return null;
    }

}
