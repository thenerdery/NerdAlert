package com.nerderylabs.android.nerdalert.fragments;

import com.google.android.gms.common.api.GoogleApiClient;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.adapters.TabsPagerAdapter;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.widgets.NoSwipeViewPager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    View view;

    GoogleApiClient googleApiClient;

    Neighbor myInfo = new Neighbor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use a retained fragment to avoid re-publishing or re-subscribing upon orientation changes
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getContext(),
                getActivity().getSupportFragmentManager());
        NoSwipeViewPager viewPager = (NoSwipeViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }


}

