package com.nerderylabs.android.nerdalert.fragments;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.adapters.TabsPagerAdapter;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.settings.Settings;
import com.nerderylabs.android.nerdalert.util.DelayedTextWatcher;
import com.nerderylabs.android.nerdalert.widgets.NoSwipeViewPager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

        setupTextInputs();

        return view;
    }

    private void setupTextInputs() {
        final EditText nameEditText = (EditText) view.findViewById(R.id.my_name);
        final EditText taglineEditText = (EditText) view.findViewById(R.id.my_tagline);

        nameEditText.setText(Settings.getName(getContext()));
        taglineEditText.setText(Settings.getTagline(getContext()));

        // submit buttons are for lamers...
        DelayedTextWatcher watcher = new DelayedTextWatcher(new DelayedTextWatcher.Callback() {
            @Override
            public void afterTextChanged(Editable editableText) {
                if(nameEditText.getEditableText() == editableText) {
                    myInfo.name = editableText.toString();
                    Settings.setName(getContext(), myInfo.name);
                } else if(taglineEditText.getEditableText() == editableText) {
                    myInfo.tagline = editableText.toString();
                    Settings.setTagline(getContext(), myInfo.tagline);
                }
                Log.d(TAG, "myInfo: " + myInfo.toJson());
            }
        });

        nameEditText.addTextChangedListener(watcher);
        taglineEditText.addTextChangedListener(watcher);
    }

}

