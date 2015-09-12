package com.nerderylabs.android.nerdalert.fragment;

import com.nerderylabs.android.nerdalert.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BeaconFragment extends BaseFragment {

    private static final String TAG = BeaconFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_beacons, container, false);
    }

}
