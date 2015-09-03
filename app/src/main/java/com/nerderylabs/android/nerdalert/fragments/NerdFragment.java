package com.nerderylabs.android.nerdalert.fragments;

import com.nerderylabs.android.nerdalert.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NerdFragment extends BaseFragment {

    private static final String TAG = NerdFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nerds, container, false);
    }

}
