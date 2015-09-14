package com.nerderylabs.android.nerdalert.ui.fragment;

import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.ui.activity.NearbyInterface;
import com.nerderylabs.android.nerdalert.ui.adapter.TabsPagerAdapter;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.settings.Settings;
import com.nerderylabs.android.nerdalert.ui.widget.NoSwipeViewPager;
import com.nerderylabs.android.nerdalert.util.ProfileUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    View view;

    NoSwipeViewPager viewPager;

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
        viewPager = (NoSwipeViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        loadProfileInformation();

        initializeNametag();

        initializeFab();

        return view;
    }

    private void loadProfileInformation() {
        Context context = getContext();
        String name = Settings.getName(context);
        String tagline = Settings.getTagline(context);
        Pair<String, Bitmap> profile = ProfileUtil.getUserProfile(context);

        if(name.isEmpty()) {
            name = profile.first;
        }
        myInfo.setName(name);

        if(tagline.isEmpty()) {
            tagline = ProfileUtil.getDeviceName();
        }
        myInfo.setTagline(tagline);

        Bitmap photo = profile.second;
        if(photo != null) {
            myInfo.setBitmap(profile.second);
        }
    }

    private void initializeNametag() {
        final EditText nameEditText = (EditText) view.findViewById(R.id.my_name);
        final EditText taglineEditText = (EditText) view.findViewById(R.id.my_tagline);
        final ImageView photoImageView = (ImageView) view.findViewById(R.id.my_photo);

        // restore state
        nameEditText.setText(myInfo.getName());
        taglineEditText.setText(myInfo.getTagline());
        if(myInfo.getBitmap() != null) {
            photoImageView.setImageDrawable(new BitmapDrawable(getResources(), myInfo.getBitmap()));
        }

        // listen for changes
        EditText.OnEditorActionListener listener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    Context context = getContext();
                    // stop publishing if the info has changed
                    if(Settings.isPublishing(context)) {
                        nearbyInterface.unpublish(myInfo);
                    }
                    switch(v.getId()) {
                        case R.id.my_name:
                            myInfo.setName(v.getEditableText().toString());
                            Settings.setName(context, v.getEditableText().toString());
                            break;
                        case R.id.my_tagline:
                            myInfo.setTagline(v.getEditableText().toString());
                            Settings.setTagline(context, v.getEditableText().toString());
                            break;
                    }
                }

                Log.d(TAG, "myInfo: " + myInfo.toJson());

                return false;
            }
        };

        nameEditText.setOnEditorActionListener(listener);
        taglineEditText.setOnEditorActionListener(listener);

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

