package com.nerderylabs.android.nerdalert.fragment;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.activity.NearbyInterface;
import com.nerderylabs.android.nerdalert.adapter.TabsPagerAdapter;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.settings.Settings;
import com.nerderylabs.android.nerdalert.widget.DelayedTextWatcher;
import com.nerderylabs.android.nerdalert.widget.NoSwipeViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    View view;

    FloatingActionButton fab;

    NearbyInterface nearbyInterface;

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

        initializeTextInputs();

        initializeFab();

        return view;
    }

    private void initializeTextInputs() {
        final EditText nameEditText = (EditText) view.findViewById(R.id.my_name);
        final EditText taglineEditText = (EditText) view.findViewById(R.id.my_tagline);

        String name = Settings.getName(getContext());
        String tagline = Settings.getTagline(getContext());

        myInfo.name = name;
        myInfo.tagline = tagline;

        nameEditText.setText(name);
        taglineEditText.setText(tagline);

        // submit buttons are for lamers...
        DelayedTextWatcher watcher = new DelayedTextWatcher(new DelayedTextWatcher.Callback() {
            @Override
            public void afterTextChanged(Editable editableText) {
                Context context = getContext();

                // stop publishing if the info has changed
                if(Settings.isPublishing(context)) {
                    nearbyInterface.unpublish(myInfo);
                }
                if(nameEditText.getEditableText() == editableText) {
                    myInfo.name = editableText.toString();
                    Settings.setName(context, myInfo.name);
                } else if(taglineEditText.getEditableText() == editableText) {
                    myInfo.tagline = editableText.toString();
                    Settings.setTagline(context, myInfo.tagline);
                }

                Log.d(TAG, "myInfo: " + myInfo.toJson());

            }
        });

        nameEditText.addTextChangedListener(watcher);
        taglineEditText.addTextChangedListener(watcher);
    }

    private void initializeFab() {
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (Settings.isPublishing(context) || Settings.isSubscribing(context)) {
                    nearbyInterface.unpublish(myInfo);
                    nearbyInterface.unsubscribe();
                } else {
                    nearbyInterface.publish(myInfo);
                    nearbyInterface.subscribe();
                }
            }
        });

        // first time through.  we shouldn't be active unless something didn't shutdown correctly.
        Context context = getContext();
        if(Settings.isPublishing(context) || Settings.isSubscribing(context)) {
            startSpinner();
        } else {
            stopSpinner();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        nearbyInterface = (NearbyInterface) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Context context = getContext();

        // we might not have a context yet
        if(context != null) {
            // update the UI to reflect our current Nearby state
            if(Settings.isSubscribing(context) || Settings.isPublishing(context)) {
                startSpinner();
            } else {
                stopSpinner();
            }
        }
    }

    // this should only be called by the OnSharedPreferenceChangeListener, to reflect the correct state
    private void startSpinner() {
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_nearby_spinner));
    }

    // this should only be called by the OnSharedPreferenceChangeListener, to reflect the correct state
    private void stopSpinner() {
        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_nearby));
    }
}

