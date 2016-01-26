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

package com.nerderylabs.android.nerdalert.ui.fragment;

import com.nerderylabs.android.nerdalert.Constants;
import com.nerderylabs.android.nerdalert.R;
import com.nerderylabs.android.nerdalert.model.Neighbor;
import com.nerderylabs.android.nerdalert.settings.Settings;
import com.nerderylabs.android.nerdalert.ui.activity.NearbyInterface;
import com.nerderylabs.android.nerdalert.ui.adapter.TabsPagerAdapter;
import com.nerderylabs.android.nerdalert.ui.widget.NoSwipeViewPager;
import com.nerderylabs.android.nerdalert.util.ProfileUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private View view;

    private NoSwipeViewPager viewPager;

    private FloatingActionButton fab;

    private NearbyInterface nearbyInterface;

    private final Neighbor myInfo = new Neighbor();

    private Boolean isProgressIndicatorShowing = false;

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

        restoreUserInformation();

        initializeNametag();

        initializeFab();

        return view;
    }

    public void restoreUserInformation() {
        Context context = getContext();
        String name = Settings.getName(context);
        String tagline = Settings.getTagline(context);

        Pair<String, Bitmap> profile = loadPrivilegedProfileData();

        if (name.isEmpty()) {
            name = profile.first;
        }
        myInfo.setName(name);

        if (tagline.isEmpty()) {
            tagline = ProfileUtil.getDeviceName();
        }
        myInfo.setTagline(tagline);

        Bitmap photo = profile.second;
        if (photo != null) {
            myInfo.setBitmap(profile.second);
        }


    }

    private Pair<String, Bitmap> loadPrivilegedProfileData() {
        // check to see if we have the necessary permissions in the new M permission model
        int permission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CONTACTS);
        // if we don't, then request permission from the user. keep in mind we may never get it...
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "READ_CONTACTS permission not granted.");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    Constants.REQUEST_ASK_PERMISSIONS);
            return new Pair<>(null, null);
        }

        return ProfileUtil.getUserProfile(getContext());
    }

    private void initializeNametag() {
        final EditText nameEditText = (EditText) view.findViewById(R.id.my_name);
        final EditText taglineEditText = (EditText) view.findViewById(R.id.my_tagline);
        final ImageView photoImageView = (ImageView) view.findViewById(R.id.my_photo);

        // restore state
        nameEditText.setText(myInfo.getName());
        taglineEditText.setText(myInfo.getTagline());
        if (myInfo.getBitmap() != null) {
            photoImageView.setImageDrawable(new BitmapDrawable(getResources(), myInfo.getBitmap()));
        } else {
            Drawable photo = ContextCompat.getDrawable(getContext(), R.drawable.ic_contact_photo);
            DrawableCompat
                    .setTint(photo, ContextCompat.getColor(getContext(), R.color.color_primary));
            photoImageView.setImageDrawable(photo);
        }

        // listen for focus change
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    persistNametagValues((TextView) v);
                }
            }
        };

        // listen for the done key
        EditText.OnEditorActionListener doneListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    persistNametagValues(v);
                }
                return false;
            }
        };

        // open profile contact card when user's photo is tapped
        ImageView.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setDataAndType(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                intent.putExtra("finishActivityOnSaveCompleted", true);
                startActivity(intent);
            }
        };

        nameEditText.setOnEditorActionListener(doneListener);
        taglineEditText.setOnEditorActionListener(doneListener);

        nameEditText.setOnFocusChangeListener(focusListener);
        taglineEditText.setOnFocusChangeListener(focusListener);

        photoImageView.setOnClickListener(imageClickListener);

    }

    private void persistNametagValues(TextView view) {
        Context context = getContext();
        // stop publishing if the info has changed
        if (Settings.isPublishing(context)) {
            nearbyInterface.unpublish(myInfo);
            nearbyInterface.unsubscribe();
        }
        switch (view.getId()) {
            case R.id.my_name:
                myInfo.setName(view.getEditableText().toString());
                Settings.setName(context, view.getEditableText().toString());
                break;
            case R.id.my_tagline:
                myInfo.setTagline(view.getEditableText().toString());
                Settings.setTagline(context, view.getEditableText().toString());
                break;
        }
        Log.d(TAG, "myInfo: " + myInfo);
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
        if (Settings.isPublishing(context) || Settings.isSubscribing(context)) {
            startProgressIndicator();
        } else {
            stopProgressIndicator();
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
        if (context != null) {
            // update the UI to reflect our current Nearby state
            if (Settings.isSubscribing(context) || Settings.isPublishing(context)) {
                startProgressIndicator();
            } else {
                stopProgressIndicator();
            }
        }
    }

    // this should only be called by the OnSharedPreferenceChangeListener, to reflect the correct state
    private void startProgressIndicator() {
        if (!isProgressIndicatorShowing) {
            fab.setImageDrawable(
                    ContextCompat.getDrawable(getContext(), R.drawable.progress_indicator));
            Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
            rotate.setRepeatCount(Animation.INFINITE);
            fab.startAnimation(rotate);
            isProgressIndicatorShowing = true;
        }
    }

    // this should only be called by the OnSharedPreferenceChangeListener, to reflect the correct state
    private void stopProgressIndicator() {
        if (isProgressIndicatorShowing) {
            fab.setAnimation(null);
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_nearby));
            isProgressIndicatorShowing = false;
        }
    }
}

